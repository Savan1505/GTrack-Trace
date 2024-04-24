package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectDetailsResponse(
    @Json(name = "ProjectId")
    val ProjectId: String?,
    @Json(name = "ProjectName")
    val ProjectName: String?,
)