package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MaterialCodeListResponse<T>(
    @Json(name = "MaterialCode")
    val MaterialCode: List<String>?,
    @Json(name = "commonResponse")
    val commonResponse: T?,
)