package com.xcvi.micros.ui.destination.food.details

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.NumberPicker
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.StreamingText
import com.xcvi.micros.ui.core.rememberShakeOffset
import com.xcvi.micros.ui.destination.Destination
import com.xcvi.micros.ui.destination.Food
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

    OnNavigation {
        viewModel.getData(meal, date, barcode, amount)
    }

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }

    viewModel.state.portion?.let { portion ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = portion.name) },
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

                item { StreamingText(portion.macros.toString()) }
                item {
                    NumberPicker(
                        initialValue = amount,
                        onValueChange = viewModel::updateNumberPickerValue,
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
                                        Food.Meal(date = date, meal = meal)
                                    ) {
                                        popUpTo(Food.label) {
                                            inclusive = false
                                        }
                                    }
                                }
                            )
                        }
                    ) {
                        Text(text = "Ok")
                    }
                }
            }
        }
    }
}