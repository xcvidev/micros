package com.xcvi.micros.ui.feature_food.details

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.scaledCalories
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.data.food.scaledTo
import com.xcvi.micros.domain.Response
import com.xcvi.micros.ui.BaseViewModel
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

            when(val res = repository.getPortion(meal, date, barcode, amount)){
                is Response.Error -> {
                    onFailure()
                    return@launch
                }
                is Response.Success -> {
                    updateData {
                        copy(
                            portion = res.data,
                            numberPickerValue = res.data.amountInGrams.roundToInt(),
                            numberPickerCalorie = res.data.macros.calories.roundToInt(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun updateNumberPickerValue(value: Int){
        val portion = state.portion ?: return
        updateData {
            copy(
                numberPickerValue = value,
                numberPickerCalorie = portion.scaledCalories(value),
                portion = portion.scaledTo(value*1.0)
            )
        }
    }

    fun eat(date: Int, meal: Int, onFailure: () -> Unit, onSuccess: () -> Unit){
        viewModelScope.launch{
            val current = state.portion?.copy(
                date = date,
                meal = meal
            ) ?: return@launch
            if(state.numberPickerValue <= 0){
                onFailure()
                return@launch
            }
            when(repository.updatePortion(current, state.numberPickerValue.toDouble())){
                is Response.Error -> {
                    onFailure()
                    return@launch
                }
                is Response.Success -> {
                    onSuccess()
                }
            }
        }
    }
}