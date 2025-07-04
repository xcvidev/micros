package com.xcvi.micros.ui.core

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.NavGraphBuilder

inline fun <reified T : Any> NavGraphBuilder.slidingComposable(
    durationMillis: Int = 500,
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable<T>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                )
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                )
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                )
            )
        },
        content = content
    )
}



/*
inline fun <reified T : Any> NavGraphBuilder.slidingComposable(
    durationMillis: Int = 500,
    noinline content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable<T>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(durationMillis)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(durationMillis)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(durationMillis)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(durationMillis)
            )
        },
        content = content
    )
}

 */
