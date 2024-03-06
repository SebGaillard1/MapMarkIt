package com.example.mapmarkit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "point_of_interest")
data class PointOfInterest(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "latitude") val latitude: String,
    @ColumnInfo(name = "longitude") val longitude: String,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "phone") val phone: String? = null,
    @ColumnInfo(name = "rating") val rating: String? = null,
    @ColumnInfo(name = "types") val types: String? = null,
    @ColumnInfo(name = "photo_reference") val photoReference: String? = null,
    @ColumnInfo(name = "website") val website: String? = null,
    @ColumnInfo(name = "summary") val summary: String? = null,
    )

