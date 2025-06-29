package com.xcvi.micros.ui.destinations.food.scan

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.ui.BaseViewModel

class ScanViewModel(
    private val repository: FoodRepository
): BaseViewModel<ScanViewModel.State>(State()){

    class State
}