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

        // Initialisez l'API Google Places
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
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }

        lifecycleScope.launch {
            val poiDao = AppDatabase.getDatabase(requireContext()).pointOfInterestDao()
            val isFavorite = poiDao.isPoiFavorited(poiId) // Assurez-vous que cette fonction existe et renvoie un Boolean

            // Configurez le dialogue en fonction de si le POI est un favori ou non
            if (isFavorite) {
                // POI est déjà en favoris
                builder.setNegativeButton("Retirer des favoris") { dialog, _ ->
                    // Supprimez le POI de la base de données
                    val poi = PointOfInterest(poiId, poiName, poiLatLng.latitude.toString(), poiLatLng.longitude.toString())
                    lifecycleScope.launch {
                        poiDao.delete(poi)
                    }
                    dialog.dismiss()
                }
            } else {
                // POI n'est pas en favoris
                builder.setNegativeButton("Ajouter aux favoris") { dialog, _ ->
                    // Ajoutez le POI à la base de données
                    val poi = PointOfInterest(poiId, poiName, poiLatLng.latitude.toString(), poiLatLng.longitude.toString())
                    lifecycleScope.launch {
                        poiDao.insert(poi)
                        getPlaceInformation(poi)
                    }
                    dialog.dismiss()
                }
            }

            // Affichez le dialogue
            val dialog = builder.create()
            dialog.show()

            // Si le POI est un favori, changez la couleur du bouton en rouge
            if (isFavorite) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
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
            // Créez une nouvelle instance de PointOfInterest avec les informations récupérées
            val responseString = place.photoMetadatas.firstOrNull().toString()

            val photoReferencePrefix = "photoReference="
            val startIndex = responseString.indexOf(photoReferencePrefix) + photoReferencePrefix.length
            val endIndex = responseString.indexOf(",", startIndex) // Supposant que la chaîne se termine par une virgule après le photoReference

            val photoReference = if (startIndex > photoReferencePrefix.length && endIndex > startIndex) {
                responseString.substring(startIndex, endIndex)
            } else {
                ""
            }

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
                businessStatus = place.businessStatus.toString(),
                summary = place.editorialSummary,
                website = place.websiteUri.toString()
            )
            // Enregistrez l'instance dans la base de données
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
