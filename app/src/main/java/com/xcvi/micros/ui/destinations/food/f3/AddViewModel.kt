package com.xcvi.micros.ui.destinations.food.f3

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.domain.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

class AddViewModel(
    private val repository: FoodRepository
) : BaseViewModel<AddViewModel.State>(State()) {


    data class State(
        val isGenerating: Boolean = false,
        val isStreaming: Boolean = false,

        val recents: List<Portion> = emptyList(),
        val filtered: List<Portion> = emptyList(),
        val generated: Portion? = null
    )


    fun getData() {
        viewModelScope.launch {
            when (val res = repository.getRecents()) {
                is Response.Success -> updateData {
                    copy(
                        recents = res.data,
                        filtered = res.data,
                        isGenerating = false,
                        isStreaming = false,
                        generated = null,
                    )
                }

                is Response.Error -> {}
            }
        }
    }

    fun generate(query: String, onFailure: () -> Unit) {
        if (state.isGenerating || state.isStreaming) return
        if (query.isBlank()) {
            onFailure()
            return
        }
        viewModelScope.launch {
            updateData {
                copy(
                    isGenerating = true,
                    generated = null
                )
            }

            when (val res = repository.generate(query)) {
                is Response.Error -> if(state.isGenerating){
                    updateData {
                        copy(
                            isGenerating = false,
                            isStreaming = false,
                            generated = null,
                            filtered = recents
                        )
                    }
                    onFailure()
                }

                is Response.Success -> if(state.isGenerating){
                    updateData {
                        copy(
                            generated = res.data,
                            filtered = emptyList(),
                            isGenerating = false,
                            isStreaming = true
                        )
                    }
                }
            }
        }
    }

    fun onFinishedStreaming() {
        updateData {
            copy(
                isStreaming = false,
            )
        }
    }

    fun stop() {
        updateData {
            copy(
                isGenerating = false,
                isStreaming = false,
                generated = null,
                filtered = recents
            )
        }
    }

    fun filter(query: String) {
        if (query == "") {
            updateData {
                copy(
                    filtered = recents,
                    generated = null
                )
            }
            return
        }
        updateData {
            copy(
                filtered = recents.filter { it.name.contains(query, ignoreCase = true) },
                generated = null
            )
        }
    }
}