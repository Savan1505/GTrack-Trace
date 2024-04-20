package com.trace.gtrack.ui.home.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.trace.gtrack.R
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivityHomeBinding
import com.trace.gtrack.databinding.NavHeaderBinding
import com.trace.gtrack.ui.assignqr.AssignQRActivity
import com.trace.gtrack.ui.assignqr.materialcodetracker.ui.MaterialCodeActivity
import com.trace.gtrack.ui.changepwd.ChangePasswordActivity
import com.trace.gtrack.ui.login.ui.LoginActivity
import com.trace.gtrack.ui.profile.ProfileActivity
import com.trace.gtrack.ui.searchmaterial.ui.SearchMaterialActivity
import com.trace.gtrack.ui.unassignqr.ui.UnAssignQRActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var actionBarToggle: ActionBarDrawerToggle

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewHeader = binding.navView.getHeaderView(0)
        val navViewHeaderBinding: NavHeaderBinding = NavHeaderBinding.bind(viewHeader)
        navViewHeaderBinding.tvUsername.text = persistenceManager.getUserName().uppercase(Locale.ROOT)
        navViewHeaderBinding.tvProject.text = persistenceManager.getProjectName()
        navViewHeaderBinding.tvSite.text = persistenceManager.getSiteName()
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

                    /*R.id.nav_profile -> {
                        ProfileActivity.launch(this@HomeActivity)
                    }

                    R.id.nav_change_password -> {
                        ChangePasswordActivity.launch(this@HomeActivity)
                    }*/

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
        val bundle = Bundle()
        bundle.putString("userName", persistenceManager.getUserName().uppercase(Locale.ROOT))
        bundle.putString("projectName", persistenceManager.getProjectName())
        bundle.putString("siteName", persistenceManager.getSiteName())
        val fragmentManager = supportFragmentManager
        val fragment = HomeFragment.newInstance(this@HomeActivity)
        fragment.arguments = bundle
        fragmentManager.beginTransaction().replace(
            R.id.nav_host_fragment,
            fragment,
            HomeFragment::class.java.simpleName
        )
            .commit()
    }

    private fun logoutDialog() {
        val mDialog = MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background)
        mDialog.setPositiveButton(R.string.btn_no) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }.setNegativeButton(R.string.btn_yes) { dialogInterface, _ ->
            dialogInterface.dismiss()
            persistenceManager.setLoginState(false)
            persistenceManager.saveUserId("")
            persistenceManager.saveUserName("")
            persistenceManager.saveProjectId("")
            persistenceManager.saveProjectName("")
            persistenceManager.saveSiteId("")
            persistenceManager.saveSiteName("")
            LoginActivity.launch(this@HomeActivity)
            finish()
        }.setMessage(R.string.logout_msg)
            .setTitle(R.string.logout).create()
        mDialog.show()
    }
}