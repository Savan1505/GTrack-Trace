package com.trace.gtrack.data.network.base

import com.squareup.moshi.Json

data class CommonProjectDetailsResponse<T>(
    @field:Json(name = "status")
    val status: Int,
    @field:Json(name = "msg")
    val msg: String? = "",
    @field:Json(name = "err_msg")
    val err_msg: String? = "",
    @Json(name = "projectDetails")
    val projectDetails: T?,
) {
    fun isSuccess(): Boolean = status == 1
}