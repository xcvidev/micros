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

    fun getData(barcode: String, amount: Int, onFailure: () -> Unit){
        viewModelScope.launch {
            updateData {
                copy(
                    isLoading = true
                )
            }

            when(val res = repository.getPortion(barcode)){
                is Response.Error -> {
                    onFailure()
                    return@launch
                }
                is Response.Success -> {
                    val scaled = res.data.scaledTo(amount*1.0)
                    updateData {
                        copy(
                            portion = scaled,
                            numberPickerValue = amount,
                            numberPickerCalorie = scaled.macros.calories.roundToInt(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun updateNumberPickerValue(value: Int){
        val portion = state.portion ?: return
        if(value <= 0) return
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
    fun enhance(userDesc: String, onFailure: () -> Unit){
        viewModelScope.launch{
            val current = state.portion ?: return@launch
            updateData {
                copy(
                    isLoading = true
                )
            }

            when(
                val enhanced = repository.enhance(current.scaledTo(100.0), userDesc)
            ){
                is Response.Error -> {
                    onFailure()
                }
                is Response.Success -> {
                    println("MyLog: Enhanced Vitamin c: ${enhanced.data.macros.vitaminC}")
                    updateData {
                        copy(
                            portion = enhanced.data.scaledTo(numberPickerValue*1.0),
                        )
                    }
                }
            }
            updateData {
                copy(
                    isLoading = false
                )
            }
        }
    }
}