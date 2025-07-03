package com.xcvi.micros.ui.destinations.food.f1

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
import com.xcvi.micros.data.food.model.MealCard
import com.xcvi.micros.data.food.model.entity.AminoAcids
import com.xcvi.micros.data.food.model.entity.Macros
import com.xcvi.micros.data.food.model.entity.Minerals
import com.xcvi.micros.data.food.model.entity.Vitamins
import com.xcvi.micros.ui.core.DateSelector
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destinations.FoodGraph
import com.xcvi.micros.ui.destinations.food.FoodSummaryCard
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    //date: Int,
    navController: NavHostController,
    viewModel: FoodViewModel,
    topAppBarTitle: String,
    aminoTitle: String ,
    macroTitle: String,
    mineralTitle: String,
    vitaminTitle: String ,
) {
    val state = viewModel.state
    OnNavigation {
        viewModel.getData()
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
                    currentDate = state.currentDate,
                    onDateChanged = { date ->
                        viewModel.setDate(date)
                    },
                    horizontalPadding = 24.dp
                )
                Spacer(modifier = Modifier.height(36.dp))
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                FoodSummaryCard(
                    calories = state.totalSummary?.macros?.calories?.roundToInt() ?: 0,
                    protein = state.totalSummary?.macros?.protein?: 0.0,
                    carbs = state.totalSummary?.macros?.carbohydrates?: 0.0,
                    fats = state.totalSummary?.macros?.fats ?: 0.0,
                    proteinLabel = stringResource(R.string.protein),
                    carbsLabel = stringResource(R.string.carbs),
                    fatsLabel = stringResource(R.string.fats),
                )
                Spacer(modifier = Modifier.height(36.dp))

            }
            val meals = state.meals
            items(meals) { mealCard ->
                MealCard(
                    mealLabel = stringResource(R.string.meal),
                    mealCard = mealCard
                ) {
                    if (mealCard.portions.isEmpty()) {
                        navController.navigate(
                            FoodGraph.Add(
                                meal = mealCard.meal,
                                date = state.currentDate,
                            )
                        )
                    } else {
                        navController.navigate(
                            FoodGraph.Meal(
                                meal = mealCard.meal,
                                date = state.currentDate
                            )
                        )
                    }
                }
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                MicrosSection(
                    macros = state.totalSummary?.macros ?: Macros(),
                    minerals = state.totalSummary?.minerals ?: Minerals(),
                    vitamins = state.totalSummary?.vitamins ?: Vitamins(),
                    aminoAcids = state.totalSummary?.aminoAcids ?: AminoAcids(),
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
    mealCard: MealCard,
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
                text = "$mealLabel ${mealCard.meal}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))
            if (mealCard.portions.isNotEmpty()) {
                val calories = "${mealCard.summary.macros.calories.roundToInt()} kcal"
                Text(
                    text = calories,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    mealCard.portions.forEach { portion ->
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



