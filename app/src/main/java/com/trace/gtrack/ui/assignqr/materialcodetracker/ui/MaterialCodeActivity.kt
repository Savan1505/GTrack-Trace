package com.trace.gtrack.ui.assignqr.materialcodetracker.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.MaterialCodeAdapter
import com.trace.gtrack.common.utils.makeSuccessToast
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityMaterialCodeBinding
import com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel.AssignMaterialState
import com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel.AssignState
import com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel.AssignViewModel
import com.trace.gtrack.ui.home.ui.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import javax.inject.Inject

@AndroidEntryPoint
class MaterialCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMaterialCodeBinding

    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::scanQRCodeResult)
    private val assignViewModel: AssignViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMaterialCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        binding.mainToolbar.ivBackButton.show()
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }
        binding.edtSearchMaterialCode.setOnClickListener {
            if (binding.edtScanQrHere.text.toString().isNotEmpty()) {
//                SearchActivity.launch(this@MaterialCodeActivity)
                Intent(this@MaterialCodeActivity, SearchActivity::class.java).apply {
                    launcher.launch(this)
                }
            } else {
                makeWarningToast(resources.getString(R.string.error_qrcode))
            }

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
        if (intent.extras?.getBoolean(OPEN_SCANNER) == true) scanQrCode.launch(ScannerConfig.build {
            setHapticSuccessFeedback(true) // enable (default) or disable haptic feedback when a barcode was detected
            setShowTorchToggle(true) // show or hide (default) torch/flashlight toggle button
            setShowCloseButton(true) // show or hide (default) close button
            setUseFrontCamera(false) // use the front camera
        })

        binding.btnAssignQr.setOnClickListener {
            assignViewModel.postAssignMaterialTagAPI(
                this@MaterialCodeActivity,
                persistenceManager.getAPIKeys(),
                persistenceManager.getProjectId(),
                persistenceManager.getSiteId(),
                binding.edtScanQrHere.text.toString(),
                binding.edtSearchMaterialCode.text.toString()
            )
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data?.getStringExtra("material_code")
            binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                intent.toString()
            )
            // Handle the Intent
        }
    }
    private fun observe() {
        assignViewModel.stateAM.observe(this@MaterialCodeActivity) {
            when (it) {

                is AssignMaterialState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                AssignMaterialState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is AssignMaterialState.Success -> {
                    AppProgressDialog.hide()
                    makeSuccessToast(it.message)
                    binding.edtScanQrHere.text?.clear()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, MaterialCodeActivity::class.java))
        }

        const val OPEN_SCANNER = "open_scanner"
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