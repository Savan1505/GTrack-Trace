package com.trace.gtrack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityLandingBinding
import com.trace.gtrack.ui.home.ui.HomeActivity
import com.trace.gtrack.ui.login.ui.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (persistenceManager.getLoginState()) {
            HomeActivity.launch(this@LandingActivity)
            finish()
        } else {
            LoginActivity.launch(this@LandingActivity)
            finish()
        }
    }
}