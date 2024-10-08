package com.trace.gtrack.ui.assignqr.iotcode.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.IOTCodeAdapter
import com.trace.gtrack.common.utils.makeSuccessToast
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityIotCodeBinding
import com.trace.gtrack.ui.assignqr.iotcode.viewmodel.IOTAssignState
import com.trace.gtrack.ui.assignqr.iotcode.viewmodel.IOTCodeState
import com.trace.gtrack.ui.assignqr.iotcode.viewmodel.IOTViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import javax.inject.Inject

@AndroidEntryPoint
class IOTCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIotCodeBinding
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::scanQRCodeResult)
    private val iOTViewModel: IOTViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIotCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        binding.mainToolbar.ivBackButton.show()
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }

        iOTViewModel.getIOTCodeAPI(this@IOTCodeActivity, "TGluZGUgUHZ0IEx0ZA==")
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

        binding.btnAssignQr.setOnClickListener {
            if (persistenceManager.getIOTCode() == binding.selectIotCode.text.toString() && binding.edtScanQrHere.text.toString()
                    .isNotEmpty()
            ) {
                iOTViewModel.postIotQRCodeReMappingAPI(
                    this@IOTCodeActivity,
                    persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.selectIotCode.text.toString(),
                    binding.edtScanQrHere.text.toString()
                )
            } else {
                iOTViewModel.postIotQRCodeMappingAPI(
                    this@IOTCodeActivity,
                    persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.selectIotCode.text.toString(),
                    binding.edtScanQrHere.text.toString()
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, IOTCodeActivity::class.java))
        }

        const val OPEN_SCANNER = "open_scanner"
    }

    private fun observe() {
        iOTViewModel.state.observe(this@IOTCodeActivity) {
            when (it) {
                is IOTCodeState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                IOTCodeState.Loading -> {
                    AppProgressDialog.show(this@IOTCodeActivity)
                }

                is IOTCodeState.Success -> {
                    AppProgressDialog.hide()
                    handleIOTCodePopUp(it.lstIOTResponse)
                }
            }
        }

        iOTViewModel.stateAS.observe(this@IOTCodeActivity) {
            when (it) {
                is IOTAssignState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                IOTAssignState.Loading -> {
                    AppProgressDialog.show(this@IOTCodeActivity)
                }

                is IOTAssignState.Success -> {
                    AppProgressDialog.hide()
                    persistenceManager.saveIOTCode(binding.selectIotCode.text.toString())
                    makeSuccessToast(it.toString())
                    binding.selectIotCode.text = ""
                }
            }
        }
    }

    private fun onSiteShowPopupWindow(
        v: View?,
        list: List<String>?,
    ) {
        if (v == null) return
        val popup = PopupWindow(v.context)
        popup.setOnDismissListener {
            handleArrowIOTCode(false)
        }
        val layout: View =
            LayoutInflater.from(v.context).inflate(R.layout.common_dropdown_popup, null)
        popup.elevation = 20f
        val rvLayout = layout.findViewById<RecyclerView>(R.id.rv_material_code)
        val adapter = IOTCodeAdapter(list) {
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
                this@IOTCodeActivity,
                R.drawable.ic_transparent
            )
        )
        popup.isFocusable = true
        // Show anchored to button
        popup.showAsDropDown(v)
    }

    private fun handleIOTCodePopUp(lstIOT: List<String>?) {
        binding.llIotCode.setOnClickListener {
            onSiteShowPopupWindow(it, lstIOT)
            handleArrowIOTCode(true)
        }
    }

    private fun handleArrowIOTCode(open: Boolean) {
        binding.ivDropDownArrow.animate().rotationBy((if (open) 1 else -1) * 180f).start()
    }


    private fun scanQRCodeResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                binding.edtScanQrHere.text =
                    Editable.Factory.getInstance().newEditable(
                        result.content.rawValue
                        // decoding with default UTF-8 charset when rawValue is null will not result in meaningful output, demo purpose
                            ?: result.content.rawBytes?.let { String(it) }.orEmpty().toString()
                    )
            }

            QRResult.QRUserCanceled -> "User canceled"
            QRResult.QRMissingPermission -> "Missing permission"
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
        }

    }
}