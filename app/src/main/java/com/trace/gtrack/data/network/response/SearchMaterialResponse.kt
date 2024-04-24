package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchMaterialResponse(
    @Json(name = "QRCode")
    val QRCode: String?,
    @Json(name = "Latitude")
    val Latitude: String?,
    @Json(name = "Longitude")
    val Longitude: String?,
    @Json(name = "RFIDCode")
    val RFIDCode: String?,
    @Json(name = "BatteryStatus")
    val BatteryStatus: String?,
    @Json(name = "BadStatus")
    val BadStatus: String?,
)