package com.trace.gtrack.ui.selectprojsite.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.trace.gtrack.R
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.ProjDropDownMenuAdapter
import com.trace.gtrack.common.SiteDropDownMenuAdapter
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.data.network.response.ProjectKeysResponse
import com.trace.gtrack.data.network.response.SiteDetailsByProjectResponse
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivitySelectProjSiteBinding
import com.trace.gtrack.ui.home.ui.HomeActivity
import com.trace.gtrack.ui.login.ui.LoginActivity
import com.trace.gtrack.ui.selectprojsite.viewmodel.ProjDetailByProjectState
import com.trace.gtrack.ui.selectprojsite.viewmodel.ProjSiteViewModel
import com.trace.gtrack.ui.selectprojsite.viewmodel.SiteDetailByProjectState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectProSiteActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectProjSiteBinding
    private val projSiteViewModel: ProjSiteViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectProjSiteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        projSiteViewModel.getProjectKeysAPI(this@SelectProSiteActivity)
        observe()
        binding.btnContinue.setOnClickListener {
            if (binding.selectProject.text.toString().trim().isEmpty()) {
                makeWarningToast(resources.getString(R.string.error_project))
                return@setOnClickListener
            }
            if (binding.selectSite.text.toString().trim().isEmpty()) {
                makeWarningToast(resources.getString(R.string.error_site))
                return@setOnClickListener
            }
            persistenceManager.setLoginState(true)
            HomeActivity.launch(this@SelectProSiteActivity)
            finish()
        }
        binding.btnLogout.setOnClickListener {
            logoutDialog()
        }
    }

    private fun logoutDialog() {
        val mDialog = MaterialAlertDialogBuilder(
            this,
            R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Background
        )
        mDialog.setPositiveButton(R.string.btn_no) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }.setNegativeButton(R.string.btn_yes) { dialogInterface, _ ->
            dialogInterface.dismiss()
            persistenceManager.setLoginState(false)
            persistenceManager.saveProjectId("")
            persistenceManager.saveProjectName("")
            persistenceManager.saveSiteId("")
            persistenceManager.saveSiteName("")
            LoginActivity.launch(this@SelectProSiteActivity)
            finish()
        }.setMessage(R.string.logout_msg)
            .setTitle(R.string.logout).create()
        mDialog.show()
    }

    private fun observe() {
        projSiteViewModel.state.observe(this) {
            when (it) {

                is SiteDetailByProjectState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                SiteDetailByProjectState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is SiteDetailByProjectState.Success -> {
                    AppProgressDialog.hide()
                    handleSitePopUp(it.lstSite)
                }
            }
        }

        projSiteViewModel.stateProj.observe(this) {
            when (it) {
                is ProjDetailByProjectState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                ProjDetailByProjectState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is ProjDetailByProjectState.Success -> {
                    AppProgressDialog.hide()
                    handleProjectPopUp(it.lstProject)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, SelectProSiteActivity::class.java))
        }
    }

    private fun onProjShowPopupWindow(
        v: View?,
        list: List<ProjectKeysResponse>?,
    ) {
        if (v == null) return
        val popup = PopupWindow(v.context)
        popup.setOnDismissListener {
            handleArrowProject(false)
        }
        val layout: View =
            LayoutInflater.from(v.context).inflate(R.layout.common_dropdown_popup, null)
        popup.elevation = 20f
        val rvLayout = layout.findViewById<RecyclerView>(R.id.rv_material_code)
        val adapter = ProjDropDownMenuAdapter(list) {
            popup.dismiss()
            binding.selectProject.text = it?.projectName
            it?.projectId?.let { projectId ->
                persistenceManager.saveProjectId(projectId)
                it.projectAPIKeys?.let { apiKey ->
                    persistenceManager.saveAPIKeys(apiKey)
                    projSiteViewModel.postSiteDetailByProjectAPI(
                        this@SelectProSiteActivity,
                        apiKey,
                        projectId,
                        persistenceManager.getUserId()
                    )
                }
            }
            persistenceManager.saveProjectName(binding.selectProject.text.toString())
        }
        rvLayout.adapter = adapter
        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = v.width
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_transparent
            )
        )
        popup.isFocusable = true
        // Show anchored to button
        popup.showAsDropDown(v)
    }

    private fun handleProjectPopUp(lstProject: List<ProjectKeysResponse>?) {
        binding.llProject.setOnClickListener {
            onProjShowPopupWindow(it, lstProject)
            handleArrowProject(true)
        }
    }

    private fun onSiteShowPopupWindow(
        v: View?,
        list: List<SiteDetailsByProjectResponse>?,
    ) {
        if (v == null) return
        val popup = PopupWindow(v.context)
        popup.setOnDismissListener {
            handleArrowSite(false)
        }
        val layout: View =
            LayoutInflater.from(v.context).inflate(R.layout.common_dropdown_popup, null)
        popup.elevation = 20f
        val rvLayout = layout.findViewById<RecyclerView>(R.id.rv_material_code)
        val adapter = SiteDropDownMenuAdapter(list) {
            popup.dismiss()
            binding.selectSite.text = it?.SiteName
            it?.SiteId?.let { it1 -> persistenceManager.saveSiteId(it1) }
            persistenceManager.saveSiteName(binding.selectSite.text.toString())
        }
        rvLayout.adapter = adapter
        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = v.width
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_transparent
            )
        )
        popup.isFocusable = true
        // Show anchored to button
        popup.showAsDropDown(v)
    }

    private fun handleSitePopUp(lstSite: List<SiteDetailsByProjectResponse>?) {
        binding.llSite.setOnClickListener {
            onSiteShowPopupWindow(it, lstSite)
            handleArrowSite(true)
        }
    }

    private fun handleArrowProject(open: Boolean) {
        binding.ivDropDownProject.animate().rotationBy((if (open) 1 else -1) * 180f).start()
    }

    private fun handleArrowSite(open: Boolean) {
        binding.ivDropDownSite.animate().rotationBy((if (open) 1 else -1) * 180f).start()
    }
}