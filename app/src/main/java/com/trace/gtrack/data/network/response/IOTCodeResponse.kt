package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IOTCodeResponse<T>(
    @Json(name = "IOTCodeList")
    val IOTCodeList: List<String>?,
    @Json(name = "commonResponse")
    val commonResponse: T?,
)