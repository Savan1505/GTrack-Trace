package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginAzureRequest(
    @Json(name = "AzureUserID")
    val azureUserID: String? = "",
)