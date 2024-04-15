package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InsertRFIDRequest(
    @Json(name = "Data")
    val data: String? = "",
)