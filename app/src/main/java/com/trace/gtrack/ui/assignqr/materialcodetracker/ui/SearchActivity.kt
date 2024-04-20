package com.trace.gtrack.ui.assignqr.materialcodetracker.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.trace.gtrack.common.AppProgressDialog
import com.trace.gtrack.common.MaterialCodeAdapter
import com.trace.gtrack.common.utils.makeWarningToast
import com.trace.gtrack.data.persistence.IPersistenceManager
import com.trace.gtrack.databinding.ActivitySearchBinding
import com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel.AssignState
import com.trace.gtrack.ui.assignqr.materialcodetracker.viewmodel.AssignViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var materialCodeAdapter: MaterialCodeAdapter

    private val assignViewModel: AssignViewModel by viewModels()

    @Inject
    internal lateinit var persistenceManager: IPersistenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observe()
        binding.edtSearchMaterialCode.doAfterTextChanged {
            if (binding.edtSearchMaterialCode.text.toString().length > 3) {
                assignViewModel.postAssignedMaterialListAPI(
                    this@SearchActivity,
                    persistenceManager.getAPIKeys(),
                    persistenceManager.getProjectId(),
                    persistenceManager.getSiteId(),
                    binding.edtSearchMaterialCode.text.toString()
                )
            }
        }
        setupPopMaterialCode()
    }

    private fun setupPopMaterialCode() {
        materialCodeAdapter = MaterialCodeAdapter {
            binding.rlItemList.visibility = View.GONE
            val intentCode:Intent = intent
            intentCode.putExtra("material_code", it)
            setResult(Activity.RESULT_OK, intentCode)
            finish()
            //binding.edtSearchMaterialCode.text = Editable.Factory.getInstance().newEditable(it)
        }
        binding.rlItemList.adapter = materialCodeAdapter
    }

    private fun observe() {
        assignViewModel.state.observe(this@SearchActivity) { it ->
            when (it) {

                is AssignState.Error -> {
                    AppProgressDialog.hide()
                    makeWarningToast(it.msg)
                }

                AssignState.Loading -> {
                    AppProgressDialog.show(this)
                }

                is AssignState.Success -> {
                    materialCodeAdapter.updateSearchMaterialCodeList(it.lstMaterialCode)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(Intent(context, SearchActivity::class.java))
        }
    }
}