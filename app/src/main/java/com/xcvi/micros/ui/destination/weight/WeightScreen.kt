package com.xcvi.micros.ui.destination.weight

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.DecimalNumberPicker
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.getLocalDateTime
import com.xcvi.micros.ui.core.keyboardOpenState
import com.xcvi.micros.ui.core.roundDecimals
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    bottomBarPadding: Dp,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: WeightViewModel = koinViewModel()
) {

    OnNavigation {
        viewModel.getData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Weight Manager")
                }
            )
        },
    ) { padding ->
        viewModel.state.currentWeight?.let { currentWeight ->
            var isEditing by remember { mutableStateOf(false) }

            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                modifier = modifier.padding(
                    bottom = bottomBarPadding,
                    top = padding.calculateTopPadding()
                )
            ) {
                Text(
                    text = "Current weight: ${currentWeight.value} ${currentWeight.unit}",
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )

                LazyColumn {
                    item {
                        DecimalNumberPicker(
                            initialValue = currentWeight.value.roundDecimals(),
                            onValueChange = { number ->
                                viewModel.setNumberPickerValue(number)
                                if(!isEditing){
                                    isEditing = (number != currentWeight.value)
                                }
                            }
                        )
                    }
                    item {
                        AnimatedVisibility(isEditing) {
                            Button(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                onClick = {
                                    isEditing = false
                                    viewModel.save()
                                }
                            ) {
                                Text(text = "Save")
                            }
                        }
                    }
                    if(viewModel.state.weights.isNotEmpty()){
                        item {
                            Text(
                                text = "Today's Weight",
                                modifier = modifier.fillMaxWidth(),
                                textAlign = TextAlign.Right,
                            )
                        }
                    }
                    itemsIndexed(viewModel.state.weights) { index, weight ->
                        HorizontalDivider()
                        ListItem(
                            headlineContent = {
                                Text(text = weight.value.toString())
                            },
                            supportingContent = {
                                Text(text = "${getLocalDateTime(weight.timestamp)}")
                            },
                            trailingContent = {
                                IconButton(onClick = { viewModel.delete(weight) }) {
                                    Text("x", fontSize = 24.sp)
                                }
                            }
                        )
                    }
                    if(viewModel.state.weights.isNotEmpty()){
                        item {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

}


















