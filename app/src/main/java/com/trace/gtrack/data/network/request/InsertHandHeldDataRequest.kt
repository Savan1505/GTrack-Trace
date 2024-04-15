package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InsertHandHeldDataRequest(
    @Json(name = "latitude")
    val latitude: String? = "",
    @Json(name = "longitude")
    val longitude: String? = "",
    @Json(name = "RFID")
    val rfid: String? = "",
)