package com.xcvi.micros.ui.destinations.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.domain.monthFormatted
import com.xcvi.micros.ui.core.BarGraph
import com.xcvi.micros.ui.core.DotGraph
import com.xcvi.micros.ui.destinations.food.SummaryCard
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt

@Composable
fun FoodGraph(
    showDate: Boolean,
    data: Map<LocalDate, Portion>,
    modifier: Modifier = Modifier,
    noDataText: String,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    if (data.isEmpty()) {
        EmptyGraph(
            noDataText = noDataText,
        ) {
            selectedIndex = it
        }
    } else {
        val foods = data.values.toList()
        val calories = foods.map { it.calories.roundToInt() }
        val labels = if (showDate) {
            data.keys.toList().map {
                "${it.monthFormatted(true)} ${it.dayOfMonth}"
            }
        } else {
            data.keys.toList().map {
                it.monthFormatted(true)
            }
        }
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            BarGraph(
                yAxis = calories,
                onValueChange = { index ->
                    selectedIndex = index
                },
                xAxis = labels,
                maxY = calories.max() * 1.1,
            )

            val currentValue = foods[selectedIndex]
            Box(
                modifier = modifier.background(
                    MaterialTheme.colorScheme.surfaceContainer.copy(
                        alpha = 0.5f
                    )
                ).padding(vertical = 12.dp)
            ) {
                SummaryCard(
                    calories = currentValue.calories.roundToInt(),
                    protein = currentValue.macros.protein,
                    carbs = currentValue.macros.carbs,
                    fats = currentValue.macros.fats,
                )
            }
        }
    }
}

@Composable
private fun EmptyGraph(
    noDataText: String,
    onValueChange: (Int) -> Unit,
) {
    Column{
        Box {
            DotGraph(
                yAxis = (1..12).map { 0.0 },
                onValueChange = { index ->
                    onValueChange(index)
                },
                xAxis = (1..12).map {
                    LocalDate(
                        2025,
                        it,
                        1
                    ).monthFormatted(true)
                },
                maxY = 1.0,
                indicatorColor = Color.Transparent,
                lineColor = Color.Transparent,
            )
            Text(text = noDataText, Modifier.align(Alignment.Center))
        }
        Box(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surfaceContainer.copy(
                    alpha = 0.5f
                )
            )
        ) {
            SummaryCard(
                calories = 0,
                protein = 0.0,
                carbs = 0.0,
                fats = 0.0,
            )
        }
    }
}
