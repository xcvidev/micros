package com.xcvi.micros.ui.destinations.food.details

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.ui.BaseViewModel

class DetailsViewModel(
    private val repository: FoodRepository
): BaseViewModel<DetailsViewModel.State>(State()){

    data class State(
        val portion: Portion? = null,
        val numberPickerValue: Int = 0
    )

    fun getData(meal: Int, date: Int, barcode: String, amount: Int){

    }

    fun eat(date: Int, meal: Int, onFailure: () -> Unit, onSuccess: () -> Unit){

    }
    fun updateNumberPickerValue(value: Int){
        updateData {
            copy(numberPickerValue = value)
        }
    }
}