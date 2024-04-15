package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IOTCodeRequest(
    @Json(name = "IOTCode")
    val iotCode: String? = "",
    @Json(name = "QRCode")
    val qRCode: String? = "",
)