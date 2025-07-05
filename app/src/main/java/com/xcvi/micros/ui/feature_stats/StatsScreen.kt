package com.xcvi.micros.ui.feature_stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import com.xcvi.micros.data.food.FoodStats
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.weight.model.WeightStats
import com.xcvi.micros.ui.core.DropDownChip
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.feature_food.FoodSummary
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = koinViewModel(),
    bottomBarPadding: Dp,
    topAppBarTitle: String = stringResource(R.string.stats),
    noFoodDataText: String = stringResource(R.string.no_food_data),
    noWeightDataText : String= stringResource(R.string.no_measured_weights),
    maxLabel: String = stringResource(R.string.max),
    minLabel: String = stringResource(R.string.min),
    avgLabel: String = stringResource(R.string.avg)
) {
    OnNavigation {
        viewModel.getData()
    }

    val weeksFilter = stringResource(R.string.filter1)
    val monthsFilter = stringResource(R.string.filter2)
    var selectedFilter by remember { mutableStateOf(weeksFilter) }
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = topAppBarTitle)
                },
                actions = {
                    if (viewModel.state.hasData) {
                        DropDownChip(
                            options = listOf(weeksFilter, monthsFilter),
                            selectedOption = selectedFilter,
                            onOptionSelected = {
                                selectedFilter = it
                            }
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = bottomBarPadding.minus(20.dp)
                ),
        ) {

            var foods by remember { mutableStateOf(FoodStats(0, 0.0, 0.0, 0.0,0.0)) }
            var weights by remember { mutableStateOf(WeightStats()) }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                FoodGraph(
                    data = if (selectedFilter == weeksFilter) state.foodsByWeek else state.foodsByMonth,
                    showDate = selectedFilter == weeksFilter,
                    noDataText = noFoodDataText,
                    onScroll = {
                        foods = it
                    }
                )
            }
            FoodSummary(
                calories = foods.calories.roundToInt(),
                protein = foods.protein,
                carbs = foods.carbohydrates,
                fats = foods.fats,
                modifier = Modifier.padding(bottom = 36.dp, start = 8.dp, end = 8.dp, top = 12.dp),
                backgroundColor = Color.Transparent,
                proteinLabel = stringResource(R.string.protein),
                carbsLabel = stringResource(R.string.carbs),
                fatsLabel = stringResource(R.string.fats),
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                WeightGraph(
                    data = if (selectedFilter == weeksFilter) state.weightsByWeek else state.weightsByMonth,
                    showDate = selectedFilter == weeksFilter,
                    noDataText = noWeightDataText,
                    onScroll = { weights = it }
                )
            }
            WeightSummary(
                list = weights,
                maxLabel = maxLabel,
                minLabel = minLabel,
                avgLabel = avgLabel,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(top = 12.dp)
            )
        }
    }

}


