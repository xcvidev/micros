package com.xcvi.micros.ui.destinations.weight

import com.xcvi.micros.domain.Weight
import com.xcvi.micros.domain.WeightRepository
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.ui.BaseViewModel

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
                    it.timestamp.getEpochDate() == getToday()
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