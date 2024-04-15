package com.trace.gtrack.data.network.base

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

private const val TAG = "safeApiCall"
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T,
): ResponseWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResponseWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            println(throwable)
            println(throwable.cause)
            Timber.tag(TAG).e(throwable)
            when (throwable) {
                is IOException -> {
                    ResponseWrapper.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResponseWrapper.GenericError(code, errorResponse)
                }
                else -> {
                    ResponseWrapper.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
            moshiAdapter.fromJson(it)
        }
    } catch (exception: Exception) {
        null
    }
}