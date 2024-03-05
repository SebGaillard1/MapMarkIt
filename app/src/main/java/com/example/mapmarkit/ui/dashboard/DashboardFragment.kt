package com.example.mapmarkit.ui.dashboard

import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PoiAdapter

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisez l'API Google Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyC8x6iTjvcg3Rgmj-UgdkZbrOD2FaVoV0o")
        }
        placesClient = Places.createClient(requireContext())
    }

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
            layoutManager = LinearLayoutManager(requireContext())
            adapter = PoiAdapter(emptyList()).also { this@DashboardFragment.adapter = it }
        }

        // Observez les données des POIs et mettez à jour l'UI
        dashboardViewModel.allPointsOfInterest.observe(viewLifecycleOwner) { pois ->
            adapter.updateData(pois)
            getPlaceInformation(pois.first().id)
        }

        return root
    }

    private fun getPlaceInformation(placeId: String) {
        // Définissez les champs d'informations du lieu à récupérer.
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.CURRENT_OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.TYPES)

        // Construisez la requête
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            // Utilisez les informations du lieu ici. Par exemple :
            Log.i("PlacesInfo", "Lieu trouvé: ${place.name}, Adresse: ${place.address}, horaires : ${place.openingHours}")
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                val apiException = exception
                Log.e("PlacesError", "Lieu non trouvé: ${apiException.statusCode}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
