package com.xcvi.micros.ui.destinations.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.xcvi.micros.domain.Weight
import com.xcvi.micros.domain.monthFormatted
import com.xcvi.micros.domain.normalize
import com.xcvi.micros.domain.roundDecimals
import com.xcvi.micros.ui.core.DotGraph
import kotlinx.datetime.LocalDate

@Composable
fun WeightGraph(
    showDate: Boolean,
    data: Map<LocalDate, Weight>,
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
        val weights = data.values.toList().map { it.value }
        val weightsNormalized = weights.normalize()
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

            DotGraph(
                yAxis = weightsNormalized,
                onValueChange = { index ->
                    selectedIndex = index
                },
                xAxis = labels,
                maxY = weightsNormalized.max() * 1.1,
            )

            val currentValue = weights[selectedIndex]

            Text(
                text = currentValue.roundDecimals().toString(),
                modifier = Modifier.fillMaxWidth(),
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyGraph(
    noDataText: String,
    onValueChange: (Int) -> Unit,
) {
    Column {
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
}
