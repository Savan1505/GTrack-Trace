package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationAssignMaterialResponse(
    @Json(name = "Message")
    val Message: String?,
    @Json(name = "MaterialCode")
    val MaterialCode: String?,
    @Json(name = "GIOTCode")
    val GIOTCode: String?,
    @Json(name = "Latitude")
    val Latitude: String?,
    @Json(name = "Longitude")
    val Longitude: String?,
)