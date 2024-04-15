package com.trace.gtrack.common.utils

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.trace.gtrack.R
import io.github.muddz.styleabletoast.StyleableToast

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun Activity.makeWarningToast(message: String) {
    StyleableToast.Builder(this).text(message).textColor(Color.WHITE).font(R.font.hellix_medium)
        .backgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)).gravity(Gravity.BOTTOM)
        .show()
}

fun Activity.makeSuccessToast(message: String) {
    StyleableToast.Builder(this).text(message).textColor(Color.WHITE).font(R.font.hellix_medium)
        .backgroundColor(ContextCompat.getColor(this, R.color.green_success))
        .gravity(Gravity.BOTTOM).show()
}

fun Activity?.showLongToast(message: String) {
    this?.let {
        StyleableToast.Builder(it).text(message).textColor(Color.WHITE).font(R.font.hellix_medium)
            .backgroundColor(ContextCompat.getColor(it, R.color.colorBlack)).gravity(Gravity.TOP)
            .length(Toast.LENGTH_LONG).show()
    }
}

fun ImageView.setImageUrl(url: String?) {
    Glide.with(this.context).load(url)
//            .placeholder(R.drawable.bg_default_form_template)
        .into(this)
}