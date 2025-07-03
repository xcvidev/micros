package com.xcvi.micros.ui.destinations.weight

import com.xcvi.micros.data.weight.WeightRepository
import com.xcvi.micros.data.weight.model.Weight
import com.xcvi.micros.domain.getEndOfWeek
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getStartOfWeek
import com.xcvi.micros.domain.getTimestamp
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.ui.BaseViewModel

class WeightViewModel(
    private val repository: WeightRepository
) : BaseViewModel<WeightViewModel.State>(State()) {

    data class State(
        val initialValue: Double = 0.0,     // must have to avoid 0.0 number picker initial value
        val numberPickerValue: Double = 0.0,
        val currentDate: Int = getToday(),
        val weights: List<Weight> = emptyList(),
        val deleteWeight: Weight? = null
    )

    fun getData(date: Int) {

    }

    fun setNumberPickerValue(value: Double) {
        updateData {
            copy(numberPickerValue = value)
        }
    }

    fun setDate(date: Int) {
        updateData {
            copy(currentDate = date)
        }
        getData(date)
    }

    fun setDeleteWeight(weight: Weight) {
        updateData {
            copy(deleteWeight = weight)
        }
    }

    fun save() {
        /*
        repository.weights.add(
            Weight(
                value = state.numberPickerValue,
                timestamp = state.currentDate.getTimestamp(8, 0)
            )
        )
        val weights = repository.weights.filter {
            it.timestamp.getEpochDate() >= state.currentDate.getStartOfWeek() &&
                    it.timestamp.getEpochDate() <= state.currentDate.getEndOfWeek()
        }.sortedByDescending { it.timestamp }
        updateData {
            copy(
                initialValue = weights.lastOrNull()?.value ?: 0.0,
                weights = weights
            )
        }

         */
    }

    fun delete() {
        if (state.deleteWeight == null) return
        //repository.weights.remove(state.deleteWeight)
        getData(state.currentDate)
    }
}









