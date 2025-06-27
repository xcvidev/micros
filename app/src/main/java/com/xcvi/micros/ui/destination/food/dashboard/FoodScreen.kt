package com.xcvi.micros.ui.destination.food.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoMeals
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.ui.core.DateSelector
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.getToday
import com.xcvi.micros.ui.destination.Food
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FoodScreen(
    navController: NavHostController,
    bottomBarPadding: Dp,
    modifier: Modifier = Modifier,
    viewModel: FoodViewModel = koinViewModel()
) {

    OnNavigation {
        viewModel.getData(getToday())
    }

    viewModel.state.summary?.let { summary ->
        Scaffold(
            modifier = modifier.padding(bottom = bottomBarPadding),
            topBar = {
                TopAppBar(
                    title = {
                        DateSelector(
                            currentDate = viewModel.state.date,
                            onDateChanged = {
                                viewModel.setDate(it)
                            },
                            horizontalPadding = 24.dp
                        )
                    }
                )
            },
        ) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    MacrosCard(summary)
                }
                item {
                    Row(modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        MealButton(
                            calories = summary.calories,
                            meal = 1,
                            onClick = {
                                navController.navigate(
                                    Food.Meal(
                                        date = summary.date,
                                        meal = 1
                                    )
                                )
                            })
                        MealButton(calories = summary.calories, meal = 2, onClick = {
                            navController.navigate(Food.Meal(date = summary.date, meal = 2))
                        })
                        MealButton(calories = summary.calories, meal = 3, onClick = {
                            navController.navigate(Food.Meal(date = summary.date, meal = 3))
                        })
                    }
                }
                item {
                    Row(modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        MealButton(calories = summary.calories, meal = 4, onClick = {
                            navController.navigate(Food.Meal(date = summary.date, meal = 4))
                        })
                        MealButton(calories = summary.calories, meal = 5, onClick = {
                            navController.navigate(Food.Meal(date = summary.date, meal = 5))
                        })
                        MealButton(calories = summary.calories, meal = 6, onClick = {
                            navController.navigate(Food.Meal(date = summary.date, meal = 6))
                        })
                    }
                }

                item {
                    MicrosCard(summary)
                }

            }
        }
    }
}


@Composable
fun MacrosCard(
    portion: Portion,
    modifier: Modifier = Modifier
) {
    Card {
        ListItem(
            headlineContent = {
                Column {
                    Text(text = "Protein: ${portion.macros.protein} g")
                    Text(text = "Carbs: ${portion.macros.carbs} g")
                    Text(text = "Fats: ${portion.macros.fats} g")
                }
            },
            trailingContent = {
                Text(
                    text = portion.calories.roundToInt().toString() + " kcal",
                    fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun MicrosCard(
    summary: Portion,
    modifier: Modifier = Modifier
) {
    Card {
        Column {
            Text(text = "Protein: ${summary.macros.protein} g")
            Text(text = "Carbs: ${summary.macros.carbs} g")
            Text(text = "Fats: ${summary.macros.fats} g")
            Text(text = "Calories: ${summary.calories} kcal")
            Text(text = "Vitamins: ${summary.vitamins}")
            Text(text = "Minerals: ${summary.minerals}")
            Text(text = "Amino acids: ${summary.aminoAcids}")
        }
    }
}


@Composable
fun MealButton(
    calories: Double,
    meal: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(calories > 0){
            Button(
                onClick = onClick,
            ) {
                Text(text = "$calories kcal")
            }
        } else {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            ) {
                Icon(
                    imageVector = Icons.Default.NoMeals,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface
                    )
            }
        }
        Text(text = "Meal $meal", textAlign = TextAlign.Center)
    }
}














