package com.xcvi.micros.ui.destinations.food.meal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.data.entity.AminoAcids
import com.xcvi.micros.data.entity.Macros
import com.xcvi.micros.data.entity.Minerals
import com.xcvi.micros.data.entity.Portion
import com.xcvi.micros.data.entity.Vitamins
import com.xcvi.micros.domain.roundDecimals
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.destinations.FoodGraph
import com.xcvi.micros.ui.destinations.food.FoodSummary
import com.xcvi.micros.ui.destinations.food.MacroLabel
import com.xcvi.micros.ui.destinations.food.dashboard.MicrosSection
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen(
    date: Int,
    meal: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MealViewModel = koinViewModel(),
    topAppBarText: String,
    inputDialogTitle: String ,
    inputDialogPlaceholder: String,
    saveMealButtonText: String ,
    macroTitle: String ,
    aminoTitle: String,
    mineralTitle: String,
    vitaminTitle: String,
) {
    OnNavigation {
        viewModel.getData(date = date, meal = meal)
    }

    val state = viewModel.state
    var showInputDialog by remember { mutableStateOf(false) }
    if (showInputDialog) {
        InputDialog(
            title = inputDialogTitle,
            placeholder = inputDialogPlaceholder,
            onDismiss = { showInputDialog = false },
            onConfirm = { name ->
                viewModel.saveCustomMeal(name)
                showInputDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = topAppBarText) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                }
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = {
                    navController.navigate(FoodGraph.Add(meal = meal, date = date))
                }
            ) {
                Icon(Icons.Default.Add, "")
            }
        }
    ) {
        val portions = state.portions
        val summary = state.summary
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                FoodSummary(
                    proteinLabel = stringResource(R.string.protein),
                    carbsLabel = stringResource(R.string.carbs),
                    fatsLabel = stringResource(R.string.fats),
                    calories = summary?.macros?.calories?.roundToInt() ?: 0,
                    protein = summary?.macros?.protein?.roundDecimals() ?: 0.0,
                    carbs = summary?.macros?.carbohydrates?.roundDecimals() ?: 0.0,
                    fats = summary?.macros?.fats?.roundDecimals() ?: 0.0,
                    backgroundColor = Color.Transparent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }

            if (portions.isNotEmpty()) {
                item {
                    Row(
                        modifier = modifier.fillParentMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showInputDialog = true }
                        ) {
                            Text(text = saveMealButtonText)
                        }
                    }
                }
            }
            itemsIndexed(portions) { index, item ->
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
                PortionItem(
                    onIncrease = { viewModel.increasePortion(item) },
                    onDecrease = { viewModel.decreasePortion(item) },
                    onClick = {
                        navController.navigate(
                            FoodGraph.Details(
                                meal = meal,
                                date = date,
                                amount = item.amountInGrams.roundToInt(),
                                barcode = item.barcode
                            )
                        )
                    },
                    item = item,
                )
            }

            item {
                if (portions.isNotEmpty()) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                MicrosSection(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    aminoTitle = aminoTitle,
                    macroTitle = macroTitle,
                    mineralTitle = mineralTitle,
                    vitaminTitle = vitaminTitle,
                    macros = summary?.macros ?: Macros(),
                    minerals = summary?.minerals ?: Minerals(),
                    vitamins = summary?.vitamins ?: Vitamins(),
                    aminoAcids = summary?.aminoAcids ?: AminoAcids()
                )

                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
private fun PortionItem(
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onClick: () -> Unit,
    item: Portion, modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable {
            onClick()
        }
    ){
        Spacer(modifier = Modifier.height(4.dp))
        ListItem(
            headlineContent = {
                Text(
                    text = item.name,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            },
            supportingContent = {
                Text(
                    text = "${item.macros.calories.roundToInt()} kcal, ${item.amountInGrams.roundToInt()}g",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecrease,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentPadding = PaddingValues(0.dp), // Remove internal padding
                        border = BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            "",
                            tint = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        )
                    }
                    OutlinedButton(
                        onClick = onIncrease,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentPadding = PaddingValues(0.dp), // Remove internal padding
                        border = BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            "",
                            tint = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun InputDialog(
    title: String,
    placeholder: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
                Text(text = "Ok")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Card {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text(text = placeholder) },
                    singleLine = true,
                    maxLines = 1,
                    keyboardActions = KeyboardActions(onDone = { onConfirm(name) }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            }
        }
    )

}










