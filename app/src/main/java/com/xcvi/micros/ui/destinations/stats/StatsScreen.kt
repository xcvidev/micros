package com.xcvi.micros.ui.destinations.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.domain.formatEpochDate
import com.xcvi.micros.domain.getLocalDate
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.domain.normalize
import com.xcvi.micros.domain.roundDecimals
import com.xcvi.micros.ui.core.BarGraph
import com.xcvi.micros.ui.core.DotGraph
import com.xcvi.micros.ui.core.DropDownChip
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destinations.food.SummaryCard
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    bottomBarPadding: Dp,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = koinViewModel()
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
                    Text(text = "Trends")
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
        LazyColumn(
            modifier = modifier.padding(
                bottom = bottomBarPadding,
                top = padding.calculateTopPadding()
            ),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)
        ) {
            item {
                var selectedIndex by remember { mutableStateOf(0) }
                if (state.foodsByWeek.isEmpty() || state.foodsByMonth.isEmpty()) {
                    Box {
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            DotGraph(
                                yAxis = (1..12).map { 0.0 },
                                onValueChange = { index ->
                                    selectedIndex = index
                                },
                                xAxis = (1..12).map {
                                    LocalDate(
                                        2025,
                                        it,
                                        1
                                    ).month.name.substring(0..2)
                                },
                                maxY = 1.0,
                                indicatorColor = Color.Transparent,
                                lineColor = Color.Transparent,
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                            val calories = 0
                            SummaryCard(
                                calories = calories,
                                protein = calories * 0.3 / 4,
                                carbs = calories * 0.5 / 4,
                                fats = calories * 0.2 / 9,
                            )
                        }
                        Box(
                            modifier = modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Food data.", modifier.align(Alignment.Center))
                        }
                    }
                } else {
                    if (selectedFilter == "Week") {
                        val foods = state.foodsByWeek.map { it.value }
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {

                            BarGraph(
                                yAxis = foods,
                                onValueChange = { index ->
                                    selectedIndex = index
                                },
                                xAxis = state.foodsByWeek.map {
                                    it.label.getLocalDate().month.name.substring(
                                        0..2
                                    ) + " " + it.label.getLocalDate().dayOfMonth
                                },
                                maxY = foods.max(),
                            )

                            val calories = state.foodsByWeek[selectedIndex].value.roundToInt()
                            SummaryCard(
                                calories = calories,
                                protein = calories * 0.3 / 4,
                                carbs = calories * 0.5 / 4,
                                fats = calories * 0.2 / 9,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    } else {
                        val foods = state.foodsByMonth.map { it.value }
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            BarGraph(
                                yAxis = foods,
                                onValueChange = { index ->
                                    selectedIndex = index
                                },
                                xAxis = state.foodsByMonth.map {
                                    LocalDate.fromEpochDays(it.label).month.name.substring(
                                        0..2
                                    )
                                },
                                maxY = foods.max(),
                            )
                            val calories = state.foodsByMonth[selectedIndex].value.roundToInt()
                            SummaryCard(
                                calories = calories,
                                protein = calories * 0.3 / 4,
                                carbs = calories * 0.5 / 4,
                                fats = calories * 0.2 / 9,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant

                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier.size(48.dp))
            }


            item {
                var selectedIndex by remember { mutableStateOf(0) }
                if (state.weightsByWeek.isEmpty() || state.weightsByMonth.isEmpty()) {
                    Box {
                        DotGraph(
                            yAxis = (1..12).map { 0.0 },
                            onValueChange = { index ->
                                selectedIndex = index
                            },
                            xAxis = (1..12).map {
                                LocalDate(
                                    2025,
                                    it,
                                    1
                                ).month.name.substring(0..2)
                            },
                            maxY = 1.0,
                            indicatorColor = Color.Transparent,
                            lineColor = Color.Transparent,
                            backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                        )

                        Text(text = "No measured weights.", modifier.align(Alignment.Center))
                    }
                } else {
                    if (selectedFilter == "Week") {
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            var weights = state.weightsByWeek.map { it.value }
                            if (weights.size > 1) {
                                weights = weights.normalize()
                            }
                            DotGraph(
                                yAxis = weights,
                                onValueChange = { index ->
                                    selectedIndex = index
                                },
                                xAxis = state.weightsByWeek.map {
                                    it.label.getLocalDate().month.name.substring(
                                        0..2
                                    ) + " " + it.label.getLocalDate().dayOfMonth
                                },
                                maxY = weights.max(),
                            )
                            Text(
                                "${state.weightsByWeek[selectedIndex].value.roundDecimals()} kg",
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    } else {
                        var weights = state.weightsByMonth.map { it.value }
                        var indicatorColor = Color.Transparent
                        if (weights.size > 1) {
                            weights = weights.normalize()
                            indicatorColor = MaterialTheme.colorScheme.onSurface
                        }
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            DotGraph(
                                yAxis = weights,
                                onValueChange = { index ->
                                    selectedIndex = index
                                },
                                xAxis = state.weightsByMonth.map {
                                    LocalDate.fromEpochDays(it.label).month.name.substring(
                                        0..2
                                    )
                                },
                                maxY = weights.max(),
                            )
                            Text(
                                text = "${state.weightsByWeek[selectedIndex].value.roundDecimals()} kg",
                                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)

                            )
                        }
                    }
                }
            }
        }
    }

}


