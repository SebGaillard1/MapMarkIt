package com.example.mapmarkit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mapmarkit.AppDatabase
import com.example.mapmarkit.R
import com.example.mapmarkit.model.PointOfInterest
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var progressBar: ProgressBar

    private var isFirstLocationUpdate = true
    private lateinit var locationStatusText: TextView

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isFirstLocationUpdate && locationResult.locations.isNotEmpty()) {
                    val location = locationResult.locations.first()
                    val userLocation = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                    progressBar.visibility = View.GONE
                    locationStatusText.visibility = View.GONE
                    isFirstLocationUpdate = false
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progressBar)
        locationStatusText = view.findViewById(R.id.locationStatusText)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        createLocationRequest()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { map ->
            googleMap = map
            googleMap.uiSettings.isMyLocationButtonEnabled = false

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true

                if (isFirstLocationUpdate) {
                    progressBar.visibility = View.VISIBLE
                    locationStatusText.visibility = View.VISIBLE
                }
                startLocationUpdates()
            } else {
                val predefinedLocation = LatLng(45.75, 4.85)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(predefinedLocation, 10f))
            }

            // 1. Écouter les clics sur les points d'intérêt
            googleMap.setOnPoiClickListener { poi ->
                // 2. Récupérer les informations du point d'intérêt
                val poiName = poi.name
                val poiLatLng = poi.latLng
                val poiId = poi.placeId

                // Afficher les informations du point d'intérêt dans un dialogue
                showPoiInfoDialog(poiName, poiLatLng, poiId)
            }
        }
    }

    private fun showPoiInfoDialog(poiName: String, poiLatLng: LatLng, poiId: String) {
        val snippet = "Position: ${poiLatLng.latitude}, ${poiLatLng.longitude} $poiId"

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(poiName)
            .setMessage(snippet)

        lifecycleScope.launch {
            val poiDao = AppDatabase.getDatabase(requireContext()).pointOfInterestDao()
            val isFavorite = poiDao.isPoiFavorited(poiId)
            if (isFavorite) {
                // POI est déjà en favoris, affichez "Retirer des favoris"
                builder.setNegativeButton("Retirer des favoris") { dialog, _ ->
                    // Supprimez le POI de la base de données
                    val poi = PointOfInterest(poiId, poiName, poiLatLng.latitude.toString(), poiLatLng.longitude.toString())
                    lifecycleScope.launch {
                        poiDao.delete(poi)
                    }
                    dialog.dismiss()
                }
            } else {
                // POI n'est pas en favoris, affichez "Ajouter aux favoris"
                builder.setNegativeButton("Ajouter aux favoris") { dialog, _ ->
                    // Ajoutez le POI à la base de données
                    val poi = PointOfInterest(poiId, poiName, poiLatLng.latitude.toString(), poiLatLng.longitude.toString())
                    lifecycleScope.launch {
                        poiDao.insert(poi)
                    }
                    dialog.dismiss()
                }
            }

            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            builder.create().show()
        }
    }




    override fun onStop() {
        super.onStop()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
