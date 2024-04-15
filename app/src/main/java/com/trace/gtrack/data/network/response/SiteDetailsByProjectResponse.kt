package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SiteDetailsByProjectResponse(
    @Json(name = "SiteId")
    val siteId: String?,
    @Json(name = "SiteName")
    val siteName: String?,
)