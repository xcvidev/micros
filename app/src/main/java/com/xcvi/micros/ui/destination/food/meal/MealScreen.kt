package com.xcvi.micros.ui.destination.food.meal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destination.Food
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

    val mealName = when (meal) {
        1 -> "Breakfast"
        2 -> "Lunch"
        3 -> "Dinner"
        4 -> "Snack 1"
        5 -> "Snack 2"
        6 -> "Snack 3"
        else -> ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = mealName) },
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
                    navController.navigate(Food.Add(meal = meal, date = date))
                }
            ) {
                Icon(Icons.Default.Add, "")
            }
        }
    ) {
        val portions = viewModel.state.portions
        val summary = viewModel.state.summary
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                ListItem(
                    headlineContent = {
                        Text(text = "Calories: ${summary?.calories ?: 0}")
                    },
                    supportingContent = {
                        Column {
                            Text(text = "Protein: ${summary?.macros?.protein ?: 0}")
                            Text(text = "Carbs: ${summary?.macros?.carbs ?: 0}")
                            Text(text = "Fats: ${summary?.macros?.fats ?: 0}")
                        }
                    }
                )
            }

            item {
                Row(
                    modifier = modifier.fillParentMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            TODO()
                        }
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
                            Food.Details(
                                meal = meal,
                                date = date,
                                amount = item.amount.roundToInt()
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
                                Text("-")
                            }
                            IconButton(onClick = { viewModel.increasePortion(item) }) {
                                Text("+")
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}











