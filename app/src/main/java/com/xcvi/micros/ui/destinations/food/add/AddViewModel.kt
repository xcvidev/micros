package com.xcvi.micros.ui.destinations.food.add

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

    }

    fun generate(date: Int, meal: Int) {

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