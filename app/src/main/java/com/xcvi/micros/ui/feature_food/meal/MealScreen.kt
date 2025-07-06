package com.xcvi.micros.ui.feature_food.meal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.displayName
import com.xcvi.micros.domain.roundDecimals
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.rememberShakeOffset
import com.xcvi.micros.ui.FoodGraph
import com.xcvi.micros.ui.feature_food.FoodSummary
import com.xcvi.micros.ui.feature_food.dashoard.FoodViewModel
import com.xcvi.micros.ui.feature_food.dashoard.MicrosSection
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreen(
    date: Int,
    meal: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: FoodViewModel,
    topAppBarText: String = stringResource(R.string.meal) + " $meal",
    inputDialogTitle : String= stringResource(R.string.save),
    inputDialogPlaceholder: String = stringResource(R.string.enter_a_name),
    saveMealButtonText : String= stringResource(R.string.save_custom_meal),
    macroTitle: String = stringResource(R.string.macros),
    aminoTitle : String= stringResource(R.string.amino_acids),
    mineralTitle : String= stringResource(R.string.minerals),
    vitaminTitle : String= stringResource(R.string.vitamins),
) {

    OnNavigation {
        viewModel.getData()
    }

    val state = viewModel.state

    val portions = state.meals.getOrNull(meal -1)?.portions ?: emptyList()
    val summary = state.meals.getOrNull(meal -1)?.summary ?: Portion()

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    var showInputDialog by remember { mutableStateOf(false) }
    if (showInputDialog) {
        InputDialog(
            title = inputDialogTitle,
            placeholder = inputDialogPlaceholder,
            onDismiss = { showInputDialog = false },
            onConfirm = { name ->
                viewModel.saveCustomMeal(
                    name = name,
                    portions = portions,
                    onError = { shakeTrigger = true },
                    onSuccess = { showInputDialog = false }
                )
            },
            offset = shakeOffset
        )
    }
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog) {
        DeleteDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.delete {
                    shakeTrigger = true
                }
                showDeleteDialog = false
            },
            offset = shakeOffset,
            itemName = state.deletePortion?.name ?: ""
        )
    }

    Scaffold(
        modifier = modifier.offset(x = shakeOffset),
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
                    navController.navigate(FoodGraph.Search(meal = meal, date = date))
                }
            ) {
                Icon(Icons.Default.Add, "")
            }
        }
    ) {

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
                    calories = summary.macros.calories.roundToInt(),
                    protein = summary.macros.protein.roundDecimals(),
                    carbs = summary.macros.carbohydrates.roundDecimals(),
                    fats = summary.macros.fats.roundDecimals(),
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
                    onIncrease = { viewModel.increasePortion(item) { shakeTrigger = true } },
                    onDecrease = {
                        viewModel.decreasePortion(
                            portion = item,
                            onError = { shakeTrigger = true },
                            onDelete = { showDeleteDialog = true }
                        )
                    },
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
                    onLongClick = {
                        showDeleteDialog = true
                        viewModel.setDeletePortion(item)
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
                    macros = summary.macros,
                    minerals = summary.minerals,
                    vitaminsFull = summary.vitaminsFull,
                    aminoAcids = summary.aminoAcids
                )
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PortionItem(
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    item: Portion,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        ListItem(
            headlineContent = {
                Text(
                    text = item.displayName(),
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
    offset: Dp
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier.offset(x = offset),
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
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }
    )

}



@Composable
fun DeleteDialog(
    itemName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    offset: Dp
) {
    AlertDialog(
        modifier = Modifier.offset(x = offset),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = stringResource(R.string.delete))
            }
        },
        title = {
            Text(text = stringResource(R.string.delete))
        },
        text = {
            val text = stringResource(R.string.delete_confirm_text).removeSuffix("?")
            Text("$text\n$itemName?")
        }
    )

}










