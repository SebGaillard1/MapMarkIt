package com.example.mapmarkit.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mapmarkit.databinding.FragmentDashboardBinding
import com.example.mapmarkit.model.PointOfInterest
import com.example.mapmarkit.ui.PoiAdapter
import com.example.mapmarkit.ui.dashboard.PoiDetails.PoiDetailsDialogFragment

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PoiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = PoiAdapter(emptyList()) { poi ->
                showPoiDetailsDialog(poi)
            }.also { this@DashboardFragment.adapter = it }        }

        dashboardViewModel.allPointsOfInterest.observe(viewLifecycleOwner) { pois ->
            adapter.updateData(pois)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showPoiDetailsDialog(poi: PointOfInterest) {
        val dialogFragment = PoiDetailsDialogFragment().apply {
            arguments = Bundle().apply {
                putString("poiName", poi.name)
                putString("poiType", poi.types)
                putString("poiRating", "⭐️ ${poi.rating} /5")
                putString("poiDetails", poi.summary)
                putString("poiAddress", "📍  ${poi.address}")
                putString("poiPhone", "📞  ${poi.phone}")
                putString("poiWebsite", "🌐  ${poi.website}")
                putString("poiImageUrl", poi.photoReference)
                putString("poiId", poi.id)
            }
        }
        dialogFragment.show(childFragmentManager, "PoiDetailsDialog")
    }


}
