package com.xcvi.micros.ui.destination.food.scan

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.destination.food.add.AddViewModel

class ScanViewModel(
    private val repository: FoodRepository
): BaseViewModel<ScanViewModel.State>(State()){

    class State
}