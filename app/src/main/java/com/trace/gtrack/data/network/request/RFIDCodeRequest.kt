package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RFIDCodeRequest(
    @Json(name = "QRCode")
    val qRCode: String? = "",
    @Json(name = "RFIDCode")
    val rfidCode: String? = "",
)