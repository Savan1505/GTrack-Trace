package com.trace.gtrack.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.trace.gtrack.R
import com.trace.gtrack.ui.assignqr.AssignQRActivity
import com.trace.gtrack.ui.assignqr.materialcode.MaterialCodeActivity
import com.trace.gtrack.ui.changepwd.ChangePasswordActivity
import com.trace.gtrack.databinding.ActivityHomeBinding
import com.trace.gtrack.ui.login.ui.LoginActivity
import com.trace.gtrack.ui.profile.ProfileActivity
import com.trace.gtrack.ui.searchmaterial.SearchMaterialActivity
import com.trace.gtrack.ui.unassignqr.UnAssignQRActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var actionBarToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            actionBarToggle = ActionBarDrawerToggle(
                this@HomeActivity, drawerLayout, mainToolbar.toolBar,
                R.string.open, R.string.close,
            )
            drawerLayout.addDrawerListener(actionBarToggle)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            actionBarToggle.syncState()
            homeFragmentLoad()
            navView.setNavigationItemSelectedListener {
                drawerLayout.closeDrawers()
                when (it.itemId) {
                    R.id.nav_home -> {
                        homeFragmentLoad()
                    }

                    R.id.nav_assign_qr -> {
                        AssignQRActivity.launch(this@HomeActivity)
                    }

                    R.id.nav_unassign_qr -> {
                        UnAssignQRActivity.launch(this@HomeActivity)
                    }

                    R.id.nav_search_material -> {
                        SearchMaterialActivity.launch(this@HomeActivity)
                    }

                    R.id.nav_track_material -> {
                        MaterialCodeActivity.launch(this@HomeActivity)
                    }

                    R.id.nav_profile -> {
                        ProfileActivity.launch(this@HomeActivity)
                    }

                    R.id.nav_change_password -> {
                        ChangePasswordActivity.launch(this@HomeActivity)
                    }

                    R.id.nav_logout -> {
                        logoutDialog()
                    }
                }
                true
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    private fun homeFragmentLoad() {
        val fragmentManager = supportFragmentManager
        val fragment = HomeFragment.newInstance(this@HomeActivity)

        fragmentManager.beginTransaction().replace(
            R.id.nav_host_fragment,
            fragment,
            HomeFragment::class.java.simpleName
        )
            .commit()
    }

    private fun logoutDialog() {
        val mDialog = MaterialAlertDialogBuilder(this)
        mDialog.setPositiveButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }.setNegativeButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            LoginActivity.launch(this@HomeActivity)
        }.setMessage("Are you sure, you want to logout?")
            .setTitle("Alert Dialog").create()
        mDialog.show()
    }
}