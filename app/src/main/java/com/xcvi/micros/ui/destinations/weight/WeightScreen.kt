package com.xcvi.micros.ui.destinations.weight

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xcvi.micros.domain.formatEpochDate
import com.xcvi.micros.domain.formatTimestamp
import com.xcvi.micros.domain.getEndOfWeek
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
    viewModel: WeightViewModel = koinViewModel()
) {
    val state = viewModel.state
    OnNavigation {
        viewModel.getData(getToday())
    }

    var showDialog by remember { mutableStateOf(false) }

    if(showDialog){
        DateSelectorDialog(
            currentDate = state.currentDate,
            onDismissRequest = { showDialog = false },
            onDateChanged = { viewModel.setDate(it) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Weight Manager")
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
                    Text(text = "Save")
                }
            }
            item {
                Spacer(modifier = modifier.height(60.dp))
            }
            if (state.weights.isNotEmpty()) {
                val text = if(state.currentDate == getToday()) {
                    "This Week"
                } else {
                    "${state.currentDate.getStartOfWeek().formatEpochDate()} - ${state.currentDate.getEndOfWeek().formatEpochDate()}"
                }
                item {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = text,
                            modifier = modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                        )
                    }
                }
                itemsIndexed(state.weights) { _, weight ->
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {
                            Text(
                                text = weight.value.toString(),
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                            )
                        },
                        supportingContent = {
                            Text(text = weight.timestamp.formatTimestamp())
                        },
                        trailingContent = {
                            IconButton(onClick = { viewModel.delete(weight) }) {
                                Icon(Icons.Default.Clear, "")
                            }
                        },
                        modifier = modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    HorizontalDivider()
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
                            text = "No measured weights this week.",
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



















