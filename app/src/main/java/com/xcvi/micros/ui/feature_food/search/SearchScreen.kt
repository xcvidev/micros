package com.xcvi.micros.ui.feature_food.search

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.xcvi.micros.R
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.displayName
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.getLocalizedText
import com.xcvi.micros.ui.FoodGraph
import com.xcvi.micros.ui.core.AnimatedDots
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
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
    placeHolder: String,
    generatingIndicatorText: String = stringResource(R.string.generating),
    generatedLabel: String = stringResource(R.string.generated),
    searchResultsLabel: String = stringResource(R.string.results),
    proteinLabel: String = stringResource(R.string.protein),
    carbsLabel: String = stringResource(R.string.carbs),
    fatsLabel: String = stringResource(R.string.fats),
    recentlyAddedText: String = stringResource(R.string.recently_added)
) {
    OnNavigation {
        viewModel.getData()
    }

    val context = LocalContext.current
    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset = rememberShakeOffset(shakeTrigger) {
        shakeTrigger = false // reset after animation
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()

    val isStreaming = viewModel.state.isStreaming
    val isQuerying = viewModel.state.isQuerying
    val isGenerating = viewModel.state.isGenerating

    val generated = viewModel.state.generated
    val recents = viewModel.state.recents
    val searchResults = viewModel.state.searchResults
    var query by remember { mutableStateOf("") }

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

    Scaffold(
        modifier = modifier
            .offset(x = shakeOffset)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                )
            },
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack,"")
                    }
                }
            )
        }
    ) { padding ->
        LaunchedEffect(lazyListState.isScrollInProgress) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }

        LazyColumn(
            state = lazyListState,
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {

            item {
                SearchField(
                    placeHolder = placeHolder,
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.filter(query)
                    },
                    isQuerying = isQuerying,
                    isStreaming = isStreaming,
                    onScan = {
                        navController.navigate(
                            FoodGraph.Scan(
                                meal = meal,
                                date = date
                            )
                        )
                    },
                    onClick = {
                        if (isQuerying || isStreaming) {
                            viewModel.resetState()
                        } else {
                            viewModel.find(query) { error ->
                                if (error == Failure.Network) {
                                    toast(context, error)
                                }
                                shakeTrigger = true
                            }
                        }
                        query = ""
                    }
                )
            }


            if (isGenerating || generated != null) {
                item {
                    GeneratedItem(
                        modifier = modifier.padding(vertical = 18.dp),
                        generatedLabel = generatedLabel,
                        generatingIndicatorText = generatingIndicatorText,
                        isStreaming = isStreaming,
                        proteinLabel = proteinLabel,
                        carbsLabel = carbsLabel,
                        fatsLabel = fatsLabel,
                        portion = generated,
                        onClick = {
                            if (generated != null) {
                                goToItem(generated)
                            }
                        }
                    ) {
                        viewModel.stopStreaming()
                    }
                }
            }

            if (isQuerying && searchResults.isEmpty()) {
                item {
                    StreamingText(
                        modifier = modifier.padding(top = 12.dp, start = 8.dp),
                        text = "Searching...",
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            if (searchResults.isNotEmpty()) {
                item {
                    ResultLabel(
                        text = searchResultsLabel,
                        modifier = Modifier.padding(top = 12.dp, start = 8.dp)
                    )
                    searchResults.forEach {
                        PortionItem(
                            portion = it,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) { goToItem(it) }
                    }
                }
            }


            if (recents.isNotEmpty()) {
                item {
                    ResultLabel(
                        text = recentlyAddedText,
                        modifier = Modifier.padding(top = 12.dp, start = 8.dp)
                    )
                    viewModel.state.recents.forEach {
                        PortionItem(
                            portion = it,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) { goToItem(it) }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }

        }
    }
}


@Composable
fun SearchField(
    placeHolder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isQuerying: Boolean,
    isStreaming: Boolean,
    onClick: () -> Unit,
    onScan: () -> Unit,
) {

    val icon = if (isQuerying || isStreaming) {
        Icons.Default.Stop
    } else {
        Icons.Default.ArrowUpward
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
                    if (isQuerying || isStreaming) {
                        //AnimatedDots()
                    } else {
                        StreamingText(placeHolder)
                    }
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
                        onClick()
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
                    onClick = onClick,
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


/*
TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.filter(query)
                    }
                )
                Button(

                ) {
                    Text(
                        text = if (isQuerying || isStreaming) {
                            "X"
                        } else {
                            "Search"
                        }
                    )

                }
 */

@Composable
fun PortionItem(
    portion: Portion,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
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
    modifier: Modifier = Modifier,
    generatingIndicatorText: String,
    generatedLabel: String,
    isStreaming: Boolean,
    proteinLabel: String,
    carbsLabel: String,
    fatsLabel: String,
    portion: Portion?,
    onClick: () -> Unit,
    onFinished: () -> Unit
) {

    if (portion == null) {
        Box(modifier = modifier) {
            StreamingTextCard(
                title = generatingIndicatorText,
                subtitle = "",
                body = "",
                onClick = {},
                onFinished = {},
                isStreaming = isStreaming
            )
        }
    } else {
        Box(modifier = modifier) {
            StreamingTextCard(
                title = portion.displayName(),
                subtitle = "${portion.macros.calories.roundToInt()} kcal, ${portion.amountInGrams.roundToInt()} g",
                body = "$proteinLabel: ${portion.macros.protein} g\n$carbsLabel: ${portion.macros.carbohydrates} g\n$fatsLabel: ${portion.macros.fats} g ",
                onClick = onClick,
                onFinished = onFinished,
                isStreaming = isStreaming,
                action = {
                    ResultLabel(generatedLabel)
                }
            )
        }
    }
}


fun toast(context: Context, failure: Failure) {
    Toast.makeText(context, failure.getLocalizedText(context), Toast.LENGTH_LONG).show()
}


@Composable
fun ResultLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = MaterialTheme.typography.bodySmall.fontSize,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    )
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










