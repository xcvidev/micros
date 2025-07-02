package com.xcvi.micros.ui.destinations.food.details

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.xcvi.micros.ui.core.LoadingIndicator
import com.xcvi.micros.ui.core.NumberPicker
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.rememberShakeOffset
import com.xcvi.micros.ui.destinations.FoodGraph
import com.xcvi.micros.ui.destinations.food.dashboard.MicrosSection
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
    errorTitle: String ,
    errorMessage: String ,
    aminoTitle: String ,
    macroTitle: String ,
    mineralTitle: String,
    vitaminTitle: String,

) {
    val state = viewModel.state
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
        viewModel.getData(meal, date, barcode, amount) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                navController.popBackStack()
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text(text = "Ok")
                }
            },
            title = {
                Text(text = errorTitle)
            },
            text = {
                Text(text = errorMessage)
            }
        )

    } else {
        when {
            state.isLoading -> {
                LoadingIndicator(modifier = modifier.fillMaxSize())
            }

            state.portion != null -> {
                Scaffold(
                    modifier = modifier.fillMaxSize().pointerInput(Unit) {
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
                                IconButton(onClick = { navController.popBackStack() }) {
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
                                    text = state.portion.name,
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
                                onValueChange = viewModel::updateNumberPickerValue,
                                clickGranularity = 1,
                                onImeAction = {
                                    keyboard?.hide()
                                    focusManager.clearFocus()
                                }

                            )
                        }
                        item {
                            Button(
                                modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
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
                            MicrosSection(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                aminoTitle = aminoTitle,
                                macroTitle = macroTitle,
                                mineralTitle = mineralTitle,
                                vitaminTitle = vitaminTitle,
                                macros = state.portion.macros,
                                minerals = state.portion.minerals,
                                vitamins = state.portion.vitamins,
                                aminoAcids = state.portion.aminoAcids
                            )
                        }
                    }
                }
            }

        }

    }

}


@Composable
fun NutritionLabel() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        val white = Color.White
        val gray = Color.Gray

        LabelText("Dimensioni porzione 100 g", white)
        Divider(color = white, thickness = 1.dp)
        LabelText("Quantità per porzione", white)
        Spacer(modifier = Modifier.height(8.dp))
        LabelText("Calorie 130", white)

        Divider(color = white, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        LabelText("% valori giornalieri", white)

        NutritionItem("Grassi totali", "0,2 g", "0%", white)
        NutritionItem("Grassi saturi", "0 g", "0%", gray)
        NutritionItem("Colesterolo", "0 mg", "0%", white)
        NutritionItem("Sodio", "1 mg", "0%", white)
        NutritionItem("Carboidrati totali", "28,1 g", "9%", white)
        NutritionItem("Fibra dietetica", "0,4 g", "2%", gray)
        NutritionItem("Zuccheri", "0 g", "", gray)
        NutritionItem("Proteine", "2,6 g", "", white)

        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LabelText("Vitamina A 0%", white)
            LabelText("Vitamina C 0%", white)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            LabelText("Calcio 1%", white)
            LabelText("Ferro 7%", white)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "* I valori giornalieri percentuali sono basati su una dieta da 2000 calorie. " +
                    "I valori giornalieri potrebbero essere maggiori o inferiori in base al fabbisogno di calorie.",
            color = gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun NutritionItem(name: String, amount: String, percent: String, textColor: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$name $amount", color = textColor)
        if (percent.isNotBlank()) {
            Text(text = percent, color = textColor)
        }
    }
}

@Composable
fun LabelText(text: String, color: Color) {
    Text(text = text, color = color, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
}