package com.xcvi.micros.ui.destinations.weight

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xcvi.micros.domain.Weight
import com.xcvi.micros.domain.formatEpochDate
import com.xcvi.micros.domain.formatTimestamp
import com.xcvi.micros.domain.getEndOfWeek
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getStartOfWeek
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.ui.core.DecimalNumberPicker
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.domain.roundDecimals
import com.xcvi.micros.ui.core.DateSelectorDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    modifier: Modifier = Modifier,
    viewModel: WeightViewModel = koinViewModel(),
    topAppBarTitle: String = "Weight Manager",
    deleteDialogTitle: String = "Delete weight",
    deleteDialogText: String = "Are you sure you want to delete this weight?",
    deleteDialogButtonText: String = "Delete",
    saveButtonText: String = "Save",
    noWeightsText: String = "No weights saved this week.",

) {
    val state = viewModel.state
    OnNavigation {
        viewModel.getData(getToday())
    }

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DateSelectorDialog(
            currentDate = state.currentDate,
            onDismissRequest = { showDialog = false },
            onDateChanged = { viewModel.setDate(it) }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(deleteDialogTitle)
            },
            text = { Text(deleteDialogText) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete()
                    showDeleteDialog = false
                }) {
                    Text(deleteDialogButtonText)
                }
            }
        )

    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = topAppBarTitle)
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.CalendarMonth, "")
                    }
                }
            )
        },
    ) { padding ->

        var isEditing by remember { mutableStateOf(false) }
        LazyColumn(
            modifier = modifier.padding(padding)
        ) {
            item {
                Spacer(modifier = modifier.height(48.dp))
            }
            item {
                Text(
                    text = state.numberPickerValue.roundDecimals().toString(),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

            }
            item {
                DecimalNumberPicker(
                    initialValue = state.initialValue,
                    onValueChange = { number ->
                        viewModel.setNumberPickerValue(number)
                        if (!isEditing) {
                            isEditing = (number != state.numberPickerValue)
                        }
                    }
                )
            }
            item {
                Button(
                    //enabled = isEditing,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = {
                        isEditing = false
                        viewModel.save()
                    }
                ) {
                    Text(text = saveButtonText)
                }
            }
            item {
                Spacer(modifier = modifier.height(60.dp))
            }
            if (state.weights.isNotEmpty()) {
                item {
                    WeightSummaryScreen(
                        list = state.weights,
                        onClick = { weight ->
                            viewModel.setDeleteWeight(weight)
                            showDeleteDialog = true
                        }
                    )
                }
            } else {
                item {
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = noWeightsText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            item {
                Spacer(modifier = modifier.height(120.dp))
            }

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeightSummaryScreen(
    list: List<Weight>,
    onClick: (Weight) -> Unit,
    maxLabel: String = "Max",
    minLabel: String = "Min",
    avgLabel: String = "Avg"
) {
    if (list.isEmpty()) return

    val min = list.minOf { it.value }
    val max = list.maxOf { it.value }
    val avg = list.sumOf { it.value } / list.size
    val date = list.first().timestamp.getEpochDate()
    val start = date.getStartOfWeek().formatEpochDate(short = false)
    val end = date.getEndOfWeek().formatEpochDate(short = false)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                //    horizontal = 24.dp,
                vertical = 16.dp
            )
    ) {
        // Date range title
        Text(
            text = "$start – $end",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SummaryItem(label = minLabel, value = min.roundDecimals().toString())
            SummaryItem(label = avgLabel, value = avg.roundDecimals().toString())
            SummaryItem(label = maxLabel, value = max.roundDecimals().toString())
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Weight entries
        list.forEach { weight ->
            WeightEntry(
                weight = weight.value.toString(),
                date = weight.timestamp.formatTimestamp(short = true, showDayOfWeek = true),
                onClick = { onClick(weight) })
        }
        HorizontalDivider(thickness = 0.3.dp)
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeightEntry(
    weight: String,
    date: String,
    onClick: () -> Unit = {}
) {

    val interactionSource = remember { MutableInteractionSource() }
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxWidth() // Optional: to give it some size
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = { /* optional */ },
                onLongClick = {
                    println("Long press detected!")
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
    ) {
        HorizontalDivider(thickness = 0.3.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = weight,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Text(
            text = date,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}



