package com.xcvi.micros.ui.destination.food.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
    date: Int,
    meal: Int,
    amount: Int,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = koinViewModel()
) {

}