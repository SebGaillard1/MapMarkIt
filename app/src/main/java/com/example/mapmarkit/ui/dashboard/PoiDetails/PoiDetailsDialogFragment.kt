package com.example.mapmarkit.ui.dashboard.PoiDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.mapmarkit.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PoiDetailsDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PoiDetailsDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_poi_details_dialog, container, false)

        // Utilisez la vue gonflée pour trouver les TextViews et configurer leurs textes
        view.findViewById<TextView>(R.id.tvPoiName).text = arguments?.getString("poiName")
        view.findViewById<TextView>(R.id.tvPoiType).text = arguments?.getString("poiType")
        view.findViewById<TextView>(R.id.tvPoiRating).text = arguments?.getString("poiRating")
        view.findViewById<TextView>(R.id.tvPoiStatus).text = arguments?.getString("poiStatus")
        view.findViewById<TextView>(R.id.tvPoiDetails).text = arguments?.getString("poiDetails")
        view.findViewById<TextView>(R.id.tvPoiAddress).text = arguments?.getString("poiAddress")
        view.findViewById<TextView>(R.id.tvPoiPhone).text = arguments?.getString("poiPhone")
        view.findViewById<TextView>(R.id.tvPoiWebsite).text = arguments?.getString("poiWebsite")

        // Ici, vous pouvez également charger l'image si vous avez une URL ou une référence en arguments
        // Par exemple, avec Glide:
        // Glide.with(this).load(arguments?.getString("poiImageUrl")).into(view.findViewById<ImageView>(R.id.ivPoiImage))

        // Retournez la vue gonflée
        return view
    }
}