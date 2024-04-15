package com.trace.gtrack.ui.selectprojsite

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.trace.gtrack.R
import com.trace.gtrack.common.CommonDropDownMenuAdapter
import com.trace.gtrack.databinding.ActivitySelectProjSiteBinding
import com.trace.gtrack.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectProSiteActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelectProjSiteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectProjSiteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleProjectPopUp()
        handleSitePopUp()
        binding.btnContinue.setOnClickListener {
            HomeActivity.launch(this@SelectProSiteActivity)
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, SelectProSiteActivity::class.java))
        }
    }

    private fun onShowPopupWindow(v: View?, list: List<String>, isProject: Boolean) {
        if (v == null) return
        val popup = PopupWindow(v.context)
        popup.setOnDismissListener {
            if (isProject) {
                handleArrowProject(false)
            } else {
                handleArrowSite(false)
            }
        }
        val layout: View =
            LayoutInflater.from(v.context).inflate(R.layout.common_dropdown_popup, null)
        popup.elevation = 20f
        val rvLayout = layout.findViewById<RecyclerView>(R.id.rl_item_list)
        val adapter = CommonDropDownMenuAdapter(list) {
            popup.dismiss()
            if (isProject) {
                binding.selectProject.text = it
            } else {
                binding.selectSite.text = it
            }

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

    private fun handleProjectPopUp() {
        val projectList = mutableListOf(
            "Project 1",
            "Project 2",
            "Project 3",
            "Project 4",
            "Project 5",
            "Project 6",
            "Project 7",
            "Project 8",
        )
        binding.llProject.setOnClickListener {
            onShowPopupWindow(it, projectList, true)
            handleArrowProject(true)
        }
    }

    private fun handleSitePopUp() {
        val siteList = mutableListOf(
            "Site 1",
            "Site 2",
            "Site 3",
            "Site 4",
            "Site 5",
            "Site 6",
            "Site 7",
            "Site 8",
        )
        binding.llSite.setOnClickListener {
            onShowPopupWindow(it, siteList, false)
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