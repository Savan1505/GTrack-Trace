package com.trace.gtrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trace.gtrack.ui.assignqr.AssignQRActivity
import com.trace.gtrack.ui.assignqr.materialcode.MaterialCodeActivity
import com.trace.gtrack.databinding.FragmentHomeBinding
import com.trace.gtrack.ui.searchmaterial.SearchMaterialActivity
import com.trace.gtrack.ui.unassignqr.UnAssignQRActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var authActivity: HomeActivity

    companion object {

        fun newInstance(a: HomeActivity): HomeFragment {
            val fragment = HomeFragment()
            fragment.authActivity = a
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvAssignQr.setOnClickListener {
            AssignQRActivity.launch(requireActivity())
        }
        binding.cvUnassignQr.setOnClickListener {
            UnAssignQRActivity.launch(requireActivity())
        }
        binding.cvSearchMaterial.setOnClickListener {
            SearchMaterialActivity.launch(requireActivity())
        }
        binding.cvTrackMaterial.setOnClickListener {
            MaterialCodeActivity.launch(requireActivity())
        }
    }
}