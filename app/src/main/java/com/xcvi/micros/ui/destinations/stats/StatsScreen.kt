package com.xcvi.micros.ui.destinations.stats

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
import com.xcvi.micros.data.entity.Portion
import com.xcvi.micros.domain.WeightStats
import com.xcvi.micros.ui.core.DropDownChip
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destinations.food.FoodSummary
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = koinViewModel(),
    bottomBarPadding: Dp,
    topAppBarTitle: String ,
    noFoodDataText: String ,
    noWeightDataText: String,
    maxLabel: String ,
    minLabel: String,
    avgLabel: String,
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

            var foods by remember { mutableStateOf(Portion()) }
            var weights by remember { mutableStateOf(WeightStats()) }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                FoodGraph(
                    data = if (selectedFilter == "Week") state.foodsByWeek else state.foodsByMonth,
                    showDate = selectedFilter == "Week",
                    noDataText = noFoodDataText,
                    onScroll = {
                        foods = it
                    }
                )
            }
            FoodSummary(
                calories = foods.macros.calories.roundToInt(),
                protein = foods.macros.protein,
                carbs = foods.macros.carbohydrates,
                fats = foods.macros.fats,
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
                    data = if (selectedFilter == "Week") state.weightsByWeek else state.weightsByMonth,
                    showDate = selectedFilter == "Week",
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


