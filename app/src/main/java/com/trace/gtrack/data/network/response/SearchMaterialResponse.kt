package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchMaterialResponse(
    @Json(name = "QRCode")
    val qRCode: String?,
    @Json(name = "Latitude")
    val latitude: String?,
    @Json(name = "Longitude")
    val longitude: String?,
    @Json(name = "RFIDCode")
    val rFIDCode: String?,
    @Json(name = "BatteryStatus")
    val batteryStatus: String?,
    @Json(name = "BadStatus")
    val badStatus: String?,
)