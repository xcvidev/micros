package com.xcvi.micros.ui.destinations.food.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.NumberPicker
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.rememberShakeOffset
import com.xcvi.micros.ui.destinations.FoodGraph
import com.xcvi.micros.ui.destinations.food.add.PortionItem
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    date: Int,
    meal: Int,
    amount: Int,
    barcode: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = koinViewModel()
) {
    val state=viewModel.state
    OnNavigation {
        viewModel.getData(meal, date, barcode, amount)
    }

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }

    state.portion?.let { portion ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {  },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .offset(x = shakeOffset),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                item {
                    Text(text = portion.name, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                }
                item {
                    NumberPicker(
                        initialValue = amount,
                        onValueChange = viewModel::updateNumberPickerValue,
                        clickGranularity = 1
                    )
                }
                item {
                    Button(
                        onClick = {
                            viewModel.eat(
                                date = date,
                                meal = meal,
                                onFailure = {
                                    shakeTrigger = true
                                },
                                onSuccess = {
                                    navController.navigate(
                                        FoodGraph.Meal(date = date, meal = meal)
                                    ) {
                                        popUpTo(FoodGraph.Meal(date = date, meal = meal)) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                    ) {
                        Text(text = "Ok")
                    }
                }
                item{
                    PortionItem(
                        portion = portion,
                        streamContent = false
                    ) {
                    }
                }
            }
        }
    }
}