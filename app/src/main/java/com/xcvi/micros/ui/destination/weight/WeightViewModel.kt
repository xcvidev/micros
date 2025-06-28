package com.xcvi.micros.ui.destination.weight

import com.xcvi.micros.domain.Weight
import com.xcvi.micros.domain.WeightRepository
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.core.getLocalDateTime
import com.xcvi.micros.ui.core.getToday

class WeightViewModel(private val repository: WeightRepository): BaseViewModel<WeightViewModel.State>(State())  {
    data class State(
        val numberPickerValue: Double = 0.0,
        val currentWeight: Weight? = null,
        val weights: List<Weight> = emptyList()
    )

    fun getData(){
        updateData {
            copy(
                weights = repository.weights.filter {
                    getLocalDateTime(it.timestamp).date.toEpochDays() == getToday()
                },
                currentWeight = repository.weights.first(),
                numberPickerValue = repository.weights.first().value
            )
        }
    }

    fun setNumberPickerValue(value: Double){
        updateData {
            copy(numberPickerValue = value)
        }
    }

    fun save(){
        repository.weights.add(
            Weight(
                value = state.numberPickerValue,
                timestamp = System.currentTimeMillis()
            )
        )
        getData()
    }

    fun delete(weight: Weight){
        repository.weights.remove(weight)
        getData()
    }
}