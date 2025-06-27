package com.xcvi.micros.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay


@Composable
fun OnNavigation(
    vararg keys: Any?,
    delay: Long = 0L,
    block: () -> Unit
) {
    val currentBlock by rememberUpdatedState(block)
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner, *keys) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            delay(delay)
            currentBlock()
        }
    }
}