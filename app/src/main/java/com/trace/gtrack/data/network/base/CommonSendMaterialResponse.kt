package com.trace.gtrack.data.network.base

import com.squareup.moshi.Json

data class CommonSendMaterialResponse<T>(
    @field:Json(name = "Status")
    val Status: Int,
    @field:Json(name = "Message")
    val Message: String? = "",
    @field:Json(name = "Error_Message")
    val Error_Message: String? = "",
    @Json(name = "sendMaterial")
    val sendMaterial: T?,
) {
    fun isSuccess(): Boolean = Status == 1
}