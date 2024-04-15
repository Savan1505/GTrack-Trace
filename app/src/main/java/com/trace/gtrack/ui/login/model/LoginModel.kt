package com.trace.gtrack.ui.login.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class LoginModel(
    var userName: String = "",
) : Parcelable