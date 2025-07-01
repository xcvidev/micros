package com.xcvi.micros.ui.destinations.stats

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.xcvi.micros.domain.Weight
import com.xcvi.micros.domain.formatEpochDate
import com.xcvi.micros.domain.formatTimestamp
import com.xcvi.micros.domain.getEndOfWeek
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getStartOfWeek
import com.xcvi.micros.domain.monthFormatted
import com.xcvi.micros.domain.normalize
import com.xcvi.micros.domain.roundDecimals
import com.xcvi.micros.ui.core.DotGraph
import com.xcvi.micros.ui.destinations.weight.SummaryItem
import com.xcvi.micros.ui.destinations.weight.WeightEntry
import kotlinx.datetime.LocalDate

@Composable
fun WeightGraph(
    onScroll: (List<Weight>) -> Unit,
    showDate: Boolean,
    data: Map<LocalDate, List<Weight>>,
    noDataText: String,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    if (data.isEmpty()) {
        EmptyGraph(
            noDataText = noDataText,
        ) {
            onScroll(emptyList())
        }
    } else {

        val dates = data.keys.toList()           // same size as weights
        val weights = data.values.toList()       // same size as dates

        println("dates: ${dates.size}")
        println("weights: ${weights.size}")
        println("selected: $selectedIndex")


        val labels = if (showDate) {
            dates.map {
                "${it.monthFormatted(true)} ${it.dayOfMonth}"
            }
        } else {
            dates.map {
                it.monthFormatted(true)
            }
        }

        val yAxis = weights.map { it.weightAvg() }.normalize()
        DotGraph(
            yAxis = yAxis,
            onValueChange = { index ->
                selectedIndex = index
            },
            xAxis = labels,
            maxY = yAxis.max() * 1.1,
        )
        val currentWeights = weights[selectedIndex]
        onScroll(currentWeights)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeightSummary(
    modifier: Modifier = Modifier,
    list: List<Weight>,
    maxLabel: String,
    minLabel: String,
    avgLabel: String
) {
    if (list.isEmpty()) return

    val min = list.minOf { it.value }
    val max = list.maxOf { it.value }
    val avg = list.sumOf { it.value } / list.size

    Box(modifier = modifier, contentAlignment = Alignment.Center){
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
            maxY = 1.0,
            indicatorColor = Color.Transparent,
            lineColor = Color.Transparent,
        )
        Text(text = noDataText, Modifier.align(Alignment.Center))
    }
}
