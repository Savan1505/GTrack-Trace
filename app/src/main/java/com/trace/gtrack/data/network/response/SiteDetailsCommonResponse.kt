package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SiteDetailsCommonResponse<T>(
    @Json(name = "SiteDetails")
    val SiteDetails: List<SiteDetailsByProjectResponse>?,
    @Json(name = "commonResponse")
    val commonResponse: T?,
)