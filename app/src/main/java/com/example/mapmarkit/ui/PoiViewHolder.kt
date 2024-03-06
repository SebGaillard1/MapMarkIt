package com.example.mapmarkit.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapmarkit.R

class PoiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val poiImageView: ImageView = itemView.findViewById(R.id.poi_image)
    val poiNameTextView: TextView = itemView.findViewById(R.id.poi_name)
    val poiTypeTextView: TextView = itemView.findViewById(R.id.poi_type)
    val poiAddressTextView: TextView = itemView.findViewById(R.id.poi_address)
    val poiRatingTextView: TextView = itemView.findViewById(R.id.poi_rating)
    val poiPhoneTextView: TextView = itemView.findViewById(R.id.poi_phone)
}

