package com.xcvi.micros.ui.core

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SmoothProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    durationMillis: Int = 500,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceBright
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = durationMillis, easing = LinearOutSlowInEasing)
    )
    LinearProgressIndicator(
        modifier = modifier,
        progress = { animatedProgress.value },
        color = color,
        trackColor = trackColor,
        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        gapSize = 0.dp,
        drawStopIndicator = {}
    )
}

/*

                else -> {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = stringResource(R.string.search_dialog_text_2))
                        val animatedProgress = animateFloatAsState(
                            targetValue = progress,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearOutSlowInEasing
                            )
                        )
                        LinearProgressIndicator(
                            progress = { animatedProgress.value },
                            trackColor = MaterialTheme.colorScheme.surfaceBright,
                            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
 */