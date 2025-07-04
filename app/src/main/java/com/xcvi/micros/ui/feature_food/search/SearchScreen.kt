package com.xcvi.micros.ui.feature_food.search

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.displayName
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.getLocalizedText
import com.xcvi.micros.ui.FoodGraph
import com.xcvi.micros.ui.core.OnNavigation
import com.xcvi.micros.ui.core.StreamingText
import com.xcvi.micros.ui.core.StreamingTextCard
import com.xcvi.micros.ui.core.rememberShakeOffset
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    date: Int,
    meal: Int,
    navController: NavHostController,
    viewModel: SearchViewModel = koinViewModel(),
    generatedLabel: String = stringResource(R.string.generated),
    searchResultsLabel: String = stringResource(R.string.results),
    generatingIndicatorText: String = stringResource(R.string.generating),
    proteinLabel: String = stringResource(R.string.protein),
    carbsLabel: String = stringResource(R.string.carbs),
    fatsLabel: String = stringResource(R.string.fats),
    recentlyAddedText: String = stringResource(R.string.recently_added)
) {
    OnNavigation {
        viewModel.getData()
    }

    val context = LocalContext.current
    var hasStreamed by remember { mutableStateOf(false) }
    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }

    val goToItem = { portion: Portion ->
        navController.navigate(
            FoodGraph.Details(
                date = date,
                meal = meal,
                amount = portion.amountInGrams.roundToInt(),
                barcode = portion.barcode
            )
        )
    }

    var query by remember { mutableStateOf("") }
    LazyColumn(
        modifier = Modifier
            .padding(24.dp)
            .offset(x = shakeOffset),
    ) {
        item {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.filter(query)
                }
            )
            Button(
                onClick = {
                    viewModel.find(query) { error ->
                        if (error == Failure.Network) {
                            toast(context, error)
                        }
                        shakeTrigger = true
                    }
                }
            ) {
                Text(text = "Search")
            }
        }

        val isStreaming = viewModel.state.isStreaming
        val isGenerating = viewModel.state.isGenerating
        val generated = viewModel.state.generated
        if (isGenerating) {
            item { StreamingText(generatingIndicatorText) }
        }
        if (isStreaming || generated != null) {
            item {
                Text(generatedLabel)

                GeneratedItem(
                    proteinLabel = proteinLabel,
                    carbsLabel = carbsLabel,
                    fatsLabel = fatsLabel,
                    portion = generated ?: Portion(),
                    onClick = {
                        if (generated != null) {
                            goToItem(generated)
                        }
                    }
                ) {
                    hasStreamed = true
                }
            }
        }

        if (viewModel.state.recents.isNotEmpty()) {
            item {
                Text(text = "Recently Added")
                viewModel.state.recents.forEach {
                    PortionItem(it) { goToItem(it) }
                }
            }
        }


        if (viewModel.state.searchResults.isNotEmpty()) {
            item {
                Text(text = "Search Results")
                viewModel.state.searchResults.forEach {
                    PortionItem(it) { goToItem(it) }
                }
            }
        }
    }


}

fun toast(context: Context, failure: Failure) {
    Toast.makeText(context, failure.getLocalizedText(context), Toast.LENGTH_LONG).show()
}

/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    generatedLabel: String,
    searchResultsLabel: String,
    recentlyAddedText: String,
    placeHolder: String,
    generatingIndicatorText: String,
    proteinLabel: String,
    carbsLabel: String,
    fatsLabel: String,
    date: Int,
    meal: Int,
    navController: NavHostController,
    viewModel: AddViewModel
) {

    val placeHolderList = listOf(
        stringResource(R.string.describe_your_food),
        stringResource(R.string.try_a_plate_of_pasta_with_tomato_sauce),
        stringResource(R.string.describe_what_you_ate),
    )
    val placeHolder = remember { placeHolderList.random() }


    val context = LocalContext.current
    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    var query by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // State
    val isGenerating = viewModel.state.isGenerating
    val isStreaming = viewModel.state.isStreaming
    val recents = viewModel.state.filtered
    val searchResults = viewModel.state.searchResult

    // Functions
    val onGenerate = {
        viewModel.search(query = query) { error ->
            when (error) {
                is Failure.EmptyResult -> {
                    Toast.makeText(context, error.getLocalizedText(context), Toast.LENGTH_SHORT).show()
                }
                is Failure.Network -> {
                    Toast.makeText(context, error.getLocalizedText(context), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    shakeTrigger = true
                }
            }

        }
        query = ""
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    val onScan = {
        navController.navigate(
            FoodGraph.Scan(
                meal = meal,
                date = date
            )
        )
    }

    val onItemClick = { generated: Portion ->
        viewModel.setPortion(
            portion = generated,
            onNavigation = {
                navController.navigate(
                    FoodGraph.Details(
                        date = date,
                        meal = meal,
                        amount = generated.amountInGrams.roundToInt(),
                        barcode = generated.barcode
                    )
                )
            },
            onFailure = {
                shakeTrigger = true
            }
        )
    }

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
                        onGenerate = onGenerate,
                        onScan = onScan,
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

                    searchResults.isEmpty() -> {
                        item { ResultLabel(text = recentlyAddedText) }
                        items(recents) {
                            PortionItem(
                                portion = it,
                                onClick = { onItemClick(it) }
                            )
                            HorizontalDivider(
                                thickness = 0.25.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                            )
                        }
                        if (recents.isNotEmpty()) {
                            item {
                                HorizontalDivider(
                                    thickness = 0.25.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                                )
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }

                    else -> {
                        item {
                            if(searchResults.generated != null) {
                                ResultLabel(text = generatedLabel)
                                Spacer(modifier = Modifier.height(8.dp))
                                GeneratedItem(
                                    proteinLabel = proteinLabel,
                                    carbsLabel = carbsLabel,
                                    fatsLabel = fatsLabel,
                                    portion = searchResults.generated,
                                    onClick = { onItemClick(searchResults.generated) },
                                    onFinished = {
                                        viewModel.onFinishedStreaming()
                                    }
                                )
                            } else {
                                viewModel.onFinishedStreaming()
                            }
                        }

                        if(searchResults.portions.isNotEmpty()){
                            item {
                                Spacer(modifier = Modifier.height(18.dp))
                                ResultLabel(text = searchResultsLabel)
                            }
                            items(searchResults.portions) {
                                PortionItem(
                                    portion = it,
                                    onClick = { onItemClick(it) }
                                )
                                HorizontalDivider(
                                    thickness = 0.25.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                                )
                            }
                            if (recents.isNotEmpty()) {
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
    }



}





@Composable
fun ResultLabel(text: String) {
    Text(
        modifier = Modifier.padding(top = 12.dp, start = 8.dp),
        text = text,
        fontSize = MaterialTheme.typography.bodySmall.fontSize,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    )
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
*/

@Composable
fun PortionItem(
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
                text = portion.displayName(),
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
        title = portion.displayName(),
        subtitle = "${portion.macros.calories.roundToInt()} kcal, ${portion.amountInGrams.roundToInt()} g",
        body = "$proteinLabel: ${portion.macros.protein} g\n$carbsLabel: ${portion.macros.carbohydrates} g\n$fatsLabel: ${portion.macros.fats} g ",
        onClick = onClick,
        onFinished = onFinished
    )
}











