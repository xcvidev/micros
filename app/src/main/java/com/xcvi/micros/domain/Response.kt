package com.xcvi.micros.domain

sealed interface Response<out D> {
    data class Success<out D>(val data: D) : Response<D>
    data class Error(val error: Throwable) : Response<Nothing>

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error
}


sealed class Failure: Throwable() {
    data class Authentication(override val message: String): Failure()
    data class Network(override val message: String): Failure()
    data class Database(override val message: String): Failure()
    data class Unknown(override val message: String): Failure()
}