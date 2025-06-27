package com.xcvi.micros.ui.core

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun FadingText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 14,
    animationDuration: Int = 300,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colorScheme.onSurface
) {

    Box(modifier = modifier){
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                fadeIn(tween(animationDuration)) togetherWith fadeOut(tween(300))
            },
            label = "AnimatedText"
        ) { targetText ->
            Text(
                text = targetText,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                color = color
            )
        }
    }
}


@Composable
fun SlidingText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 14,
    animationDuration: Int = 300,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Box(modifier = modifier){
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(durationMillis = animationDuration),
                    initialOffsetX = { fullWidth -> fullWidth } // Slide in from right
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = animationDuration
                    ),
                    targetOffsetX = { fullWidth -> -fullWidth } // Slide out to left
                )
            },
            label = "SlideTextAnimation"
        ) { targetText ->
            Text(
                text = targetText,
                fontSize = fontSize.sp,
                fontWeight = fontWeight,
                color = color
            )
        }
    }
}
