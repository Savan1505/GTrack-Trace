package com.trace.gtrack.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import com.trace.gtrack.R
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        binding.mainToolbar.ivBackButton.show()
        binding.edtUserName.text = Editable.Factory.getInstance().newEditable(persistenceManager.getUserName().uppercase(
            Locale.ROOT))
        binding.mainToolbar.ivBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }
    }
}