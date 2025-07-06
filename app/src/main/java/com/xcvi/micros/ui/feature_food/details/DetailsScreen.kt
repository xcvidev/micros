package com.xcvi.micros.ui.feature_food.details

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.data.food.model.entity.displayName
import com.xcvi.micros.ui.FoodGraph
import com.xcvi.micros.ui.core.LoadingIndicator
import com.xcvi.micros.ui.core.NumberPicker
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.rememberShakeOffset
import com.xcvi.micros.ui.feature_food.dashoard.MicrosSection
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    date: Int,
    meal: Int,
    amount: Int,
    barcode: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = koinViewModel(),
    errorTitle: String = stringResource(R.string.error),
    errorMessage: String = stringResource(R.string.something_went_wrong),
    aminoTitle: String = stringResource(R.string.amino_acids_for_100_g),
    macroTitle: String = stringResource(R.string.macros_for_100_g),
    mineralTitle: String = stringResource(R.string.minerals_for_100_g),
    vitaminTitle: String = stringResource(R.string.vitamins_for_100_g),
) {
    val state = viewModel.state
    var desc by remember { mutableStateOf("") }
    var shakeTrigger by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val lazyState = rememberLazyListState()
    if (lazyState.isScrollInProgress) {
        focusManager.clearFocus()
        keyboard?.hide()
    }

    OnNavigation {
        viewModel.getData(amount = amount, barcode = barcode) {
            shakeTrigger = true
        }
    }

    val onBack = {
        navController.popBackStack()
    }


    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        if(desc.isNotBlank()){
                            keyboard?.hide()
                            focusManager.clearFocus()
                            showDialog = false
                            viewModel.enhance(
                                userDesc = desc
                            ) {
                                shakeTrigger = true
                            }
                        } else{
                            shakeTrigger = true
                        }
                    }
                ) {
                    Text(text = "Ok")
                }
            },
            title = {
                Text(stringResource(R.string.provide_food_description))
            },
            text = {
                Card{
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = desc,
                        onValueChange = {
                            desc = it
                        },
                        singleLine = true,
                        maxLines = 1,
                        placeholder = {
                            Text(text = stringResource(R.string.description))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )
                }
            }
        )

    }

    state.portion?.let { portion ->
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            keyboard?.hide()
                        }
                    )
                },
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { onBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = ""
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                state = lazyState,
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .offset(x = shakeOffset),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = portion.displayName(),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.numberPickerCalorie} kcal, ${state.numberPickerValue} g",
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = MaterialTheme.typography.bodyMedium.fontWeight,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    NumberPicker(
                        initialValue = state.numberPickerValue,
                        onValueChange = {
                            if (it > 0) {
                                viewModel.updateNumberPickerValue(it)
                            }
                        },
                        clickGranularity = 1,
                        onImeAction = {
                            keyboard?.hide()
                            focusManager.clearFocus()
                        }

                    )
                }
                item {
                    Button(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        onClick = {
                            viewModel.eat(
                                date = date,
                                meal = meal,
                                onFailure = {
                                    shakeTrigger = true
                                },
                                onSuccess = {
                                    navController.navigate(
                                        FoodGraph.Meal(date = date, meal = meal)
                                    ) {
                                        popUpTo(
                                            FoodGraph.label
                                        ) {
                                            inclusive = false
                                        }
                                    }
                                }
                            )
                        }
                    ) {
                        Text(text = "Ok")
                    }
                }

                item {
                    TextButton(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 8.dp),
                        onClick = { showDialog = true }
                    ) {
                        Text(text = stringResource(R.string.enhance))
                    }
                }

                item {
                    MicrosSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        aminoTitle = aminoTitle,
                        macroTitle = macroTitle,
                        mineralTitle = mineralTitle,
                        vitaminTitle = vitaminTitle,
                        macros = portion.macros,
                        minerals = portion.minerals,
                        vitaminsFull = portion.vitaminsFull,
                        aminoAcids = portion.aminoAcids
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

        }
    }

    if (state.isLoading) {
        LoadingIndicator(modifier = modifier.fillMaxSize())
    }
}




