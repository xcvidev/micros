package com.xcvi.micros.ui.destination.food.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.ui.core.BackIcon
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.StreamingText
import com.xcvi.micros.ui.destination.FoodGraph
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    date: Int,
    meal: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: AddViewModel = koinViewModel()
) {

    OnNavigation {
        viewModel.getData()
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {}, navigationIcon = { BackIcon { navController.popBackStack() } })
        }
    ) { padding ->
        Column(
            modifier = modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PromptField(
                isGenerating = viewModel.state.isGenerating,
                onPrompt = {
                    viewModel.generate(date = date, meal = meal)
                },
                onStop = { },
                onScan = { navController.navigate(FoodGraph.Scan(meal = meal, date = date)) },
                query = viewModel.state.query,
                onQueryChange = { viewModel.setQuery(it) },
                placeHolder = "Describe food",
            )
            val portions = viewModel.state.portions
            val generated = viewModel.state.generated
            LazyColumn{
                when {
                    viewModel.state.isGenerating -> item {
                        StreamingText(
                            fullText = "Generating...",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    generated != null -> item {
                        PortionItem(
                            portion = generated,
                            streamContent = true
                        ) {
                            navController.navigate(
                                FoodGraph.Details(
                                    meal = meal,
                                    date = date,
                                    amount = generated.amount.roundToInt(),
                                    barcode = generated.barcode
                                )
                            )
                        }
                    }

                    viewModel.state.generated == null -> items(portions) { recent ->
                        PortionItem(
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
                    }
                }

            }
        }
    }
}

@Composable
fun PortionItem(
    portion: Portion,
    modifier: Modifier = Modifier,
    streamContent: Boolean = false,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
    ) {
        HorizontalDivider()
        ListItem(
            modifier = modifier,
            headlineContent = {
                if (streamContent) {
                    Box {
                        StreamingText(
                            charDelayMillis = 30,
                            fullText = """
                                ${portion.name}
                                    
                                Calories: ${portion.calories} kcal, ${portion.amount} g
                                    
                                Protein: ${portion.macros.protein} g
                                Carbs: ${portion.macros.carbs} g
                                Fats: ${portion.macros.fats} g
                                """.trimIndent(),
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    Text(
                        text = """
                        ${portion.name}
                                    
                        Calories: ${portion.calories} kcal, ${portion.amount} g

                        Protein: ${portion.macros.protein} g
                        Carbs: ${portion.macros.carbs} g
                        Fats: ${portion.macros.fats} g
                        """.trimIndent(),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
        )
        if (streamContent) {
            HorizontalDivider()
        }
    }
}

@Composable
fun PromptField(
    isGenerating: Boolean,
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onScan
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(text = "Scan barcode")
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = ""
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        if (isGenerating) {
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
                    if (isGenerating) {
                        Icon(Icons.Default.Stop, "")
                    } else {
                        Icon(Icons.Default.ArrowUpward, "")
                    }
                }
            }
        }

    }
}













