package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse<T>(
    @Json(name = "UserId")
    val UserId: String?,
    @Json(name = "UserName")
    val UserName: String?,
    @Json(name = "ProjectId")
    val ProjectId: String?,
    @Json(name = "SiteId")
    val SiteId: String?,
    @Json(name = "commonResponse")
    val commonResponse: T?,
)