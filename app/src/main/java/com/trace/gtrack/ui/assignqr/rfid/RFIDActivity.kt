package com.trace.gtrack.ui.assignqr.rfid

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.trace.gtrack.common.utils.makeSuccessToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.databinding.ActivityRfidBinding
import com.trace.gtrack.ui.assignqr.rfid.common.IRFIDReaderListener
import com.trace.gtrack.ui.assignqr.rfid.common.RFIDReaderInterface
import com.trace.gtrack.ui.assignqr.rfid.common.ScanConnectionEnum
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig

@AndroidEntryPoint
class RFIDActivity : AppCompatActivity(), IRFIDReaderListener {

    private lateinit var binding: ActivityRfidBinding
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::scanQRCodeResult)
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 100
    private val ACCESS_FINE_LOCATION_REQUEST_CODE = 99

    private var scanConnectionMode: ScanConnectionEnum = ScanConnectionEnum.SledScan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidBinding.inflate(layoutInflater)
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

        //Scanner Initializations
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_REQUEST_CODE
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    BLUETOOTH_PERMISSION_REQUEST_CODE
                )
            } else {
                configureDevice()
            }
        } else {
            configureDevice()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                configureDevice()
            } else {
                Toast.makeText(this, "Bluetooth Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun configureDevice() {

        Thread { configureRFID() }.start()

    }

    private fun configureRFID() {
        // Configure RFID
        if (rfidInterface == null)
            rfidInterface = RFIDReaderInterface(this@RFIDActivity)

        var connectRFIDResult = rfidInterface!!.connect(applicationContext, scanConnectionMode)

        runOnUiThread {
//            progressBar.visibility = ProgressBar.GONE
            Toast.makeText(
                applicationContext,
                if (connectRFIDResult) "RFID Reader connected!" else "RFID Reader connection ERROR!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }

    private fun dispose() {
        try {
            if (rfidInterface != null) {
                rfidInterface!!.onDestroy()
            }
        } catch (ex: Exception) {
        }
    }

    override fun newTagRead(epc: String?) {
        runOnUiThread {
            makeSuccessToast(epc!!)
//            tagsList.add(0, epc!!)
//            listViewRFID.invalidateViews()
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, RFIDActivity::class.java))
        }

        const val OPEN_SCANNER = "open_scanner"
        private var rfidInterface: RFIDReaderInterface? = null
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