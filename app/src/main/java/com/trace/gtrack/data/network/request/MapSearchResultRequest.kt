package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MapSearchResultRequest(
    @Json(name = "UserId")
    val userId: String? = "",
    @Json(name = "MaterialCode")
    val materialCode: String? = "",
    @Json(name = "TotalSearchTime")
    val totalSearchTime: String? = "",
    @Json(name = "CreatedDate")
    val createdDate: String? = "",
)