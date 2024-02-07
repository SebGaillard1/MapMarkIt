package com.example.mapmarkit.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmarkit.R

class PoiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val poiNameTextView: TextView = itemView.findViewById(R.id.poi_name)
    val poiLatTextView: TextView = itemView.findViewById(R.id.poi_lat)
    val poiLongTextView: TextView = itemView.findViewById(R.id.poi_long)
    val poiIdTextView: TextView = itemView.findViewById(R.id.poi_id)
}

