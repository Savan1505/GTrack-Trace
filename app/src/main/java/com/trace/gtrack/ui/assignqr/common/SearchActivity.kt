package com.trace.gtrack.ui.assignqr.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trace.gtrack.common.MaterialItemAdapter
import com.trace.gtrack.common.utils.hide
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.common.utils.show
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivitySearchBinding
import com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel.AssignState
import com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel.AssignViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var materialCodeAdapter: MaterialItemAdapter

    private val assignViewModel: AssignViewModel by viewModels()
    private var isFirstTimeCall = true

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager
    var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        binding.ivBack.setOnClickListener {
            finish()
        }
        if (binding.edtSearchMaterialCode.requestFocus()) {
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(
                binding.edtSearchMaterialCode,
                InputMethodManager.SHOW_IMPLICIT
            )
        }
        binding.edtSearchMaterialCode.doAfterTextChanged {
            if (binding.edtSearchMaterialCode.text.toString().length > 3) {
                assignViewModel.lstMaterialCode = ArrayList()
                assignViewModel.pageNumber = 1
                setupMaterialCodeAdapter()
                loadMore()
            }
        }
    }

    private fun setupMaterialCodeAdapter() {
        linearLayoutManager =
            binding.rvMaterialCode.layoutManager as LinearLayoutManager?
        binding.rvMaterialCode.isNestedScrollingEnabled = false
        materialCodeAdapter = MaterialItemAdapter({
            binding.rvMaterialCode.isGone
            val intentCode: Intent = intent
            intentCode.putExtra("material_code", it)
            setResult(Activity.RESULT_OK, intentCode)
            finish()
        }, assignViewModel.lstMaterialCode)
        binding.rvMaterialCode.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isFirstTimeCall) {
                        isFirstTimeCall = false
                        assignViewModel.pageNumber++
                        loadMore()
                    }
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isFirstTimeCall = true
                }
                materialCodeAdapter.showProgressBarNotify(true)
            }
        })
        binding.rvMaterialCode.adapter = materialCodeAdapter
    }

    private fun observe() {
        assignViewModel.state.observe(this@SearchActivity) {
            when (it) {

                is AssignState.Error -> {
                    binding.rvMaterialCode.hide()
                    binding.tvNoData.show()
                    materialCodeAdapter.showProgressBarNotify(false)
                    makeWarningToast(it.msg)
                }

                AssignState.Loading -> {
                    materialCodeAdapter.showProgressBarNotify(true)
                }

                is AssignState.Success -> {
                    binding.rvMaterialCode.show()
                    binding.tvNoData.hide()
                    materialCodeAdapter.showProgressBarNotify(false)
                    if (it.lstMaterialCode.isNotEmpty()) {
                        assignViewModel.lstMaterialCode += it.lstMaterialCode
                        setupMaterialCodeAdapter()
                    }
                }
            }
        }
    }

    fun loadMore() {
        materialCodeAdapter.showProgressBarNotify(true)
        assignViewModel.postAssignedMaterialListAPI(
            this@SearchActivity,
            persistenceManager.getAPIKeys(),
            persistenceManager.getProjectId(),
            persistenceManager.getSiteId(),
            binding.edtSearchMaterialCode.text.toString()
        )
    }
}