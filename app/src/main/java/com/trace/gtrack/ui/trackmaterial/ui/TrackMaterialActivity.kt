package com.trace.gtrack.ui.trackmaterial.ui


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.rscja.deviceapi.RFIDWithUHFUART
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.invisible
import com.trace.gtrack.common.utils.makeSuccessToast
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.network.request.InsertHandHeldDataRequest
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityTrackMaterialBinding
import com.trace.gtrack.ui.assignqr.common.SearchActivity
import com.trace.gtrack.ui.trackmaterial.viewmodel.HandHeldDataState
import com.trace.gtrack.ui.trackmaterial.viewmodel.InsertMapResultState
import com.trace.gtrack.ui.trackmaterial.viewmodel.InsertRFIDMapState
import com.trace.gtrack.ui.trackmaterial.viewmodel.TrackMaterialMaterialState
import com.trace.gtrack.ui.trackmaterial.viewmodel.TrackMaterialViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TrackMaterialActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityTrackMaterialBinding

    //    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentLocationMarker: Marker? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val trackMaterialViewModel: TrackMaterialViewModel by viewModels()
//    private var startTime: Long = 0
//    private var stopTime: Long = 0
    // private var isRunning: Boolean = false

    var mReader: RFIDWithUHFUART? = null
    private var am: AudioManager? = null
    private var volumnRatio = 1.0f
    var soundMap = HashMap<Int, Int>()
    private var soundPool: SoundPool? = null

    var handHeldDeviceId = ""
    val newInsertRFIDDataRequest = mutableListOf<InsertHandHeldDataRequest>()
    //var isStopClick = false

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onStart() {
        super.onStart()
        AppProgressDialog.show(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({
            window.statusBarColor = resources.getColor(R.color.colorPrimary)
            handHeldDeviceId =
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            observe()
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
//                    return
            }
            MapsInitializer.initialize(this@TrackMaterialActivity)
            //mapView = binding.mapView
            binding.mapView.onCreate(savedInstanceState)
            am = this.getSystemService(AUDIO_SERVICE) as AudioManager // 实例化AudioManager对象
            initSound()
            mReader = try {
                RFIDWithUHFUART.getInstance()
            } catch (ex: Exception) {
                ex.printStackTrace()
                return@postDelayed
            }
            binding.mapView.getMapAsync(this)
            if (mReader != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    mReader?.init()
                }
            }
            binding.btnStart.isClickable = false
            binding.mainToolbar.ivBackButton.show()
            binding.mainToolbar.ivBackButton.setOnClickListener {
                finish()
            }
            binding.edtSearchMaterialCode.setOnClickListener {
                Intent(this@TrackMaterialActivity, SearchActivity::class.java).apply {
                    materialCodeActivityForResult.launch(this)
                }
            }

            binding.btnStart.setOnClickListener {
                //startTimer()
                am = this.getSystemService(AUDIO_SERVICE) as AudioManager // 实例化AudioManager对象
                initSound()
                //isStopClick = false
                trackMaterialViewModel.postSearchMaterialCodeAPI(
                    this@TrackMaterialActivity,
                    persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.edtSearchMaterialCode.text.toString(),
                )
            }

            binding.btnStop.setOnClickListener {
                if (trackMaterialViewModel.lstInsertRFIDDataRequest.isNotEmpty() && persistenceManager != null) {
                    Log.e("TAG", "onCreate: STOP " + Gson().toJson(newInsertRFIDDataRequest))

                    if (newInsertRFIDDataRequest.isEmpty()
                    ) {
                        Toast.makeText(this, "No Data Available", Toast.LENGTH_SHORT).show()
                    } else {
                        trackMaterialViewModel.lstHandHeldDataRequest = ArrayList()
                        trackMaterialViewModel.lstInsertRFIDDataRequest.clear()

                        /*newInsertRFIDDataRequest.distinct()

                        trackMaterialViewModel.lstInsertRFIDDataRequest.addAll(
                            newInsertRFIDDataRequest
                        )

                        Log.e(
                            "newInsertRFIDDataRequest size",
                            newInsertRFIDDataRequest.size.toString()
                        )*/

                        newInsertRFIDDataRequest.forEach { it1 ->
                            if (it1.latitude != 0.0 && it1.longitude != 0.0) {
                                trackMaterialViewModel.lstInsertRFIDDataRequest.addAll(
                                    newInsertRFIDDataRequest.distinctBy { it.rfid })
                            }
                        }
                        if (trackMaterialViewModel.lstInsertRFIDDataRequest.isNotEmpty()) {
                            trackMaterialViewModel.postInsertRFIDDataAPI(
                                this@TrackMaterialActivity,
                                persistenceManager.getAPIKeys(),
                                persistenceManager.getProjectId(),
                                persistenceManager.getSiteId(),
                            )
                        }

                    }


                }
                binding.mapView.invisible()
                mReader?.stopInventory()
                releaseSoundPool()
                //isStopClick = true
                //stopTimer()
                trackMaterialViewModel.lstHandHeldDataRequest = ArrayList()
                trackMaterialViewModel.lstInsertRFIDDataRequest.clear()
                trackMaterialViewModel.lstTrackMaterialResponse = ArrayList()
                binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
                binding.btnStart.isClickable = false
                binding.btnStop.background = getDrawable(R.drawable.app_btn_grey_background)
                binding.btnStop.isClickable = false
                binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                    ""
                )

                /*trackMaterialViewModel.totalSearchTime = epochToTime(getElapsedTime())
                if (persistenceManager != null) {
                    trackMaterialViewModel.postInsertMAPSearchResultAPI(
                        this@TrackMaterialActivity,
                        persistenceManager.getAPIKeys(),
                        persistenceManager.getProjectId(),
                        persistenceManager.getSiteId(),
                        persistenceManager.getUserId(),
                        binding.edtSearchMaterialCode.text.toString(),
                    )
                }*/
            }
        }, 500)


    }

    private val materialCodeActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data?.getStringExtra("material_code")
                binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                    intent.toString()
                )
                if (binding.edtSearchMaterialCode.text.toString().isNotEmpty()) {
                    binding.btnStart.isClickable = true
                    binding.btnStart.isEnabled = true
                    binding.btnStart.background = getDrawable(R.drawable.app_button_background)
                }
                // Handle the Intent
            }
        }

    private fun observe() {
        trackMaterialViewModel.state.observe(this@TrackMaterialActivity) {
            when (it) {

                is TrackMaterialMaterialState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                    binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStart.isClickable = false
                    binding.btnStop.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStop.isClickable = false
                    binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                        ""
                    )
                }

                TrackMaterialMaterialState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is TrackMaterialMaterialState.Success -> {
                    AppProgressDialog.hide()
                    binding.mapView.show()
                    binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStart.isClickable = false
                    binding.btnStop.isClickable = true
                    binding.btnStop.background = getDrawable(R.drawable.app_button_red_background)
                    trackMaterialViewModel.lstTrackMaterialResponse = it.lstTrackMaterialResponse
                    binding.mapView.getMapAsync(this)
                    if (mReader != null) {
                        rfidReaderConnection()
                        mReader?.startInventoryTag()!!
                    }

                    if (::googleMap.isInitialized) {
                        setMapLocation()
                    }
                }
            }
        }
        trackMaterialViewModel.stateHH.observe(this@TrackMaterialActivity) {
            when (it) {

                is HandHeldDataState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                HandHeldDataState.Loading -> {
                    //AppProgressDialog.show(this)
                }

                is HandHeldDataState.Success -> {
                    AppProgressDialog.hide()
                    //Current Location Map
                }
            }
        }
        trackMaterialViewModel.stateRFID.observe(this@TrackMaterialActivity) {
            when (it) {

                is InsertRFIDMapState.Error -> {
                    AppProgressDialog.hide()
                    binding.mapView.invisible()
                    mReader?.stopInventory()
                    releaseSoundPool()
                    //isStopClick = true
                    //stopTimer()
                    trackMaterialViewModel.lstTrackMaterialResponse = ArrayList()
                    binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStart.isClickable = false
                    binding.btnStop.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStop.isClickable = false
                    binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                        ""
                    )
                    makeWarningToast(it.msg)
                    finish()
                }

                InsertRFIDMapState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is InsertRFIDMapState.Success -> {


                    AppProgressDialog.hide()
//                    binding.mapView.invisible()
//                    mReader?.stopInventory()
//                    releaseSoundPool()
//                    //isStopClick = true
//                    //stopTimer()
//                    trackMaterialViewModel.lstTrackMaterialResponse = ArrayList()
//                    binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
//                    binding.btnStart.isClickable = false
//                    binding.btnStop.background = getDrawable(R.drawable.app_btn_grey_background)
//                    binding.btnStop.isClickable = false
//                    binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
//                        ""
//                    )

                    makeSuccessToast(it.rfidMsg)

                    Handler(Looper.getMainLooper()).postDelayed({
                        this@TrackMaterialActivity.finish()
                    }, 2000)
                }
            }
        }
        trackMaterialViewModel.stateMapResult.observe(this@TrackMaterialActivity) {
            when (it) {

                is InsertMapResultState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                InsertMapResultState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is InsertMapResultState.Success -> {
                    AppProgressDialog.hide()/*trackMaterialViewModel.lstTrackMaterialResponse = ArrayList()
                    binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStart.isClickable = false
                    binding.btnStop.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStop.isClickable = false
                    binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                        ""
                    )*/
                    makeSuccessToast(it.mapResultMsg)
                }
            }
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000 // Update interval in milliseconds
        fastestInterval = 5000 // Fastest update interval in milliseconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (::googleMap.isInitialized) {
                try {
                    if (locationResult != null) {
                        super.onLocationResult(locationResult)

                        Handler().postDelayed({
                            // if (!isStopClick) {
                            for (location in locationResult.locations) {
                                // Use latitude and longitude
//                googleMap.clear()
                                val currentLatLng = LatLng(location!!.latitude, location.longitude)


                                /*trackMaterialViewModel.lstInsertRFIDDataRequest.addAll(
                            newInsertRFIDDataRequest
                        )*/
                                for (searchMaterialResponse in trackMaterialViewModel.lstTrackMaterialResponse) {
                                    if (currentLocationMarker == null) {
                                        // If marker doesn't exist, create a new marker
                                        currentLocationMarker = googleMap.addMarker(
                                            MarkerOptions().position(currentLatLng)
                                                .title(handHeldDeviceId)
                                                .icon(
                                                    BitmapDescriptorFactory.defaultMarker(
                                                        BitmapDescriptorFactory.HUE_GREEN
                                                    )
                                                )
                                        )
                                    } else {
                                        // If marker exists, update its position
                                        currentLocationMarker?.position = currentLatLng
                                    }

                                    googleMap.addMarker(
                                        MarkerOptions().position(
                                            LatLng(
                                                searchMaterialResponse.Latitude!!.toDouble(),
                                                searchMaterialResponse.Longitude!!.toDouble()
                                            )
                                        ).title(searchMaterialResponse.QRCode.toString()).icon(
                                            BitmapDescriptorFactory.defaultMarker(
                                                BitmapDescriptorFactory.HUE_RED
                                            )
                                        )
                                    )
                                    if (trackMaterialViewModel.lstInsertRFIDDataRequest.isNotEmpty()) {
                                        trackMaterialViewModel.lstInsertRFIDDataRequest.forEach {
                                            if (it.latitude == 0.0 && it.longitude == 0.0) {
                                                newInsertRFIDDataRequest.add(
                                                    InsertHandHeldDataRequest(
                                                        location.latitude,
                                                        location.longitude,
                                                        it.rfid
                                                    )
                                                )/*trackMaterialViewModel.lstInsertRFIDDataRequest.addAll(
                                            listOf(
                                                InsertHandHeldDataRequest(
                                                    location.latitude, location.longitude, it.rfid
                                                )
                                            )
                                        )*/
                                            }
                                        }
                                    }

                                    trackMaterialViewModel.lstHandHeldDataRequest = listOf(
                                        InsertHandHeldDataRequest(
                                            location.latitude, location.longitude, handHeldDeviceId
                                        )
                                    )

                                    if (trackMaterialViewModel.lstHandHeldDataRequest.isNotEmpty()) {
                                        insertHandHeldDataAPICall()
                                    }

                                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                                }

                            }
                            //}
                        }, 3000)
                    }

                } catch (e: NumberFormatException) {
                    println("Error parsing string to double: ${e.message}")
                }
            }
        }
    }

    private fun setMapLocation() {
        if (::googleMap.isInitialized) {
            try {
                for (searchMaterialResponse in trackMaterialViewModel.lstTrackMaterialResponse) {
                    googleMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                searchMaterialResponse.Latitude!!.toDouble(),
                                searchMaterialResponse.Longitude!!.toDouble()
                            )
                        ).title(searchMaterialResponse.QRCode.toString()).icon(
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )
                    )
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                searchMaterialResponse.Latitude.toDouble(),
                                searchMaterialResponse.Longitude.toDouble()
                            ), 15f
                        )
                    )
                }
            } catch (e: NumberFormatException) {
                println("Error parsing string to double: ${e.message}")
            }
        }
    }

    private fun insertHandHeldDataAPICall() {
        if (persistenceManager != null) {
            trackMaterialViewModel.postInsertHandheldDataAPI(
                this@TrackMaterialActivity,
                persistenceManager.getAPIKeys(),
                persistenceManager.getProjectId(),
                persistenceManager.getSiteId(),
            )
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, TrackMaterialActivity::class.java))
        }
    }

    override fun onMapReady(map: GoogleMap) {
        if (map != null) {
            googleMap = map
            if (::googleMap.isInitialized) {


                am = this.getSystemService(AUDIO_SERVICE) as AudioManager // 实例化AudioManager对象
                initSound();
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                if (locationRequest != null) {
                    if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_COARSE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {


                        googleMap.isMyLocationEnabled = true
                        // Zoom controls
                        googleMap.uiSettings.isZoomControlsEnabled = true

                        fusedLocationClient.requestLocationUpdates(
                            locationRequest, locationCallback, Looper.getMainLooper()
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        AppProgressDialog.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseSoundPool()
        //stopTimer()
        if (::googleMap.isInitialized) {
            googleMap.clear()
        }
        if (mReader != null) {
            mReader?.stopInventory()
            mReader!!.free()
        }
    }

    /*private fun startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis()
            isRunning = true
            println("Timer started")
        } else {
            println("Timer is already running")
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            stopTime = System.currentTimeMillis()
            isRunning = false

            println("Timer stopped")
        } else {
            println("Timer is not running")
        }
    }

    private fun getElapsedTime(): Long {
        return if (isRunning) {
            System.currentTimeMillis() - startTime
        } else {
            stopTime - startTime
        }
    }*/

    private fun epochToTime(elapsed: Long): String {
        val seconds = (elapsed / 1000) % 60
        val minutes = ((elapsed / (1000 * 60)) % 60)
        val hours = ((elapsed / (1000 * 60 * 60)) % 24)

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
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
        mReader?.setInventoryCallback { uhftagInfo ->
            try {

                if (uhftagInfo?.epc != null) {
                    playSound(1)
                }

                val item = InsertHandHeldDataRequest(
                    rfid = uhftagInfo?.epc, longitude = 0.00, latitude = 0.00
                )

                if (!trackMaterialViewModel.lstInsertRFIDDataRequest.contains(item)) {
                    trackMaterialViewModel.lstInsertRFIDDataRequest.add(item)
                }

//                trackMaterialViewModel.lstInsertRFIDDataRequest.add(
//                    InsertHandHeldDataRequest(
//                        rfid = uhftagInfo?.epc, longitude = 0.00, latitude = 0.00
//                    )
//                )
                /* trackMaterialViewModel.lstTrackMaterialResponse.forEach {
                     if (it.RFIDCode.equals(uhftagInfo?.epc!!)) {
                         CoroutineScope(Dispatchers.Main).launch {
                             playSound(1)
                         }

                     }

                 }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}