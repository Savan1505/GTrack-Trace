package com.trace.gtrack.ui.assignqr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.databinding.ActivityAssignqrBinding
import com.trace.gtrack.ui.assignqr.iotcode.ui.IOTCodeActivity
import com.trace.gtrack.ui.assignqr.materialcodetracker.ui.MaterialCodeActivity
import com.trace.gtrack.ui.assignqr.rfid.RFIDActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssignQRActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssignqrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignqrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainToolbar.ivBackButton.show()
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }
        binding.cvMaterialCode.setOnClickListener {
            MaterialCodeActivity.launch(this@AssignQRActivity)
        }
        binding.cvRfid.setOnClickListener {
            RFIDActivity.launch(this@AssignQRActivity)
        }

        binding.cvIot.setOnClickListener {
            IOTCodeActivity.launch(this@AssignQRActivity)
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, AssignQRActivity::class.java))
        }
    }
}