package com.xcvi.micros.ui.destinations.food.f3

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.ui.core.BackIcon
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.StreamingText
import com.xcvi.micros.ui.core.StreamingTextCard
import com.xcvi.micros.ui.core.rememberShakeOffset
import com.xcvi.micros.ui.destinations.FoodGraph
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    resultsLabel: String,
    placeHolder: String,
    generatingIndicatorText: String,
    proteinLabel: String,
    carbsLabel: String,
    fatsLabel: String,
    recentlyAddedText: String,
    date: Int,
    meal: Int,
    navController: NavHostController,
    viewModel: AddViewModel = koinViewModel()
) {
    OnNavigation {
        viewModel.getData()
    }


    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val isGenerating = viewModel.state.isGenerating
    val isStreaming = viewModel.state.isStreaming
    val generated = viewModel.state.generated
    val portions = viewModel.state.filtered

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = shakeOffset)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = { BackIcon { navController.popBackStack() } })
            }
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 8.dp),
            ) {
                item {
                    SearchField(
                        placeHolder = placeHolder,
                        value = query,
                        onValueChange = {
                            query = it
                            viewModel.filter(query)
                        },
                        isGenerating = isGenerating,
                        isStreaming = isStreaming,
                        onGenerate = {
                            viewModel.generate(query = query) {
                                shakeTrigger = true
                            }
                            query = ""
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                        onScan = {
                            navController.navigate(
                                FoodGraph.Scan(
                                    meal = meal,
                                    date = date
                                )
                            )
                        },
                        onStop = {
                            query = ""
                            viewModel.stop()
                        },
                    )
                }


                when {
                    isGenerating -> item {
                        StreamingText(
                            modifier = Modifier.padding(top = 12.dp, start = 8.dp),
                            fullText = generatingIndicatorText,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    generated != null -> item {

                        Text(
                            modifier = Modifier.padding(top = 12.dp, start = 8.dp, bottom = 4.dp),
                            text = resultsLabel,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )

                        GeneratedItem(
                            proteinLabel = proteinLabel,
                            carbsLabel = carbsLabel,
                            fatsLabel = fatsLabel,
                            portion = generated,
                            onClick = {
                                navController.navigate(
                                    FoodGraph.Details(
                                        date = date,
                                        meal = meal,
                                        amount = generated.amountInGrams.roundToInt(),
                                        barcode = generated.barcode
                                    )
                                )
                            },
                            onFinished = {
                                viewModel.onFinishedStreaming()
                            }
                        )
                    }

                    portions.isNotEmpty() -> {
                        item {
                            Text(
                                modifier = Modifier.padding(top = 12.dp, start = 8.dp),
                                text = recentlyAddedText,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        items(portions) {
                            RecentsItem(
                                portion = it,
                                onClick = {
                                    navController.navigate(
                                        FoodGraph.Details(
                                            date = date,
                                            meal = meal,
                                            amount = it.amountInGrams.roundToInt(),
                                            barcode = it.barcode
                                        )
                                    )
                                }
                            )
                            HorizontalDivider(
                                thickness = 0.25.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                            )
                        }
                        item {
                            HorizontalDivider(
                                thickness = 0.25.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                            )
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }

            }

        }
    }
}


@Composable
fun SearchField(
    placeHolder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isGenerating: Boolean,
    isStreaming: Boolean,
    onGenerate: () -> Unit,
    onScan: () -> Unit,
    onStop: () -> Unit,
) {

    val icon = when {
        !isGenerating && !isStreaming -> Icons.Default.ArrowUpward
        else -> Icons.Default.Stop
    }

    val placeHolderText = when {
        !isGenerating && !isStreaming -> placeHolder
        else -> ""
    }

    val action = when {
        !isGenerating && !isStreaming -> onGenerate
        else -> onStop
    }



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Card {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = {
                    onValueChange(it)
                },
                maxLines = 1,
                singleLine = true,
                placeholder = {
                    StreamingText(fullText = placeHolderText, charDelayMillis = 15)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Transparent,
                    unfocusedContainerColor = Transparent,
                    disabledContainerColor = Transparent,
                    focusedIndicatorColor = Transparent,
                    unfocusedIndicatorColor = Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onGenerate()
                    }
                )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onScan
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(R.string.scan_barcode))
                        Icon(
                            painter = painterResource(R.drawable.ic_scan),
                            contentDescription = ""
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = action,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(icon, "")
                }
            }
        }
    }
}


@Composable
fun PromptField(
    scanButtonText: String,
    isPrompting: Boolean,
    isSuccess: Boolean,
    onPrompt: (String) -> Unit,
    onStop: () -> Unit,
    onScan: () -> Unit,
    query: String,
    placeHolder: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = query,
                onValueChange = { newQuery ->
                    onQueryChange(newQuery)
                },
                maxLines = 1,
                singleLine = true,
                placeholder = {
                    Text(
                        text = placeHolder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Transparent,
                    unfocusedContainerColor = Transparent,
                    disabledContainerColor = Transparent,
                    focusedIndicatorColor = Transparent,
                    unfocusedIndicatorColor = Transparent,
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onScan
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = scanButtonText)
                        Icon(
                            painter = painterResource(R.drawable.ic_scan),
                            contentDescription = ""
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        when {
                            isPrompting -> onStop()
                            isSuccess -> onQueryChange("")
                            else -> onPrompt(query)
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    when {
                        isPrompting -> Icon(Icons.Default.Stop, "")
                        isSuccess -> Icon(Icons.Default.Clear, "")
                        else -> Icon(Icons.Default.ArrowUpward, "")
                    }
                }
            }
        }
    }
}

@Composable
fun RecentsItem(
    portion: Portion,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = portion.name,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${portion.macros.calories.roundToInt()} kcal, ${portion.amountInGrams.roundToInt()} g",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun GeneratedItem(
    proteinLabel: String,
    carbsLabel: String,
    fatsLabel: String,
    portion: Portion,
    onClick: () -> Unit,
    onFinished: () -> Unit
) {
    StreamingTextCard(
        title = portion.name,
        subtitle = "${portion.macros.calories.roundToInt()} kcal, ${portion.amountInGrams.roundToInt()} g",
        body = "$proteinLabel: ${portion.macros.protein} g\n$carbsLabel: ${portion.macros.carbohydrates} g\n$fatsLabel: ${portion.macros.fats} g ",
        onClick = onClick,
        onFinished = onFinished
    )
}


/*
PromptField(
                        modifier = Modifier.padding(vertical = 8.dp),
                        isPrompting = state.isStreaming,
                        isSuccess = state.generated != null,
                        onPrompt = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            viewModel.generate(date = date, meal = meal) { shakeTrigger = true }
                        },
                        onStop = {
                            viewModel.stop()
                            shakeTrigger = true
                        },
                        onScan = {
                            navController.navigate(
                                FoodGraph.Scan(
                                    meal = meal,
                                    date = date
                                )
                            )
                        },
                        query = state.query,
                        onQueryChange = { viewModel.setQuery(it) },
                        placeHolder = inputFieldPlaceHolder,
                        scanButtonText = scanButtonText,
                    )
 */








