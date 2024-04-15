package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse<T>(
    @Json(name = "UserId")
    val userId: String?,
    @Json(name = "UserName")
    val userName: String?,
    @Json(name = "ProjectId")
    val projectId: String?,
    @Json(name = "SiteId")
    val siteId: String?,
    @Json(name = "commonResponse")
    val commonResponse: T?,
)