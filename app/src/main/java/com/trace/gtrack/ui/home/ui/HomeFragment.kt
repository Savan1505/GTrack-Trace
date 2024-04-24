package com.trace.gtrack.ui.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trace.gtrack.databinding.FragmentHomeBinding
import com.trace.gtrack.ui.assignqr.AssignQRActivity
import com.trace.gtrack.ui.searchmaterial.ui.SearchMaterialActivity
import com.trace.gtrack.ui.trackmaterial.ui.TrackMaterialActivity
import com.trace.gtrack.ui.unassignqr.ui.UnAssignQRActivity

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeActivity: HomeActivity

    companion object {

        fun newInstance(a: HomeActivity): HomeFragment {
            val fragment = HomeFragment()
            fragment.homeActivity = a
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
        binding.tvUsername.text = arguments?.getString("userName", "")
        binding.tvProjectName.text = arguments?.getString("projectName", "")
        binding.tvSiteName.text = arguments?.getString("siteName", "")
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
            TrackMaterialActivity.launch(requireActivity())
        }
    }
}