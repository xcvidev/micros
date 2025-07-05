package com.xcvi.micros.ui.feature_weight

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.weight.WeightRepository
import com.xcvi.micros.data.weight.model.Weight
import com.xcvi.micros.domain.Response
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

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

    fun getData(date: Int = state.currentDate) {
        viewModelScope.launch {
            val latest = when (val res = repository.get()) {
                is Response.Error -> 0.0
                is Response.Success -> res.data.weight
            }

            when (val res = repository.getWeek(state.currentDate)) {
                is Response.Error -> {}
                is Response.Success -> {
                    val weights = res.data
                    updateData {
                        copy(
                            initialValue = weights.firstOrNull()?.weight ?: latest,
                            weights = weights
                        )
                    }
                }
            }

        }
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
        getData()
    }

    fun setDeleteWeight(weight: Weight) {
        updateData {
            copy(deleteWeight = weight)
        }
    }

    fun save(onError: () -> Unit = {}) {
        viewModelScope.launch {
            when (repository.save(state.numberPickerValue, state.currentDate)){
                is Response.Error -> onError()
                is Response.Success -> getData()
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            val w = state.deleteWeight ?: return@launch
            repository.delete(w)
            getData()
        }
    }
}









