package com.xcvi.micros.ui.destinations.food.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.domain.summary
import com.xcvi.micros.ui.core.DateSelector
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destinations.FoodGraph
import com.xcvi.micros.ui.destinations.food.SummaryCard
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
    val state= viewModel.state
    OnNavigation {
        viewModel.getData(getToday())
    }

    state.summary?.let { summary ->
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
                    Spacer(modifier = Modifier.height(24.dp))
                    DateSelector(
                        currentDate = state.date,
                        onDateChanged = { date ->
                            viewModel.setDate(date)
                        },
                        horizontalPadding = 24.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(24.dp))
                    SummaryCard(
                        calories = summary.calories.toInt(),
                        protein = summary.macros.protein,
                        carbs = summary.macros.carbs,
                        fats = summary.macros.fats
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                val meals = state.meals
                items(meals.keys.toList()) { index ->
                    var mealList = ""
                    meals[index]?.forEach { portion ->
                        mealList += portion.name + "\n"
                    }
                    MealCard(
                        meal = index,
                        portions = meals[index] ?: emptyList(),
                    ) {
                        navController.navigate(
                            FoodGraph.Meal(
                                meal = index,
                                date = state.date
                            )
                        )
                    }
                }
                item(span = StaggeredGridItemSpan.FullLine) {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }

    }
}


@Composable
fun MealCard(
    meal: Int,
    portions: List<Portion>,
    modifier: Modifier = Modifier,
    placeholder: String = "No meals, Tap to add",
    onClick: () -> Unit = {}
) {
    Card(
        onClick = { onClick() },
        modifier = modifier,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Meal $meal",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))
            if (portions.isNotEmpty()) {
                val calories = portions.summary().calories.roundToInt().toString() + " kcal"
                Text(
                    text = calories,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    portions.forEach { portion ->
                        Text(
                            text = portion.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                Text(
                    text = "0 kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                // Action Button
                TextButton (
                    onClick = { onClick() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun M3CardWithMedia(
    modifier: Modifier = Modifier,
    headline: String = "Display small",
    subhead: String = "Subhead",
    supportingText: String = "Explain more about the topic in the display and subhead through supporting text.",
    onActionClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subhead,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder for media
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Action")
            }
        }
    }
}




