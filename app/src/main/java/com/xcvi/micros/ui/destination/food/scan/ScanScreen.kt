package com.xcvi.micros.ui.destination.food.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScanScreen(
    date: Int,
    meal: Int,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = koinViewModel()
) {

}