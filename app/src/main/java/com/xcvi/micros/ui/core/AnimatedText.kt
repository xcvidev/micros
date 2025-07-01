package com.xcvi.micros.ui.core

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun StreamingText(
    fullText: String,
    modifier: Modifier = Modifier,
    charDelayMillis: Long = 30L,
    onFinished: (() -> Unit)? = null,
    color: Color = Color.Unspecified,
    style: TextStyle = TextStyle.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    textAlign: TextAlign? = null
) {
    var visibleText by remember { mutableStateOf("") }
    val lines = fullText.split("\n").size

    LaunchedEffect(fullText) {
        visibleText = ""
        for (i in fullText.indices) {
            visibleText += fullText[i]
            delay(charDelayMillis)
        }
        onFinished?.invoke()
    }

    Box{
        Text(
            text = visibleText,
            modifier = modifier,
            color = color,
            style = style,
            fontSize = fontSize,
            fontWeight = fontWeight,
            textAlign = textAlign,
            minLines = lines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

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
