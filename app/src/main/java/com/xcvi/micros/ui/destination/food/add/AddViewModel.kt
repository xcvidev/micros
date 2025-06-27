package com.xcvi.micros.ui.destination.food.add

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddViewModel(
    private val repository: FoodRepository
) : BaseViewModel<AddViewModel.State>(State()) {

    data class State(
        val query: String = "",
        val portions: List<Portion> = emptyList(),
        val isGenerating: Boolean = false,
    )

    fun getData() {
        updateData { copy(portions = repository.portions.sortedByDescending { it.date }) }
    }

    fun setQuery(query: String) {
        updateData {
            copy(
                query = query,
                portions = repository.portions.filter { it.name.contains(query) }
            )
        }
    }

    fun generate(date: Int, meal: Int) {
        viewModelScope.launch {
            updateData { copy(isGenerating = true) }
            delay(3000)
            val generated = repository.generate(date = date, meal = meal, description = state.query)
            if (generated == null) {
                updateData { copy(isGenerating = false) }
                return@launch
            }
            updateData { copy(isGenerating = false, portions = listOf(generated)) }
        }
    }
}