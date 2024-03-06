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

        // Configurez le RecyclerView
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = PoiAdapter(emptyList()) { poi ->
                // Ici, vous gérez le clic sur un élément
                // Par exemple, ouvrir une modale avec les détails du POI sélectionné
                showPoiDetailsDialog(poi)
            }.also { this@DashboardFragment.adapter = it }        }

        // Observez les données des POIs et mettez à jour l'UI
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
                putString("poiRating", "⭐️ ${poi.rating}")
                putString("poiStatus", poi.businessStatus)
                putString("poiDetails", poi.summary)
                putString("poiAddress", "📍  ${poi.address}")
                putString("poiPhone", "📞 ${poi.phone}")
                putString("poiWebsite", "🌐 ${poi.website}")
                // Si vous avez une URL pour l'image, vous pouvez également la passer comme ceci:
                putString("poiImageUrl", poi.photoReference)
            }
        }
        dialogFragment.show(childFragmentManager, "PoiDetailsDialog")
    }


}
