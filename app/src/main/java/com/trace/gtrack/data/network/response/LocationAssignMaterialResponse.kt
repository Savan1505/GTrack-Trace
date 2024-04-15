package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationAssignMaterialResponse(
    @Json(name = "Message")
    val message: String?,
    @Json(name = "MaterialCode")
    val materialCode: String?,
    @Json(name = "GIOTCode")
    val gIOTCode: String?,
    @Json(name = "Latitude")
    val latitude: String?,
    @Json(name = "Longitude")
    val longitude: String?,
)