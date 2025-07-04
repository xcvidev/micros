package com.xcvi.micros.domain

import android.content.Context
import com.xcvi.micros.R

sealed interface Response<out D> {
    data class Success<out D>(val data: D) : Response<D>
    data class Error(val error: Failure) : Response<Nothing>

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error
}


sealed class Failure: Throwable() {
    data object Network: Failure() {
        private fun readResolve(): Any = Network
    }
    data object EmptyResult: Failure() {
        private fun readResolve(): Any = EmptyResult
    }
    data object InvalidInput: Failure() {
        private fun readResolve(): Any = InvalidInput
    }
    data object Unknown: Failure() {
        private fun readResolve(): Any = Unknown
    }
    data object Database: Failure() {
        private fun readResolve(): Any = Database
    }
    data object Authentication: Failure() {
        private fun readResolve(): Any = Authentication
    }
}

fun Failure.getLocalizedText(context: Context): String {
    return when (this) {
        is Failure.Network -> context.getString(R.string.network_error)
        is Failure.EmptyResult -> context.getString(R.string.empty_result)
        is Failure.InvalidInput -> context.getString(R.string.invalid_input)
        is Failure.Database -> context.getString(R.string.database_error)
        is Failure.Authentication -> context.getString(R.string.authentication_error)
        else -> context.getString(R.string.unknown_error)
    }
}