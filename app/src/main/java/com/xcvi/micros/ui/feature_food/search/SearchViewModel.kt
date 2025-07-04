package com.xcvi.micros.ui.feature_food.search

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: FoodRepository
) : BaseViewModel<SearchViewModel.State>(State()) {


    data class State(
        val isGenerating: Boolean = false,
        val isStreaming: Boolean = false,

        val isSearching: Boolean = false,

        val searchResults: List<Portion> = emptyList(),
        val generated: Portion? = null,

        val recents: List<Portion> = emptyList(),
        val filtered: List<Portion> = emptyList(),
    )

    var firstNav = true
        private set

    fun getData(){
        viewModelScope.launch {
            if(firstNav){
                when(val res = repository.getRecents()){
                    is Response.Error -> {}
                    is Response.Success -> updateData {
                        copy(recents = res.data, filtered = res.data, searchResults = emptyList())
                    }
                }
                firstNav = false
            }
        }
    }

    fun search(query: String, onFailure: (Failure) -> Unit) {
        if (query.isBlank()) {
            onFailure(Failure.InvalidInput)
            return
        }
        viewModelScope.launch {
            updateData { copy(isSearching = true, filtered = emptyList()) }
            var remoteError: Throwable? = null

            val local = when (val res = repository.searchLocal(query)) {
                is Response.Error -> emptyList()
                is Response.Success -> res.data
            }

            val remote = when (val res = repository.searchRemote(query)) {
                is Response.Error -> {
                    remoteError = res.error
                    emptyList()
                }
                is Response.Success -> res.data
            }
            val data = local + remote

            updateData { copy(isSearching = false, searchResults = data) }
            if (remoteError != null && data.isEmpty()) {
                onFailure(Failure.Unknown)
            }
        }
    }




}












