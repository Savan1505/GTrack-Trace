package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeAssignMaterialCodeRequest(
    @Json(name = "UserId")
    val userId: String? = "",
    @Json(name = "MaterialCode")
    val materialCode: String? = "",
)