package com.trace.gtrack.data.network.base

import com.squareup.moshi.Json

data class NetworkResponseForArray<T>(
    @field:Json(name = "Status")
    val Status: Int,
    @field:Json(name = "Message")
    val Message: String? = "",
    @field:Json(name = "Error_Message")
    val Error_Message: String? = ""
) {
    fun isSuccess(): Boolean = Status == 200
}