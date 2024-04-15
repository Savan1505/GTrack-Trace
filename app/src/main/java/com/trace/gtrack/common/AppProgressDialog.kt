package com.trace.gtrack.common

import android.content.Context

object AppProgressDialog {
    private var pd: GTrackProgressDialog? = null

    fun show(context: Context, message: String = "") {
        if (pd != null && pd?.isShowing == true)
            pd?.dismiss()
        pd = GTrackProgressDialog.show(context, message, false)
    }

    fun hide() {
        if (pd != null && pd?.isShowing == true)
            pd?.dismiss()
    }
}