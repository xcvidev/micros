package com.xcvi.micros.ui.core

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
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
