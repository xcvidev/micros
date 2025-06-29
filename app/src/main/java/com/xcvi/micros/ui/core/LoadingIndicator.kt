package com.xcvi.micros.ui.core

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


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
