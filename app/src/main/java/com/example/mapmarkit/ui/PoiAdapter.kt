package com.example.mapmarkit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mapmarkit.R
import com.example.mapmarkit.model.PointOfInterest

class PoiAdapter(private var poiList: List<PointOfInterest>) : RecyclerView.Adapter<PoiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.poi_item_layout, parent, false)
        return PoiViewHolder(view)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val poi = poiList[position]
        val photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${poi.photoReference}&key=AIzaSyC8x6iTjvcg3Rgmj-UgdkZbrOD2FaVoV0o"

        Glide.with(holder.itemView.context)
            .load(photoUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder standard
            .into(holder.poiImageView)
        holder.poiNameTextView.text = poi.name
        holder.poiTypeTextView.text = "${poi.types}"
        holder.poiAddressTextView.text = "📍${poi.address}"
        holder.poiRatingTextView.text = "⭐ ${poi.rating}"
        holder.poiPhoneTextView.text = "📞 ${poi.phone}"
    }

    fun updateData(newData: List<PointOfInterest>) {
        poiList = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return poiList.size
    }
}
