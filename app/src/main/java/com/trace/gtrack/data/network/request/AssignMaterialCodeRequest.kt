package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssignMaterialCodeRequest(
    @Json(name = "QRCode")
    val qRCode: String? = "",
    @Json(name = "MaterialCode")
    val materialCode: String? = "",
)