package com.trace.gtrack.ui.assignqr.rfid

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.interfaces.IUHF
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.invisible
import com.trace.gtrack.common.utils.makeSuccessToast
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityRfidBinding
import com.trace.gtrack.ui.assignqr.rfid.viewmodel.RFIDState
import com.trace.gtrack.ui.assignqr.rfid.viewmodel.RFIDViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.ScannerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RFIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRfidBinding
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::scanQRCodeResult)
    private val BLUETOOTH_PERMISSION_REQUEST_CODE = 100
    private val ACCESS_FINE_LOCATION_REQUEST_CODE = 99

    //    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private var scanConnectionMode: ScanConnectionEnum = ScanConnectionEnum.SledScan
    var mReader: RFIDWithUHFUART? = null
    private var am: AudioManager? = null
    private var volumnRatio = 1.0f
    var soundMap = HashMap<Int, Int>()
    private var soundPool: SoundPool? = null
    private val rfidViewModel: RFIDViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRfidBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        observe()
        /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )*/
        binding.mainToolbar.ivBackButton.show()
        am = this.getSystemService(AUDIO_SERVICE) as AudioManager // 实例化AudioManager对象
        initSound();
        mReader = try {
            RFIDWithUHFUART.getInstance()
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }

        if (mReader != null) {
            CoroutineScope(Dispatchers.IO).launch {
                mReader?.init()
            }
        }
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

        binding.btnStartScan.setOnClickListener {
            if (binding.btnStartScan.text == resources.getString(R.string.assign_RFID)) {
                rfidViewModel.postRfidQRCodeMappingAPI(
                    this@RFIDActivity,
                    persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.edtScanQrHere.text.toString().trim(),
                    binding.edtRfidCode.text.toString().trim()
                )
            } else {
                rfidReaderConnection()
            }
        }

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
                // configureDevice()
            }
        } else {
            // configureDevice()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  configureDevice()
            } else {
                Toast.makeText(this, "Bluetooth Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onDestroy() {
        super.onDestroy()
        releaseSoundPool()
        if (mReader != null) {
            mReader!!.free()
        }
    }


    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, RFIDActivity::class.java))
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


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293) {
            if (event.repeatCount == 0) {
                rfidReaderConnection()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun playSound(id: Int) {
        val audioMaxVolume =
            am?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) // 返回当前AudioManager对象的最大音量值
        val audioCurrentVolume =
            am?.getStreamVolume(AudioManager.STREAM_MUSIC) // 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolume?.toFloat()!! / audioMaxVolume?.toFloat()!!
        try {
            soundPool?.play(
                soundMap.get(id)!!, volumnRatio,  // 左声道音量
                volumnRatio,  // 右声道音量
                1,  // 优先级，0为最低
                0,  // 循环次数，0不循环，-1永远循环
                1F // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initSound() {
        soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 5)
        soundMap[1] = soundPool?.load(this, R.raw.barcodebeep, 1)!!
        soundMap[2] = soundPool?.load(this, R.raw.serror, 1)!!
        am = this.getSystemService(AUDIO_SERVICE) as AudioManager // 实例化AudioManager对象
    }

    private fun releaseSoundPool() {
        if (soundPool != null) {
            soundPool?.release()
            soundPool = null
        }
    }

    private fun rfidReaderConnection() {
        var result = false
        val data = mReader?.readData(
            "00000000",
            IUHF.Bank_EPC,
            Integer.parseInt("2"),
            Integer.parseInt("6")
        )
        if (data != null) {
            binding.tilRfidCode.show()
            binding.edtRfidCode.text = Editable.Factory.getInstance().newEditable(
                data.toString()
            )
            binding.btnStartScan.text = resources.getString(R.string.assign_RFID)
            rfidViewModel.lstRFIDData.add(data.toString())
            persistenceManager.saveRFIDCodeList(rfidViewModel.lstRFIDData)
        } else {
            Toast.makeText(this, "Re-Scan RFID...", Toast.LENGTH_SHORT).show()

        }


        if (!TextUtils.isEmpty(data)) {
            result = true;

        } else {
            result = false;
        }
        if (!result) {
            playSound(2);
        } else {
            playSound(1);

        }
    }

    private fun observe() {
        rfidViewModel.state.observe(this@RFIDActivity) {
            when (it) {

                is RFIDState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                RFIDState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is RFIDState.Success -> {
                    AppProgressDialog.hide()
                    binding.tilRfidCode.invisible()
                    binding.edtRfidCode.text = Editable.Factory.getInstance().newEditable(
                        ""
                    )
                    binding.edtScanQrHere.text = Editable.Factory.getInstance().newEditable(
                        ""
                    )
                    binding.btnStartScan.text = resources.getString(R.string.btn_scan_RFID)
                    makeSuccessToast(it.rfidMsg)
                }
            }
        }

        /*rfidViewModel.stateRFID.observe(this@RFIDActivity) {
            when (it) {

                is InsertRFIDState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                InsertRFIDState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is InsertRFIDState.Success -> {
                    AppProgressDialog.hide()
                    makeSuccessToast(it.insertRFIDMsg)
                }
            }
        }*/
    }


    /*private val locationRequest = LocationRequest.create().apply {
        interval = 0 // Update interval in milliseconds
        fastestInterval = 0 // Fastest update interval in milliseconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult != null) {
                super.onLocationResult(locationResult)
            }

            for (location in locationResult.locations) {
                // Use latitude and longitude
                rfidViewModel.lstInsertRFIDData =
                    listOf(
                        InsertHandHeldDataRequest(
                            location!!.latitude.toString(),
                            location.longitude.toString(),
                            binding.edtRfidCode.text.toString().trim()
                        )
                    )
                *//*if (rfidViewModel.lstInsertRFIDData.isNotEmpty()) {
                    rfidViewModel.postInsertRFIDDataAPI(
                        this@RFIDActivity, persistenceManager.getAPIKeys(),
                        persistenceManager.getProjectId(),
                        persistenceManager.getSiteId()
                    )
                }*//*
            }
        }
    }*/
}