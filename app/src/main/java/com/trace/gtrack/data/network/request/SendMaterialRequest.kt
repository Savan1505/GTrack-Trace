package com.trace.gtrack.data.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMaterialRequest(
    @Json(name = "AzureUserID")
    val azureUserID: String? = "",
    @Json(name = "MaterialCode")
    val materialCode: String? = "",
    @Json(name = "Origin")
    val origin: String? = "",
    @Json(name = "PONumber")
    val pONumber: String? = "",
    @Json(name = "POItem")
    val pOItem: String? = "",
    @Json(name = "IDType")
    val iDType: String? = "",
    @Json(name = "Identifier")
    val identifier: String? = "",
    @Json(name = "IDDescription")
    val iDDescription: String? = "",
    @Json(name = "DateType")
    val dateType: String? = "",
    @Json(name = "Split")
    val split: String? = "",
    @Json(name = "TargetDate")
    val targetDate: String? = "",
    @Json(name = "ActualDate")
    val actualDate: String? = "",
    @Json(name = "ContractDate")
    val contractDate: String? = "",
    @Json(name = "Quantity")
    val quantity: String? = "",
    @Json(name = "Remarks")
    val remarks: String? = "",
    @Json(name = "TagCode")
    val tagCode: String? = "",
)