package com.xcvi.micros.ui.destinations.food.scan

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScanViewModel(
    private val repository: FoodRepository
): BaseViewModel<ScanViewModel.State>(State()){

    data class State(
        val isLoading: Boolean = false,
    )

    fun cacheScan(barcode: String, onSuccess: () -> Unit, onFailure: () -> Unit){
        viewModelScope.launch {
            updateData { copy(isLoading = true) }
            delay(1000)
            /*
            when(repository.fetch(barcode)){
                is Response.Success -> {
                    updateData { copy(isLoading = false) }
                    onSuccess()
                }
                is Response.Error -> {
                    onFailure()
                }
            }

             */
        }
    }
}