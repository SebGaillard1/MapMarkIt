package com.example.mapmarkit.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mapmarkit.AppDatabase
import com.example.mapmarkit.R
import com.example.mapmarkit.model.PointOfInterest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var progressBar: ProgressBar

    private var isFirstLocationUpdate = true
    private lateinit var locationStatusText: TextView

    private lateinit var placesClient: PlacesClient

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyC8x6iTjvcg3Rgmj-UgdkZbrOD2FaVoV0o")
        }
        placesClient = Places.createClient(requireContext())
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

            googleMap.setOnPoiClickListener { poi ->
                val poiName = poi.name
                val poiLatLng = poi.latLng
                val poiId = poi.placeId

                showPoiInfoDialog(poiName, poiLatLng, poiId)
            }
        }
    }

    private fun showPoiInfoDialog(poiName: String, poiLatLng: LatLng, poiId: String) {
        val snippet = "Ajoutez le lieu au favoris pour le retrouver facilement !"

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(poiName)
            .setMessage(snippet)
            .setPositiveButton("Fermer") { dialog, _ -> dialog.dismiss() }

        lifecycleScope.launch {
            val poiDao = AppDatabase.getDatabase(requireContext()).pointOfInterestDao()
            val isFavorite = poiDao.isPoiFavorited(poiId) // Assurez-vous que cette fonction existe et renvoie un Boolean

            if (isFavorite) {
                builder.setNegativeButton("Retirer des favoris") { dialog, _ ->
                    val poi = PointOfInterest(poiId, poiName, poiLatLng.latitude.toString(), poiLatLng.longitude.toString())
                    lifecycleScope.launch {
                        poiDao.delete(poi)
                        dialog.dismiss()
                        Toast.makeText(context, "Favoris supprimé !", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                builder.setNegativeButton("Ajouter aux favoris") { dialog, _ ->
                    val poi = PointOfInterest(poiId, poiName, poiLatLng.latitude.toString(), poiLatLng.longitude.toString())
                    lifecycleScope.launch {
                        poiDao.insert(poi)
                        getPlaceInformation(poi)
                        dialog.dismiss()
                        Toast.makeText(context, "Favoris ajouté !", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val dialog = builder.create()
            dialog.show()

            if (isFavorite) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            } else {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.rgb(20, 155, 58))
            }
        }
    }

    private fun getPlaceInformation(poi: PointOfInterest) {
        val poiDao = AppDatabase.getDatabase(requireContext()).pointOfInterestDao()
        val placeFields = listOf(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.RATING,
            Place.Field.TYPES,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.BUSINESS_STATUS,
            Place.Field.WEBSITE_URI,
            Place.Field.EDITORIAL_SUMMARY)
        val request = FetchPlaceRequest.newInstance(poi.id, placeFields)

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val responseString = place.photoMetadatas.firstOrNull().toString()

            val photoReferencePrefix = "photoReference="
            val startIndex = responseString.indexOf(photoReferencePrefix) + photoReferencePrefix.length
            val endIndex = responseString.indexOf(",", startIndex) // Supposant que la chaîne se termine par une virgule après le photoReference

            val photoReference = if (startIndex > photoReferencePrefix.length && endIndex > startIndex) {
                responseString.substring(startIndex, endIndex)
            } else {
                ""
            }

            val summary = if (place.editorialSummary.isNullOrEmpty()) "Pas de détails disponible." else place.editorialSummary

            val poi = PointOfInterest(
                id = poi.id,
                name = place.name ?: poi.name,
                latitude = poi.latitude,
                longitude = poi.longitude,
                address = place.address,
                rating = place.rating?.toString(),
                phone = place.phoneNumber?.toString(),
                types = place.placeTypes.firstOrNull(),
                photoReference = photoReference,
                summary = summary,
                website = place.websiteUri.toString()
            )

            lifecycleScope.launch {
                poiDao.insertOrReplacePoi(poi)
            }
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Log.e("PlacesError", "Place not found: ${exception.statusCode}")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
