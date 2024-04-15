package com.trace.gtrack.data.network.base

sealed class ResponseWrapper<out T> {
    data class Success<out T>(val value: T) : ResponseWrapper<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
        ResponseWrapper<Nothing>()

    object NetworkError : ResponseWrapper<Nothing>()
}