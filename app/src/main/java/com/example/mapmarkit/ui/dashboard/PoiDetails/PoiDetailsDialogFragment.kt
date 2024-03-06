package com.example.mapmarkit.ui.dashboard.PoiDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mapmarkit.AppDatabase
import com.example.mapmarkit.R
import kotlinx.coroutines.launch

class PoiDetailsDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_poi_details_dialog, container, false)

        view.findViewById<TextView>(R.id.tvPoiName).text = arguments?.getString("poiName")
        view.findViewById<TextView>(R.id.tvPoiType).text = arguments?.getString("poiType")
        view.findViewById<TextView>(R.id.tvPoiRating).text = arguments?.getString("poiRating")
        view.findViewById<TextView>(R.id.tvPoiDetails).text = arguments?.getString("poiDetails")
        view.findViewById<TextView>(R.id.tvPoiAddress).text = arguments?.getString("poiAddress")
        view.findViewById<TextView>(R.id.tvPoiPhone).text = arguments?.getString("poiPhone")
        view.findViewById<TextView>(R.id.tvPoiWebsite).text = arguments?.getString("poiWebsite")

        val imageUrl = arguments?.getString("poiImageUrl")
        val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${imageUrl}&key=AIzaSyC8x6iTjvcg3Rgmj-UgdkZbrOD2FaVoV0o"

        imageUrl?.let {
            Glide.with(this)
                .load(photoUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(view.findViewById(R.id.ivPoiImage))
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linearLayout = view.findViewById<LinearLayout>(R.id.delete_button)
        val poiDao = AppDatabase.getDatabase(requireContext()).pointOfInterestDao()

        linearLayout.setOnClickListener {
            lifecycleScope.launch {
                arguments?.getString("poiId")?.let { poiId ->
                    if (poiId.isNotEmpty()) {
                        poiDao.deleteById(poiId)
                        dialog?.dismiss()
                        Toast.makeText(context, "Favoris supprimé !", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}