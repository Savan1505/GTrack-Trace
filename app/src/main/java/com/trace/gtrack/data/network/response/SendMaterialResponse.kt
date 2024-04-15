package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMaterialResponse(
    @Json(name = "Status_Code")
    val statusCode: String?,
    @Json(name = "Detail_Message")
    val detailMessage: String?,
    @Json(name = "GIOTCode")
    val gIOTCode: String?,
    @Json(name = "MaterialCode")
    val materialCode: String?,
)