package com.trace.gtrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trace.gtrack.databinding.ActivityLandingBinding
import com.trace.gtrack.ui.login.ui.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        LoginActivity.launch(this@LandingActivity)
        finish()
    }
}