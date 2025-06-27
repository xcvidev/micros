package com.xcvi.micros.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T>(initial: T) : ViewModel() {
    var state by mutableStateOf(initial)
        protected set

    protected fun updateData(update: T.() -> T) {
        state = state.update()
    }
}

/*
abstract class BaseViewModel<T>(initial: T) : ViewModel() {
    var state by mutableStateOf<UiState<T>>(UiState.Success(initial))
        protected set
    protected val currentData: T?
        get() = (state as? UiState.Success)?.data
    protected fun updateData(update: T.() -> T) {
        val current = currentData
        if (current == null) {
            println("BaseViewModel: Skipping update — currentData is null")
            return
        }
        state = UiState.Success(current.update())
    }
    protected fun setError(error: UiState.UiError) {
        println("BaseViewModel: Setting error state — $error")
        state = UiState.Failure(error)
    }
}

sealed class UiState<out T>{
    data class Success<T>(val data: T) : UiState<T>()
    data class Failure(val error: UiError) : UiState<Nothing>()

    sealed interface UiError{
        data object Unknown: UiError
        data object Connection: UiError
        data object UserInputError: UiError
        data object UnableToGenerate: UiError
        data object NotFound: UiError
        data object OutOfDate: UiError
    }

}
 */