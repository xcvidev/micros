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
        val isStreaming: Boolean = false,

        val portions: List<Portion> = emptyList(),
        val generated: Portion? = null
    )

    fun getData() {
        updateData {
            copy(
                portions = repository.portions.subList(0,10),
                generated = null
            )
        }
    }

    fun generate(date: Int, meal: Int, onFailure: () -> Unit) {
        if(state.isGenerating || state.isStreaming) return
        if(state.query.isBlank()) {
            onFailure()
            return
        }
        viewModelScope.launch {
            updateData {
                copy(
                    isStreaming = true,
                    isGenerating = true,
                    generated = null
                )
            }
            delay(3000)
            if(state.isGenerating){
                updateData {
                    copy(
                        generated = repository.portions.random(),
                        portions = emptyList(),
                        isGenerating = false,
                        isStreaming = true
                    )
                }
            }
        }
    }

    fun setQuery(query: String) {
        if(state.isGenerating) return
        updateData {
            copy(
                query = query,
                portions = repository.portions.filter { it.name.contains(other = query, ignoreCase = true) },
                generated = null
            )
        }
    }

    fun stop(){
        updateData {
            copy(
                query = "",
                isGenerating = false,
                isStreaming = false,
                generated = null,
                portions = repository.portions
            )
        }
    }

    fun onFinishedStreamingText(){
        updateData {
            copy(
                isStreaming = false,
            )
        }
    }
}