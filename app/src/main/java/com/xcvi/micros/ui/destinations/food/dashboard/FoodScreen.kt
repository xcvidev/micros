package com.xcvi.micros.ui.destinations.food.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.data.entity.AminoAcids
import com.xcvi.micros.data.entity.Macros
import com.xcvi.micros.data.entity.Minerals
import com.xcvi.micros.data.entity.Portion
import com.xcvi.micros.data.entity.Vitamins
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.domain.summary
import com.xcvi.micros.ui.core.DateSelector
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destinations.FoodGraph
import com.xcvi.micros.ui.destinations.food.FoodSummaryCard
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FoodScreen(
    navController: NavHostController,
    viewModel: FoodViewModel = koinViewModel(),
    topAppBarTitle: String,
    aminoTitle: String ,
    macroTitle: String,
    mineralTitle: String,
    vitaminTitle: String ,
) {
    val state = viewModel.state
    OnNavigation {
        viewModel.getData(getToday())
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(topAppBarTitle)
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
                    currentDate = state.date,
                    onDateChanged = { date ->
                        viewModel.setDate(date)
                    },
                    horizontalPadding = 24.dp
                )
                Spacer(modifier = Modifier.height(36.dp))
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                FoodSummaryCard(
                    calories = state.summary?.macros?.calories?.toInt()?: 0,
                    protein = state.summary?.macros?.protein?: 0.0,
                    carbs = state.summary?.macros?.carbohydrates?: 0.0,
                    fats = state.summary?.macros?.fats ?: 0.0,
                    proteinLabel = stringResource(R.string.protein),
                    carbsLabel = stringResource(R.string.carbs),
                    fatsLabel = stringResource(R.string.fats),
                )
                Spacer(modifier = Modifier.height(36.dp))

            }
            val meals = state.meals
            items(meals.keys.toList()) { index ->
                var mealList = ""
                meals[index]?.forEach { portion ->
                    mealList += portion.name + "\n"
                }
                MealCard(
                    mealLabel = stringResource(R.string.meal),
                    meal = index,
                    portions = meals[index] ?: emptyList(),
                ) {
                    if (meals[index]?.isEmpty() == true) {
                        navController.navigate(
                            FoodGraph.Add(
                                meal = index,
                                date = state.date,
                            )
                        )
                    } else {
                        navController.navigate(
                            FoodGraph.Meal(
                                meal = index,
                                date = state.date
                            )
                        )
                    }
                }
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                MicrosSection(
                    macros = state.summary?.macros ?: Macros(),
                    minerals = state.summary?.minerals ?: Minerals(),
                    vitamins = state.summary?.vitamins ?: Vitamins(),
                    aminoAcids = state.summary?.aminoAcids ?: AminoAcids(),
                    modifier = Modifier.padding(horizontal = 8.dp),
                    aminoTitle = aminoTitle,
                    macroTitle = macroTitle,
                    mineralTitle = mineralTitle,
                    vitaminTitle = vitaminTitle,


                )
            }
            item(span = StaggeredGridItemSpan.FullLine) { // nav bar
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }

}




@Composable
fun MealCard(
    meal: Int,
    portions: List<Portion>,
    modifier: Modifier = Modifier,
    mealLabel: String,
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
                text = "$mealLabel $meal",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))
            if (portions.isNotEmpty()) {
                val calories = portions.summary().macros.calories.roundToInt().toString() + " kcal"
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
                Spacer(modifier = Modifier.height(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}



