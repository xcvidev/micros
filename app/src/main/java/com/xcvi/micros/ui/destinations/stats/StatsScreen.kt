package com.xcvi.micros.ui.destinations.stats

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.domain.Weight
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
    topAppBarTitle: String = "Stats",
    noFoodDataText: String = "No food data.",
    noWeightDataText: String = "No measured weights.",
    maxLabel: String = "Max",
    minLabel: String = "Min",
    avgLabel: String = "Avg"
) {
    OnNavigation {
        viewModel.getData()
    }

    var selectedFilter by remember { mutableStateOf("Week") }
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
                            options = listOf("Week", "Month"),
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
            var weights by remember { mutableStateOf(emptyList<Weight>()) }

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
                calories = foods.calories.roundToInt(),
                protein = foods.macros.protein,
                carbs = foods.macros.carbs,
                fats = foods.macros.fats,
                modifier = Modifier.padding(bottom = 36.dp, start = 8.dp, end = 8.dp),
                backgroundColor = Color.Transparent
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
                modifier = Modifier.weight(0.5f).padding(top = 12.dp)
            )
        }
    }

}


