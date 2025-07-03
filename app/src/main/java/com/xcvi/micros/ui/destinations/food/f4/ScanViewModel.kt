package com.xcvi.micros.ui.destinations.food.f4

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.domain.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanViewModel(
    private val repository: FoodRepository
): BaseViewModel<ScanViewModel.State>(State()) {

    data class State(
        val isLoading: Boolean = false,
    )

    fun cacheScan(barcode: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            updateData { copy(isLoading = true) }
            when (repository.scan(barcode)) {
                is Response.Success -> {
                    onSuccess()
                }
                is Response.Error -> {
                    onFailure()
                }
            }
        }
    }
}