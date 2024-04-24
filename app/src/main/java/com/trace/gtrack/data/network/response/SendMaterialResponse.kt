package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMaterialResponse(
    @Json(name = "Status_Code")
    val Status_Code: String?,
    @Json(name = "Detail_Message")
    val Detail_Message: String?,
    @Json(name = "GIOTCode")
    val GIOTCode: String?,
    @Json(name = "MaterialCode")
    val MaterialCode: String?,
)