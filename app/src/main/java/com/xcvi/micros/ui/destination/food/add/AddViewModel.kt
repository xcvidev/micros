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
        val isGenerating: Boolean = false,

        val portions: List<Portion> = emptyList(),
        val generated: Portion? = null
    )

    fun getData() {
        updateData {
            copy(
                portions = repository.portions.sortedByDescending { it.date },
            )
        }
    }

    fun generate(date: Int, meal: Int) {
        viewModelScope.launch {
            updateData { copy(isGenerating = true) }
            delay(3000)
            val generated = repository.generate(date = date, meal = meal, description = state.query)
            updateData { copy(isGenerating = false, portions = emptyList(), generated = generated) }
        }
    }

    fun setQuery(query: String) {
        updateData {
            copy(
                query = query,
                portions = repository.portions.filter { it.name.contains(other = query, ignoreCase = true) },
                generated = null
            )
        }
    }
}