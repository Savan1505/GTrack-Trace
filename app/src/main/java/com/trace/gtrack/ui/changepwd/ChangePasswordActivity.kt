package com.trace.gtrack.ui.changepwd

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.databinding.ActivityChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainToolbar.ivBackButton.show()
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, ChangePasswordActivity::class.java))
        }
    }
}