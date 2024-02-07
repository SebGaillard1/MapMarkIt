package com.example.mapmarkit.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmarkit.databinding.FragmentDashboardBinding
import com.example.mapmarkit.model.PointOfInterest
import com.example.mapmarkit.ui.PoiAdapter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PoiAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 1. Configurez le RecyclerView
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 2. Créez votre liste de points d'intérêt (factice pour cet exemple)
        val poiList = listOf(
            PointOfInterest("1", "Point of Interest 1", "40.7128", "-74.0060"),
            PointOfInterest("2", "Point of Interest 2", "34.0522", "-118.2437"),
            PointOfInterest("3", "Point of Interest 3", "51.5074", "-0.1278")
        )

        // 3. Créez et définissez l'adaptateur
        adapter = PoiAdapter(poiList)
        recyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
