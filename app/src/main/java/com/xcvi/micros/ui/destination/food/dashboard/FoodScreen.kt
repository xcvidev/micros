package com.xcvi.micros.ui.destination.food.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.DateSelector
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.PastOrPresentSelectableDates
import com.xcvi.micros.ui.core.StreamingText
import com.xcvi.micros.ui.core.getLocalDate
import com.xcvi.micros.ui.core.getLocalDateTime
import com.xcvi.micros.ui.core.getNow
import com.xcvi.micros.ui.core.getToday
import com.xcvi.micros.ui.core.monthFormatted
import com.xcvi.micros.ui.destination.FoodGraph
import com.xcvi.micros.ui.destination.food.SummaryCard
import org.koin.androidx.compose.koinViewModel

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
            topBar = {
                TopAppBar(
                    title = {
                        Text("Food Log")
                    },
                )
            },
        ) { padding ->

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, top = padding.calculateTopPadding()),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                item(span = StaggeredGridItemSpan.FullLine) {
                    DateSelector(
                        currentDate = viewModel.state.date,
                        onDateChanged = { date ->
                            viewModel.setDate(date)
                        },
                        horizontalPadding = 24.dp
                    )
                }

                item(span = StaggeredGridItemSpan.FullLine) {

                    SummaryCard(
                        calories = summary.calories.toInt(),
                        protein = summary.macros.protein,
                        carbs = summary.macros.carbs,
                        fats = summary.macros.fats
                    )
                }
                val meals = viewModel.state.meals
                items(meals.keys.toList()) { index ->
                    Card(
                        onClick = {
                            navController.navigate(
                                FoodGraph.Meal(
                                    meal = index,
                                    date = viewModel.state.date
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Meal $index",
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (meals[index]?.isNotEmpty() == true) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            meals[index]?.forEach { portion ->
                                Text(
                                    text = portion.name,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }

    }
}






