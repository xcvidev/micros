package com.xcvi.micros.ui.destinations.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.domain.monthFormatted
import com.xcvi.micros.ui.core.BarGraph
import com.xcvi.micros.ui.core.DotGraph
import kotlinx.datetime.LocalDate
import kotlin.math.roundToInt

@Composable
fun FoodGraph(
    showDate: Boolean,
    data: Map<LocalDate, Portion>,
    noDataText: String,
    onScroll: (Portion) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    if (data.isEmpty()) {
        EmptyGraph(
            noDataText = noDataText,
        ) {
            onScroll(Portion())
        }
    } else {
        val dates = data.keys.toList()
        val foods = data.values.toList()
        val calories = foods.map { it.macros.calories.roundToInt() }
        val labels = if (showDate) {
            dates.map {
                "${it.monthFormatted(true)} ${it.dayOfMonth}"
            }
        } else {
            dates.map {
                it.monthFormatted(true)
            }
        }

        BarGraph(
            yAxis = calories,
            onValueChange = { index ->
                selectedIndex = index
            },
            xAxis = labels,
            maxY = calories.max() * 1.1,
        )

        val currentValue = foods[selectedIndex]
        onScroll(currentValue)
    }
}


@Composable
private fun EmptyGraph(
    noDataText: String,
    onValueChange: (Int) -> Unit,
) {
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
            lineColor = Color.Transparent,
        )
        Text(text = noDataText, Modifier.align(Alignment.Center))
    }
}

