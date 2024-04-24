package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MaterialCodeListResponse<T>(
    @Json(name = "MaterialCode")
    val MaterialCode: List<String>?,
    @Json(name = "pageNumber")
    val pageNumber: Int?,
    @Json(name = "pageSize")
    val pageSize: Int?,
    @Json(name = "TotalItems")
    val TotalItems: Int?,
    @Json(name = "TotalPages")
    val TotalPages: Int?,
    @Json(name = "commonResponse")
    val commonResponse: T?,
)