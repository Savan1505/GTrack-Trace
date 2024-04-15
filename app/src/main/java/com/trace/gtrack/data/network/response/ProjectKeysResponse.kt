package com.trace.gtrack.data.network.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectKeysResponse(
    @Json(name = "ProjectId")
    val projectId: String?,
    @Json(name = "ProjectName")
    val projectName: String?,
    @Json(name = "ProjectAPIKeys")
    val projectAPIKeys: String?,
)