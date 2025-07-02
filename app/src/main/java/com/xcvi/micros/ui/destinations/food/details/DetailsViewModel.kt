package com.xcvi.micros.ui.destinations.food.details

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.entity.Macros
import com.xcvi.micros.data.entity.Portion
import com.xcvi.micros.data.scaledCalories
import com.xcvi.micros.data.scaledTo
import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DetailsViewModel(
    private val repository: FoodRepository
): BaseViewModel<DetailsViewModel.State>(State()){

    data class State(
        val isLoading: Boolean = false,
        val portion: Portion? = null,
        val numberPickerValue: Int = 0,
        val numberPickerCalorie: Int = 0,
    )

    fun getData(meal: Int, date: Int, barcode: String, amount: Int, onFailure: () -> Unit){
        viewModelScope.launch {
            updateData {
                copy(
                    isLoading = true
                )
            }
//            delay(1000)

            val portion = repository.portions.random()
            updateData {
                copy(
                    portion = portion,
                    numberPickerValue = portion.amountInGrams.roundToInt(),
                    numberPickerCalorie = portion.macros.calories.toInt(),
                    isLoading = false
                )
            }


        }
    }

    fun updateNumberPickerValue(value: Int){
        val portion = state.portion ?: return
        updateData {
            copy(numberPickerValue = value, numberPickerCalorie = portion.scaledCalories(value))
        }
    }

    fun eat(date: Int, meal: Int, onFailure: () -> Unit, onSuccess: () -> Unit){
        if(state.numberPickerValue <= 0){
            onFailure()
            return
        }
        //repository.eat(portion, state.numberPickerValue)
        onSuccess()
    }
}