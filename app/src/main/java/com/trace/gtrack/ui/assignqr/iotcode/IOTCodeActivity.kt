package com.trace.gtrack.ui.assignqr.iotcode

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.trace.gtrack.R
import com.trace.gtrack.common.CommonDropDownMenuAdapter
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.databinding.ActivityIotCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import io.github.g00fy2.quickie.content.QRContent

@AndroidEntryPoint
class IOTCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIotCodeBinding
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::showSnackBar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIotCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainToolbar.ivBackButton.show()
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }
        binding.ivScanQr.setOnClickListener {
            scanQrCode.launch(
                ScannerConfig.build {
                    setHapticSuccessFeedback(true) // enable (default) or disable haptic feedback when a barcode was detected
                    setShowTorchToggle(true) // show or hide (default) torch/flashlight toggle button
                    setShowCloseButton(true) // show or hide (default) close button
                    setUseFrontCamera(false) // use the front camera
                }
            )
        }
        if (intent.extras?.getBoolean(OPEN_SCANNER) == true) scanQrCode.launch(
            ScannerConfig.build {
                setHapticSuccessFeedback(true) // enable (default) or disable haptic feedback when a barcode was detected
                setShowTorchToggle(true) // show or hide (default) torch/flashlight toggle button
                setShowCloseButton(true) // show or hide (default) close button
                setUseFrontCamera(false) // use the front camera
            })
        handleIOTCodePopUp()
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, IOTCodeActivity::class.java))
        }

        const val OPEN_SCANNER = "open_scanner"
    }

    private fun onShowPopupWindow(v: View?, list: List<String>) {
        if (v == null) return
        val popup = PopupWindow(v.context)
        popup.setOnDismissListener {
            handleArrowIOTCode(false)
        }
        val layout: View =
            LayoutInflater.from(v.context).inflate(R.layout.common_dropdown_popup, null)
        popup.elevation = 20f
        val rvLayout = layout.findViewById<RecyclerView>(R.id.rl_item_list)
        val adapter = CommonDropDownMenuAdapter(list) {
            popup.dismiss()
            binding.selectIotCode.text = it
        }
        rvLayout.adapter = adapter
        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = v.width
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_transparent
            )
        )
        popup.isFocusable = true
        // Show anchored to button
        popup.showAsDropDown(v)
    }

    private fun handleIOTCodePopUp() {
        val iotCodeList = mutableListOf(
            "IOT Code 1",
            "IOT Code 2",
            "IOT Code 3",
            "IOT Code 4",
            "IOT Code 5",
            "IOT Code 6",
            "IOT Code 7",
            "IOT Code 8",
        )
        binding.llIotCode.setOnClickListener {
            onShowPopupWindow(it, iotCodeList)
            handleArrowIOTCode(true)
        }
    }

    private fun handleArrowIOTCode(open: Boolean) {
        binding.ivDropDownArrow.animate().rotationBy((if (open) 1 else -1) * 180f).start()
    }


    private fun showSnackBar(result: QRResult) {
        val text = when (result) {
            is QRResult.QRSuccess -> {
                result.content.rawValue
                // decoding with default UTF-8 charset when rawValue is null will not result in meaningful output, demo purpose
                    ?: result.content.rawBytes?.let { String(it) }.orEmpty()
            }

            QRResult.QRUserCanceled -> "User canceled"
            QRResult.QRMissingPermission -> "Missing permission"
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
        }

        Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE).apply {
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.run {
                maxLines = 5
                setTextIsSelectable(true)
            }
            if (result is QRResult.QRSuccess) {
                val content = result.content
                if (content is QRContent.Url) {
                    setAction(R.string.open_action) { openUrl(content.url) }
                    return@apply
                }
            }
            setAction(R.string.ok_action) { }
        }.show()
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (ignored: ActivityNotFoundException) {
            // no Activity found to run the given Intent
        }
    }
}