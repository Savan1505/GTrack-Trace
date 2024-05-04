package com.trace.gtrack.ui.trackmaterial.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
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
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.hide
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
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TrackMaterialActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityTrackMaterialBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentLocationMarker: Marker? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val trackMaterialViewModel: TrackMaterialViewModel by viewModels()
    private var startTime: Long = 0
    private var stopTime: Long = 0
    private var isRunning: Boolean = false

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MapsInitializer.initialize(this@TrackMaterialActivity)
        observe()
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

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
            startTimer()
            trackMaterialViewModel.postSearchMaterialCodeAPI(
                this@TrackMaterialActivity,
                persistenceManager.getAPIKeys(),
                persistenceManager.getProjectId(),
                persistenceManager.getSiteId(),
                binding.edtSearchMaterialCode.text.toString(),
            )
        }
        binding.btnStop.setOnClickListener {
            onResume()
            mapView.hide()
            stopTimer()
            if (trackMaterialViewModel.lstHandHeldDataRequest.isNotEmpty() && persistenceManager != null) {
                trackMaterialViewModel.postInsertRFIDDataAPI(
                    this@TrackMaterialActivity,
                    persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                )
            }
            trackMaterialViewModel.totalSearchTime = epochToTime(getElapsedTime())
            /*if (persistenceManager != null) {
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
                }

                TrackMaterialMaterialState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is TrackMaterialMaterialState.Success -> {
                    AppProgressDialog.hide()
                    mapView.show()
                    binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStart.isClickable = false
                    binding.btnStop.isClickable = true
                    binding.btnStop.background = getDrawable(R.drawable.app_button_red_background)
                    trackMaterialViewModel.lstTrackMaterialResponse = it.lstTrackMaterialResponse
                    mapView.getMapAsync(this)
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
                    makeWarningToast(it.msg)
                }

                InsertRFIDMapState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is InsertRFIDMapState.Success -> {
                    AppProgressDialog.hide()
                    trackMaterialViewModel.lstTrackMaterialResponse = ArrayList()
                    binding.btnStart.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStart.isClickable = false
                    binding.btnStop.background = getDrawable(R.drawable.app_btn_grey_background)
                    binding.btnStop.isClickable = false
                    binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(
                        ""
                    )
                    makeSuccessToast(it.rfidMsg)
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
                    AppProgressDialog.hide()
                    /*trackMaterialViewModel.lstTrackMaterialResponse = ArrayList()
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
            try {
                if (locationResult != null) {
                    super.onLocationResult(locationResult)
                }

                for (location in locationResult.locations) {
                    // Use latitude and longitude
//                googleMap.clear()
                    val currentLatLng = LatLng(location!!.latitude, location.longitude)
                    for (searchMaterialResponse in trackMaterialViewModel.lstTrackMaterialResponse) {
                        val handHeldDeviceId = UUID.randomUUID().toString()
                        for (rfidCode in persistenceManager.getRFIDCodeList()) {
                            trackMaterialViewModel.lstInsertRFIDDataRequest.addAll(
                                listOf(
                                    InsertHandHeldDataRequest(
                                        location.latitude,
                                        location.longitude,
                                        rfidCode
                                    )
                                )
                            )
                        }
                        trackMaterialViewModel.lstHandHeldDataRequest =
                            listOf(
                                InsertHandHeldDataRequest(
                                    location.latitude,
                                    location.longitude,
                                    handHeldDeviceId
                                )
                            )
                        if (trackMaterialViewModel.lstTrackMaterialResponse.isNotEmpty()) {
                            insertHandHeldDataAPICall()
                        }
                        if (currentLocationMarker == null) {
                            // If marker doesn't exist, create a new marker
                            currentLocationMarker = googleMap.addMarker(
                                MarkerOptions().position(currentLatLng)
                                    .title(searchMaterialResponse.RFIDCode.toString()).icon(
                                        BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
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
                                BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            )
                        )
                    }
                    //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }

            } catch (e: NumberFormatException) {
                println("Error parsing string to double: ${e.message}")
            }
        }
    }

    private fun setMapLocation() {
        try {
            for (searchMaterialResponse in trackMaterialViewModel.lstTrackMaterialResponse) {
                googleMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            searchMaterialResponse.Latitude!!.toDouble(),
                            searchMaterialResponse.Longitude!!.toDouble()
                        )
                    ).title(searchMaterialResponse.QRCode.toString()).icon(
                        BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                )
                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            searchMaterialResponse.Latitude.toDouble(),
                            searchMaterialResponse.Longitude.toDouble()
                        ), 10f
                    )
                )
            }
        } catch (e: NumberFormatException) {
            println("Error parsing string to double: ${e.message}")
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
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (locationRequest != null) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                    return
                }
                googleMap.isMyLocationEnabled = true
                // Zoom controls
                googleMap.uiSettings.isZoomControlsEnabled = true
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            setMapLocation()
        }
    }

    private fun startTimer() {
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
    }

    private fun epochToTime(elapsed: Long): String {
        val seconds = (elapsed / 1000) % 60
        val minutes = ((elapsed / (1000 * 60)) % 60)
        val hours = ((elapsed / (1000 * 60 * 60)) % 24)

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}