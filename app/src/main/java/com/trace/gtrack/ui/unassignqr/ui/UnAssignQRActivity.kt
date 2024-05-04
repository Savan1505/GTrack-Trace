package com.trace.gtrack.ui.unassignqr.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.invisible
import com.trace.gtrack.common.utils.makeSuccessToast
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityUnassignQrBinding
import com.trace.gtrack.ui.unassignqr.viewmodel.UnAssignMaterialState
import com.trace.gtrack.ui.unassignqr.viewmodel.UnAssignState
import com.trace.gtrack.ui.unassignqr.viewmodel.UnAssignViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import javax.inject.Inject

@AndroidEntryPoint
class UnAssignQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnassignQrBinding
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::scanQRCodeResult)
    private val unAssignViewModel: UnAssignViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    private var isFetch: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnassignQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        observe()
        binding.mainToolbar.ivBackButton.show()
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }
        /*binding.edtSearchMaterialCode.setOnClickListener {
            if (binding.edtScanQrHere.text.toString().isNotEmpty()) {
                Intent(this@UnAssignQRActivity, SearchActivity::class.java).apply {
                    materialCodeActivityForResult.launch(this)
                }
            } else {
                makeWarningToast(resources.getString(R.string.error_qrcode))
            }

        }*/
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
        binding.btnFetchDetails.setOnClickListener {
            if (!isFetch) {
                unAssignViewModel.postMaterialCodeByQRCodeAPI(
                    this@UnAssignQRActivity, persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.edtScanQrHere.text.toString()
                )
            } else {
                unAssignViewModel.postDeAssignMaterialTagAPI(
                    this@UnAssignQRActivity, persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    persistenceManager.getUserId(),
                    binding.edtSearchMaterialCode.text.toString()
                )
            }
        }
    }

    private val materialCodeActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data?.getStringExtra("material_code")
                binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                    intent.toString()
                )
                // Handle the Intent
            }
        }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, UnAssignQRActivity::class.java))
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

    private fun observe() {
        unAssignViewModel.state.observe(this@UnAssignQRActivity) { it ->
            when (it) {

                is UnAssignState.Error -> {
                    isFetch = false
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                UnAssignState.Loading -> {
                    isFetch = false
                    AppProgressDialog.show(this)
                }

                is UnAssignState.Success -> {
                    AppProgressDialog.hide()
                    binding.tilSearchMaterialCode.show()
                    binding.tilSearchMaterialCode.editText?.text =
                        Editable.Factory.getInstance().newEditable(it.materialCode)
                    binding.btnFetchDetails.text = getString(R.string.unassign_qr)
                    isFetch = true
                }
            }
        }

        unAssignViewModel.stateAM.observe(this@UnAssignQRActivity) {
            when (it) {

                is UnAssignMaterialState.Error -> {
                    AppProgressDialog.hide()
                    binding.edtScanQrHere.text?.clear()
                    binding.edtSearchMaterialCode.text?.clear()
                    binding.btnFetchDetails.text = getString(R.string.btn_fetch_details)
                    isFetch = false
                    binding.tilSearchMaterialCode.invisible()
                    makeWarningToast(it.msg)
                }

                UnAssignMaterialState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is UnAssignMaterialState.Success -> {
                    makeSuccessToast(it.message)
                    binding.edtScanQrHere.text?.clear()
                    binding.edtSearchMaterialCode.text?.clear()
                    binding.btnFetchDetails.text = getString(R.string.btn_fetch_details)
                    isFetch = false
                    binding.tilSearchMaterialCode.invisible()
                    AppProgressDialog.hide()
                }

                else -> {}
            }
        }
    }
}