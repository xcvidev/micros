package com.xcvi.micros.ui.destination.food.details

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.destination.food.add.AddViewModel

class DetailsViewModel(
    private val repository: FoodRepository
): BaseViewModel<DetailsViewModel.State>(State()){

    class State
}