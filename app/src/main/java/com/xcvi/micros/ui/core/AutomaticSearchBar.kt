package com.xcvi.micros.ui.core

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xcvi.micros.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutomaticSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onAutomaticSearch: () -> Unit,
    modifier: Modifier = Modifier,
    placeHolder: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
) {
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    maxLines = 1,
                    singleLine = true,
                    value = query,
                    onValueChange = { newQuery ->
                        onQueryChange(newQuery)
                        debounceJob?.cancel()  // cancel previous job if any
                        debounceJob = coroutineScope.launch {
                            delay(500)
                            onAutomaticSearch()  // always call, no gating here
                        }
                    },
                    placeholder = { Text(placeHolder) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                        disabledContainerColor = Transparent,
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                    ),
                    trailingIcon = trailingIcon,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions
                )
            }
        }

    }
}
