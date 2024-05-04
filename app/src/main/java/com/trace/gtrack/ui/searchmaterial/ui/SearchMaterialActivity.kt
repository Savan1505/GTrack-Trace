package com.trace.gtrack.ui.searchmaterial.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.invisible
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivitySearchMaterialBinding
import com.trace.gtrack.ui.assignqr.common.SearchActivity
import com.trace.gtrack.ui.searchmaterial.viewmodel.SearchMaterialState
import com.trace.gtrack.ui.searchmaterial.viewmodel.SearchMaterialViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import javax.inject.Inject

@AndroidEntryPoint
class SearchMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchMaterialBinding
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::scanQRCodeResult)
    private val searchMaterialViewModel: SearchMaterialViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        binding.mainToolbar.ivBackButton.show()
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }
        /*binding.edtSearchMaterialCode.setOnClickListener {
            if (binding.edtScanQrHere.text.toString().isNotEmpty()) {
                Intent(this@SearchMaterialActivity, SearchActivity::class.java).apply {
                    materialCodeActivityForResult.launch(this)
                }
            } else {
                makeWarningToast(resources.getString(R.string.error_qrcode))
            }

        }*/
        binding.edtScanQrHere.doOnTextChanged { text, start, before, count ->
            binding.btnFetchDetails.show()
            binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                ""
            )
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
        /*binding.edtSearchMaterialCode.setOnClickListener {
            Intent(this@SearchMaterialActivity, SearchActivity::class.java).apply {
                materialCodeActivityForResult.launch(this)
            }
        }*/
        binding.btnFetchDetails.setOnClickListener {
            searchMaterialViewModel.postMaterialCodeByQRCodeAPI(
                this@SearchMaterialActivity, persistenceManager.getAPIKeys(),
                persistenceManager.getProjectId(),
                persistenceManager.getSiteId(),
                binding.edtScanQrHere.text.toString()
            )
        }
    }

    private val materialCodeActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data?.getStringExtra("material_code")
                binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                    intent.toString()
                )
                if (binding.edtSearchMaterialCode.text.toString().isNotEmpty()) {
                    binding.btnFetchDetails.invisible()
                } else {
                    binding.btnFetchDetails.show()
                }
                // Handle the Intent
            }
        }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, SearchMaterialActivity::class.java))
        }

        const val OPEN_SCANNER = "open_scanner"
    }

    private fun scanQRCodeResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                binding.btnFetchDetails.show()
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
        searchMaterialViewModel.state.observe(this@SearchMaterialActivity) {
            when (it) {

                is SearchMaterialState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                SearchMaterialState.Loading -> {
                    AppProgressDialog.show(this@SearchMaterialActivity)
                }

                is SearchMaterialState.Success -> {
                    AppProgressDialog.hide()
                    binding.btnFetchDetails.invisible()
                    binding.tilSearchMaterialCode.show()
                    binding.edtSearchMaterialCode.text =
                        Editable.Factory.getInstance().newEditable(it.materialCode)
                }
            }
        }
    }
}