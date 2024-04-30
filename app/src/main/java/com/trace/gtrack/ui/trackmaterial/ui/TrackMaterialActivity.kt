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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.utils.hide
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityTrackMaterialBinding
import com.trace.gtrack.ui.assignqr.common.SearchActivity
import com.trace.gtrack.ui.trackmaterial.viewmodel.TrackMaterialMaterialState
import com.trace.gtrack.ui.trackmaterial.viewmodel.TrackMaterialViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackMaterialActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityTrackMaterialBinding
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val trackMaterialViewModel: TrackMaterialViewModel by viewModels()

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
            //setMapLocation()
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
            trackMaterialViewModel.lstTrackMaterialResponse = ArrayList()
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
                    trackMaterialViewModel.lstTrackMaterialResponse = it.lstTrackMaterialResponse
                    mapView.getMapAsync(this)
                }
            }
        }
    }

    private val locationRequest = LocationRequest.create()?.apply {
        interval = 10000 // Update interval in milliseconds
        fastestInterval = 5000 // Fastest update interval in milliseconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (locationResult != null) {
                super.onLocationResult(locationResult)
            }
            locationResult
            for (location in locationResult.locations) {
                // Use latitude and longitude
                val currentLatLng = LatLng(location!!.latitude, location.longitude)
                googleMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("Current Location")
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
            }
        }
    }

    private fun setMapLocation() {
        for (searchMaterialResponse in trackMaterialViewModel.lstTrackMaterialResponse) {
            googleMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        searchMaterialResponse.Latitude!!.toDouble(),
                        searchMaterialResponse.Longitude!!.toDouble()
                    )
                ).title(searchMaterialResponse.RFIDCode.toString())
            )
        }
        if (trackMaterialViewModel.lstTrackMaterialResponse.isNotEmpty()) {
            val firstLocation = trackMaterialViewModel.lstTrackMaterialResponse[0]
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        firstLocation.Latitude!!.toDouble(),
                        firstLocation.Longitude!!.toDouble()
                    ), 13f
                )
            )
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (locationRequest != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
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
            setMapLocation()
        }
    }
}