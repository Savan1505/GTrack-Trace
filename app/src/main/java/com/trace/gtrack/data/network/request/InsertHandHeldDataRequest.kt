package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InsertHandHeldDataRequest(
    @Json(name = "latitude")
    val latitude: Double? = 0.0,
    @Json(name = "longitude")
    val longitude: Double? = 0.0,
    @Json(name = "RFIDNumber")
    val rfidNumber: String? = "",
    @Json(name = "Timestamp")
    val timeStamp: String? = "",
)