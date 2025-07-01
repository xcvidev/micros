package com.xcvi.micros.ui.destinations.food.add

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.domain.Portion
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
    modifier: Modifier = Modifier,
    inputFieldPlaceHolder: String = "Describe food",
    scanButtonText: String = "Scan Barcode",
    generatingIndicatorText: String = "Generating...",
    caloriesLabel: String = "Calories",
    proteinLabel: String = "Protein",
    carbsLabel: String = "Carbs",
    fatsLabel: String = "Fats",
    recentlyAddedText: String = "Recently Added",
    date: Int,
    meal: Int,
    navController: NavHostController,
    viewModel: AddViewModel = koinViewModel()
) {
    OnNavigation {
        viewModel.getData()
    }
    val state = viewModel.state
    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

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

            val portions = state.portions
            val generated = state.generated
            val lazyListState = remember { androidx.compose.foundation.lazy.LazyListState() }

            if(lazyListState.isScrollInProgress){
                focusManager.clearFocus()
                keyboardController?.hide()
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = padding.calculateTopPadding()
                    )
            ) {
                item {
                    PromptField(
                        modifier = Modifier.padding(vertical = 8.dp),
                        isPrompting = state.isStreaming,
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
                    Spacer(modifier.height(12.dp))
                }
                when {
                    state.isGenerating -> item {
                        StreamingText(
                            fullText = generatingIndicatorText,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    generated != null -> item {
                        GeneratedItem(
                            portion = generated,
                            proteinLabel = proteinLabel,
                            carbsLabel = carbsLabel,
                            fatsLabel = fatsLabel,
                            onFinished = {
                                viewModel.onFinishedStreamingText()
                            },
                            onClick = {
                                navController.navigate(
                                    FoodGraph.Details(
                                        meal = meal,
                                        date = date,
                                        amount = generated.amount.roundToInt(),
                                        barcode = generated.barcode
                                    )
                                )
                            }
                        )
                    }

                    state.portions.isNotEmpty() -> {
                        item {
                            Text(
                                text = recentlyAddedText,
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        items(portions) { recent ->
                            RecentsItem(
                                portion = recent,
                            ) {
                                navController.navigate(
                                    FoodGraph.Details(
                                        meal = meal,
                                        date = date,
                                        amount = recent.amount.roundToInt(),
                                        barcode = recent.barcode
                                    )
                                )
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(48.dp))
                        }
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
                text = "${portion.calories} kcal, ${portion.amount} g",
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
        subtitle = "${portion.calories} kcal, ${portion.amount} g",
        body = "$proteinLabel: ${portion.macros.protein} g\n$carbsLabel: ${portion.macros.carbs} g\n$fatsLabel: ${portion.macros.fats} g ",
        onClick = onClick,
        onFinished = onFinished
    )
}

/*
OutlinedCard(
        onClick = onClick,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = portion.name,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                style = MaterialTheme.typography.titleMedium
            )
            StreamingText(
                charDelayMillis = 30,
                fullText = "${portion.calories} kcal, ${portion.amount} g",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            StreamingText(
                charDelayMillis = 30,
                fullText = "$proteinLabel: ${portion.macros.protein} g\n$carbsLabel: ${portion.macros.carbs} g\n$fatsLabel: ${portion.macros.fats} g ",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
 */

@Composable
fun PromptField(
    scanButtonText: String,
    isPrompting: Boolean,
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
                placeholder = { Text(placeHolder) },
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
                        if (isPrompting) {
                            onStop()
                        } else {
                            onPrompt(query)
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isPrompting) {
                        Icon(Icons.Default.Stop, "")
                    } else {
                        Icon(Icons.Default.ArrowUpward, "")
                    }
                }
            }
        }
    }
}













