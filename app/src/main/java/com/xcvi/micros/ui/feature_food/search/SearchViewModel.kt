package com.xcvi.micros.ui.feature_food.search

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.displayName
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val repository: FoodRepository
) : BaseViewModel<SearchViewModel.State>(State()) {


    data class State(
        val isStreaming: Boolean = false,
        val isQuerying: Boolean = false,
        val isGenerating: Boolean = false,

        val searchResults: List<Portion> = emptyList(),
        val generated: Portion? = null,
        val recents: List<Portion> = emptyList(),
    )

    private var firstNav = true
    private var defaultRecents = mutableListOf<Portion>()

    fun getData() {
        viewModelScope.launch {
            if (firstNav) {
                when (val res = repository.getRecents()) {
                    is Response.Error -> {}
                    is Response.Success -> {
                        defaultRecents.addAll(res.data)
                        updateData { copy(recents = res.data) }
                    }
                }
                firstNav = false
            }
        }
    }

    fun stopStreaming() {
        updateData { copy(isStreaming = false) }
    }

    fun resetState() {
        updateData {
            copy(
                isQuerying = false,
                isStreaming = false,
                isGenerating = false,
                generated = null,
                searchResults = emptyList(),
                recents = defaultRecents
            )
        }
    }

    fun find(query: String, onError: (Failure) -> Unit) {
        if (query.isBlank()) {
            onError(Failure.InvalidInput)
            return
        }
        viewModelScope.launch {
            updateData {
                copy(
                    recents = emptyList(),
                    searchResults = emptyList(),
                    generated = null,
                    isQuerying = true,
                    isGenerating = true
                )
            }
            var networkError = false
            var searchResults: List<Portion> = emptyList()
            var generatedResult: Portion? = null

            val searchDeferred = async {
                search(
                    query = query,
                    onError = { failure ->
                        if (failure == Failure.Network) networkError = true
                    },
                    onSuccess = {
                        searchResults = it
                        if (state.isQuerying) {
                            updateData { copy(searchResults = it) }
                        }
                    }
                )
            }

            val generateDeferred = async {
                generate(
                    query = query,
                    onSuccess = {
                        generatedResult = it
                        if (state.isQuerying) {
                            updateData {
                                copy(
                                    generated = it,
                                    isGenerating = false,
                                    isStreaming = true
                                )
                            }
                        }
                    },
                    onError = { failure ->
                        updateData { copy(isGenerating = false) }
                        if (failure == Failure.Network) networkError = true
                    }
                )
            }

            searchDeferred.await()
            generateDeferred.await()

            if (searchResults.isEmpty() && generatedResult == null) {
                if (networkError) {
                    onError(Failure.Network)
                } else {
                    onError(Failure.EmptyResult)
                }
                resetState()
            }
            updateData {
                copy(
                    isQuerying = false,
                    isGenerating = false,
                )
            }
        }
    }


    fun filter(query: String) {
        if (query.isBlank()) {
            resetState()
            return
        }
        if (state.searchResults.isNotEmpty()) {
            return
        }
        updateData {
            copy(
                recents = defaultRecents.filter {
                    it.displayName().contains(other = query, ignoreCase = true)
                }
            )
        }
    }


    private suspend fun search(
        query: String,
        onSuccess: (List<Portion>) -> Unit,
        onError: (Failure) -> Unit
    ) {
         when (val res = repository.search(query)) {
            is Response.Error -> onError(res.error)
            is Response.Success -> onSuccess(res.data)
        }
    }


    private suspend fun generate(
        query: String,
        onError: (Failure) -> Unit,
        onSuccess: (Portion) -> Unit
    ) {
        when (val res = repository.generate(query)) {
            is Response.Error -> onError(res.error)
            is Response.Success -> onSuccess(res.data)
        }
    }


}












