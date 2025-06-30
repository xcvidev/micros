package com.xcvi.micros.ui.core

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xcvi.micros.domain.roundDecimals
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun NumberPicker(
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tickColor: Color = MaterialTheme.colorScheme.onSurface,
    numberColor: Color = MaterialTheme.colorScheme.onSurface,
    indicatorTickColor: Color = MaterialTheme.colorScheme.onSurface,
    textFieldContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    valueRange: IntRange = 0..1000,
    initialValue: Int = 100,
    tickSpacingDp: Dp = 12.dp,
    clickGranularity: Int = 10,
    horizontalClickTolerancePx: Float = 150f,
    verticalClickTolerancePx: Float = 100f
) {
    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val maxOffset = (valueRange.last - valueRange.first) * tickSpacingPx

    val scrollOffsetAnim =
        remember { Animatable((initialValue - valueRange.first) * tickSpacingPx) }
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val centerValue = (scrollOffsetAnim.value / tickSpacingPx).roundToInt() + valueRange.first
    val clampedValue = centerValue.coerceIn(valueRange)
    onValueChange(clampedValue)


    // Snap to nearest tick after scroll ends
    LaunchedEffect(scrollState.isScrollInProgress) {
        if (!scrollState.isScrollInProgress) {
            val nearestTick = (scrollOffsetAnim.value / tickSpacingPx).roundToInt()
            val targetOffset = nearestTick * tickSpacingPx
            scrollOffsetAnim.animateTo(targetOffset)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val tapX = offset.x
                    val centerX = size.width / 2f
                    val relativeX = scrollOffsetAnim.value + (tapX - centerX)
                    val tappedIndexFloat = relativeX / tickSpacingPx

                    val closestMultipleOf10Index =
                        ((tappedIndexFloat.roundToInt() / clickGranularity.toDouble()).roundToInt()) * clickGranularity
                    val tappedValue = valueRange.first + closestMultipleOf10Index

                    if (tappedValue in valueRange) {
                        val tickX =
                            centerX - scrollOffsetAnim.value + (tappedValue - valueRange.first) * tickSpacingPx
                        val tapY = offset.y

                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset = (tappedValue - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
                        }
                    }
                }
            }
            .scrollable(
                orientation = Orientation.Horizontal,
                state = scrollState,
                reverseDirection = false
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value
            val totalTicks = valueRange.count()

            for (i in 0..totalTicks) {
                val x = startX + i * tickSpacingPx
                if (x < 0 || x > size.width) continue

                val height = if ((valueRange.first + i) % 5 == 0) 30f else 15f
                drawLine(
                    color = tickColor,
                    start = Offset(x, size.height / 2),
                    end = Offset(x, size.height / 2 - height),
                    strokeWidth = 2f
                )

                if ((valueRange.first + i) % 10 == 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${valueRange.first + i}",
                        x,
                        size.height / 2 + 30,
                        android.graphics.Paint().apply {
                            color = numberColor.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 28f
                        }
                    )
                }
            }
        }

        // Center indicator
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.dp)
                .height(50.dp)
                .background(indicatorTickColor)
        )


        var textValue by remember {
            mutableStateOf(
                TextFieldValue(
                    text = clampedValue.toString()
                )
            )
        }
        var isEditing by remember { mutableStateOf(false) }

        LaunchedEffect(clampedValue, isEditing) {
            if (!isEditing && textValue.text != clampedValue.toString()) {
                textValue = TextFieldValue(
                    text = clampedValue.toString()
                )
            }
        }

        val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-62).dp)
                .background(
                    color = textFieldContainerColor,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            TextField(
                value = textValue,
                onValueChange = { newValue ->
                    textValue = newValue
                    newValue.text.toIntOrNull()?.let { entered ->
                        if (entered in valueRange) {
                            val targetOffset = (entered - valueRange.first) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(80.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        //isEditing = focusState.isFocused
                        if (focusState.isFocused && !isEditing) {
                            isEditing = true
                            textValue = textValue.copy(
                                selection = TextRange(0, textValue.text.length) // Select all
                            )
                        } else if (!focusState.isFocused) {
                            isEditing = false
                        }
                    },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = numberColor,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

    }
}


@Composable
fun DecimalNumberPicker(
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    showInputField: Boolean = false,
    tickColor: Color = MaterialTheme.colorScheme.onSurface,
    numberColor: Color = MaterialTheme.colorScheme.onSurface,
    indicatorTickColor: Color = MaterialTheme.colorScheme.onSurface,
    textFieldContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..500.0,
    initialValue: Double = 80.0,
    tickSpacingDp: Dp = 12.dp,
    clickGranularity: Double = 0.1,
    horizontalClickTolerancePx: Float = 150f,
    verticalClickTolerancePx: Float = 100f,
    decimalPlaces: Int = 1
) {
    val tickSpacingPx = with(LocalDensity.current) { tickSpacingDp.toPx() }
    val totalTicks = ((valueRange.endInclusive - valueRange.start) / clickGranularity).toInt()
    val maxOffset = totalTicks * tickSpacingPx

    val scope = rememberCoroutineScope()

    val scrollOffsetAnim = remember {
        Animatable(
            ((initialValue * tickSpacingPx / clickGranularity).toFloat())
        )
    }

    LaunchedEffect(initialValue) {
        val initialOffset =
            ((initialValue - valueRange.start) / clickGranularity).toFloat() * tickSpacingPx
        scrollOffsetAnim.snapTo(initialOffset)
    }

    val scrollState = rememberScrollableState { delta ->
        val newOffset = (scrollOffsetAnim.value - delta).coerceIn(0f, maxOffset)
        val consumed = scrollOffsetAnim.value - newOffset
        scope.launch { scrollOffsetAnim.snapTo(newOffset) }
        consumed
    }

    val rawIndex = scrollOffsetAnim.value / tickSpacingPx
    val clampedIndex = rawIndex.roundToInt().coerceIn(0, totalTicks)

    val currentValue = (valueRange.start + clampedIndex * clickGranularity).roundDecimals()

    var lastValue by remember { mutableStateOf<Double?>(null) }
    val roundedValue = currentValue.roundDecimals()
    if (lastValue != roundedValue) {
        lastValue = roundedValue
        onValueChange(roundedValue)
    }


    LaunchedEffect(scrollState.isScrollInProgress) {
        if (!scrollState.isScrollInProgress) {
            val nearestTick = rawIndex.roundToInt()
            val targetOffset = nearestTick * tickSpacingPx
            scrollOffsetAnim.animateTo(targetOffset)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val tapX = offset.x
                    val centerX = size.width / 2f
                    val relativeX = scrollOffsetAnim.value + (tapX - centerX)
                    val tappedIndexFloat = relativeX / tickSpacingPx

                    val closestGranularityIndex =
                        ((tappedIndexFloat / (1 / clickGranularity)).roundToInt()) *
                                (1 / clickGranularity).toInt()

                    val tappedValue = valueRange.start + closestGranularityIndex * clickGranularity

                    if (tappedValue in valueRange) {
                        val tickX =
                            centerX - scrollOffsetAnim.value + (tappedValue - valueRange.start) / clickGranularity * tickSpacingPx
                        val tapY = offset.y

                        if (
                            abs(tapX - tickX) <= horizontalClickTolerancePx &&
                            tapY in (size.height / 2 - verticalClickTolerancePx)..(size.height / 2 + verticalClickTolerancePx)
                        ) {
                            val targetOffset =
                                ((tappedValue - valueRange.start) / clickGranularity) * tickSpacingPx
                            scope.launch {
                                scrollOffsetAnim.animateTo(targetOffset.toFloat())
                            }
                        }
                    }
                }
            }
            .scrollable(
                orientation = Orientation.Horizontal,
                state = scrollState,
                reverseDirection = false
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val startX = centerX - scrollOffsetAnim.value

            for (i in 0..totalTicks) {
                val x = startX + i * tickSpacingPx
                if (x < 0 || x > size.width) continue

                val height = if (i % 5 == 0) 30f else 15f
                drawLine(
                    color = tickColor,
                    start = Offset(x, size.height / 2),
                    end = Offset(x, size.height / 2 - height),
                    strokeWidth = 2f
                )

                if (i % 10 == 0) {
                    val value = valueRange.start + i * clickGranularity
                    drawContext.canvas.nativeCanvas.drawText(
                        "%.${decimalPlaces}f".format(value),
                        x,
                        size.height / 2 + 30,
                        android.graphics.Paint().apply {
                            color = numberColor.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 28f
                        }
                    )
                }
            }
        }


        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(2.dp)
                .height(50.dp)
                .background(indicatorTickColor)
        )
        var textValue by remember { mutableStateOf("%.${decimalPlaces}f".format(currentValue)) }
        var isEditing by remember { mutableStateOf(false) }

        LaunchedEffect(currentValue, isEditing) {
            if (!isEditing && textValue != "%.${decimalPlaces}f".format(currentValue)) {
                textValue = "%.${decimalPlaces}f".format(currentValue)
            }
        }

        val focusRequester = remember { FocusRequester() }

        if (showInputField) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-32).dp)
                    .background(
                        color = textFieldContainerColor,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                TextField(
                    value = textValue,
                    onValueChange = { newValue ->
                        textValue = newValue
                        newValue.toDoubleOrNull()?.let { entered ->
                            if (entered in valueRange) {
                                val targetOffset =
                                    ((entered - valueRange.start) / clickGranularity) * tickSpacingPx
                                scope.launch {
                                    scrollOffsetAnim.animateTo(targetOffset.toFloat())
                                }
                            }
                        }

                    },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(80.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { isEditing = it.isFocused },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        color = numberColor,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
