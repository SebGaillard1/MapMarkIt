package com.example.mapmarkit.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmarkit.R
import com.example.mapmarkit.model.PointOfInterest

class PoiAdapter(private var poiList: List<PointOfInterest>) : RecyclerView.Adapter<PoiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.poi_item_layout, parent, false)
        return PoiViewHolder(view)
    }

    override fun onBindViewHolder(holder: PoiViewHolder, position: Int) {
        val poi = poiList[position]

        holder.poiNameTextView.text = poi.name
        holder.poiLatTextView.text = "Latitude: ${poi.latitude}"
        holder.poiLongTextView.text = "Longitude: ${poi.longitude}"
        holder.poiIdTextView.text = "ID: ${poi.id}"
    }

    fun updateData(newData: List<PointOfInterest>) {
        poiList = newData
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return poiList.size
    }
}
