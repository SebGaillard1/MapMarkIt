package com.example.mapmarkit.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class PointOfInterest(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "latitude") val latitude: String,
    @ColumnInfo(name = "longitude") val longitude: String
)

