package com.xcvi.micros.ui.destination.food.add

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.ui.BaseViewModel

class AddViewModel(
    private val repository: FoodRepository
): BaseViewModel<AddViewModel.State>(State()){

    class State
}