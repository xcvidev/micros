package com.xcvi.micros.ui.feature_stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.xcvi.micros.data.weight.model.WeightStats
import com.xcvi.micros.domain.monthFormatted
import com.xcvi.micros.domain.roundDecimals
import com.xcvi.micros.ui.core.DotGraph
import com.xcvi.micros.ui.feature_weight.SummaryItem
import kotlinx.datetime.LocalDate

@Composable
fun WeightGraph(
    onScroll: (WeightStats) -> Unit,
    showDate: Boolean,
    data: List<WeightStats>,
    noDataText: String,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    if (data.isEmpty()) {
        EmptyGraph(
            noDataText = noDataText,
        ) {

        }
    } else {

        val yAxis = data.map { it.normalized }

        DotGraph(
            yAxis = yAxis,
            onValueChange = { index ->
                selectedIndex = index
            },
            xAxis = data.map { it.label },
        )
        val currentWeights = data[selectedIndex]
        onScroll(currentWeights)
    }
}

@Composable
fun WeightSummary(
    modifier: Modifier = Modifier,
    list: WeightStats,
    maxLabel: String,
    minLabel: String,
    avgLabel: String
) {

    val min = list.min ?: 0.0
    val max = list.max ?: 0.0
    val avg = list.avg ?: 0.0

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            SummaryItem(label = minLabel, value = min.roundDecimals().toString())
            SummaryItem(label = avgLabel, value = avg.roundDecimals().toString())
            SummaryItem(label = maxLabel, value = max.roundDecimals().toString())
        }
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
