package com.trace.gtrack.ui.trackmaterial.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
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
class TrackMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackMaterialBinding

    private val trackMaterialViewModel: TrackMaterialViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    private val position = LatLng(41.015137, 28.979530)

    private var markerOptions = MarkerOptions().position(position)

    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MapsInitializer.initialize(this@TrackMaterialActivity)
        observe()
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
            /*assignViewModel.postAssignMaterialTagAPI(
                this@TrackMaterialActivity,
                persistenceManager.getAPIKeys(),
                persistenceManager.getProjectId(),
                persistenceManager.getSiteId(),
                binding.edtScanQrHere.text.toString(),
                binding.edtSearchMaterialCode.text.toString()
            )*/
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
//                    makeSuccessToast(it.lstTrackMaterialResponse.toString().length)
                }
            }
        }
    }

    private fun setMapLocation(map: GoogleMap) {
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13f))
            mapType = GoogleMap.MAP_TYPE_NORMAL
            setOnMapClickListener {
                marker?.remove()
                markerOptions.position(it)
                marker = addMarker(markerOptions)
                Toast.makeText(this@TrackMaterialActivity, "Clicked on ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, TrackMaterialActivity::class.java))
        }
    }
}