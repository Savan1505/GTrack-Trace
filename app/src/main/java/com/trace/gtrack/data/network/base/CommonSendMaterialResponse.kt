package com.trace.gtrack.data.network.base

import com.squareup.moshi.Json

data class CommonSendMaterialResponse<T>(
    @field:Json(name = "Status")
    val status: Int,
    @field:Json(name = "Message")
    val message: String? = "",
    @field:Json(name = "Error_Message")
    val errorMessage: String? = "",
    @Json(name = "sendMaterial")
    val sendMaterial: T?,
) {
    fun isSuccess(): Boolean = status == 1
}