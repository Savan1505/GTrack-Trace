package com.trace.gtrack.common

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.show

class GTrackProgressDialog : Dialog {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, theme: Int) : super(context!!, theme) {}

    companion object {
        fun show(
            context: Context?,
            message: String = "",
            cancelable: Boolean,
        ): GTrackProgressDialog {
            val dialog = GTrackProgressDialog(context, R.style.AppProgressDialog)
            dialog.setTitle("")
            dialog.setContentView(R.layout.layout_progress)
            if (message.isNotEmpty()) {
                val tvMessage = dialog.findViewById<AppCompatTextView>(R.id.tvMessage)
                tvMessage.show()
                tvMessage.text = message
            }
            dialog.setCancelable(cancelable)
            dialog.window!!.attributes.gravity = Gravity.CENTER
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.2f
            dialog.window!!.attributes = lp
            dialog.show()
            return dialog
        }
    }

}