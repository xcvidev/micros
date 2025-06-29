package com.xcvi.micros.ui.destinations.food.meal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destinations.FoodGraph
import com.xcvi.micros.ui.destinations.food.SummaryCard
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen(
    date: Int,
    meal: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MealViewModel = koinViewModel()
) {
    OnNavigation {
        viewModel.getData(date = date, meal = meal)
    }
    val state = viewModel.state
    var showInputDialog by remember { mutableStateOf(false) }
    if (showInputDialog) {
        InputDialog(
            title = "Enter meal name",
            placeholder = "Meal name",
            onDismiss = { showInputDialog = false },
            onConfirm = { name ->
                viewModel.saveCustomMeal(name)
                showInputDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Meal $meal") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                }
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = {
                    navController.navigate(FoodGraph.Add(meal = meal, date = date))
                }
            ) {
                Icon(Icons.Default.Add, "")
            }
        }
    ) {
        val portions = state.portions
        val summary = state.summary
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                SummaryCard(
                    calories = summary?.calories?.roundToInt() ?: 0,
                    protein = summary?.macros?.protein ?: 0.0,
                    carbs = summary?.macros?.carbs ?: 0.0,
                    fats = summary?.macros?.fats ?: 0.0
                )
            }

            item {
                Row(
                    modifier = modifier.fillParentMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {showInputDialog = true }
                    ) {
                        Text(text = "Save as custom meal")
                    }
                }
            }
            itemsIndexed(portions) { index, item ->
                HorizontalDivider()
                ListItem(
                    modifier = modifier.clickable {
                        navController.navigate(
                            FoodGraph.Details(
                                meal = meal,
                                date = date,
                                amount = item.amount.roundToInt(),
                                barcode = item.barcode
                            )
                        )
                    },
                    headlineContent = {
                        Text(text = item.name)
                    },
                    supportingContent = {
                        Text(text = "${item.calories} kcal, ${item.amount}g")
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { viewModel.decreasePortion(item) }) {
                                Text("-", fontSize = 24.sp)
                            }
                            IconButton(onClick = { viewModel.increasePortion(item) }) {
                                Text("+",fontSize = 24.sp)
                            }
                        }
                    }
                )
            }
            item {
                HorizontalDivider()
            }
        }
    }
}


@Composable
fun InputDialog(
    title: String,
    placeholder: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
                Text(text = "Ok")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Card{
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(text = placeholder) },
                    singleLine = true,
                    maxLines = 1,
                    keyboardActions = KeyboardActions(onDone = { onConfirm(name) }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            }
        }
    )

}










