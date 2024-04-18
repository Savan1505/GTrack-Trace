package com.trace.gtrack.ui.searchmaterial.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.network.response.SearchMaterialResponse
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivitySearchMaterialBinding
import com.trace.gtrack.ui.searchmaterial.viewmodel.SearchMaterialStartState
import com.trace.gtrack.ui.searchmaterial.viewmodel.SearchMaterialState
import com.trace.gtrack.ui.searchmaterial.viewmodel.SearchMaterialViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import io.github.g00fy2.quickie.content.QRContent
import javax.inject.Inject

@AndroidEntryPoint
class SearchMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchMaterialBinding
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::showSnackBar)
    private val searchMaterialViewModel: SearchMaterialViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    private var isFetch: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
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

        binding.btnFetchDetails.setOnClickListener {
            if (!isFetch) {
                searchMaterialViewModel.postMaterialCodeByQRCodeAPI(
                    this@SearchMaterialActivity, persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.edtScanQrHere.text.toString()
                )
            } else {
                searchMaterialViewModel.postSearchMaterialCodeAPI(
                    this@SearchMaterialActivity, persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.edtSearchMaterialCode.text.toString()
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, SearchMaterialActivity::class.java))
        }

        const val OPEN_SCANNER = "open_scanner"
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

    private fun observe() {
        searchMaterialViewModel.state.observe(this@SearchMaterialActivity) { it ->
            when (it) {

                is SearchMaterialState.Error -> {
                    isFetch = false
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                SearchMaterialState.Loading -> {
                    isFetch = false
                    AppProgressDialog.show(this)
                }

                is SearchMaterialState.Success -> {
                    binding.tilSearchMaterialCode.show()
                    binding.tilSearchMaterialCode.editText?.text =
                        Editable.Factory.getInstance().newEditable(it.materialCode)
                    binding.btnFetchDetails.text = getString(R.string.btn_start)
                    isFetch = true
                }
            }
        }

        searchMaterialViewModel.stateAM.observe(this@SearchMaterialActivity) {
            when (it) {

                is SearchMaterialStartState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                SearchMaterialStartState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is SearchMaterialStartState.Success -> {
                    val lstSearchMaterialResponse: List<SearchMaterialResponse> =
                        it.lstSearchMaterialResponse
                }
            }
        }
    }
}