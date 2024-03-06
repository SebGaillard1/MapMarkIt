package com.example.mapmarkit

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mapmarkit.model.PointOfInterest

@Dao
interface PoiDao {
    @Query("SELECT * FROM point_of_interest")
    fun getAllPointsOfInterest(): LiveData<List<PointOfInterest>>

    @Query("SELECT EXISTS(SELECT 1 FROM point_of_interest WHERE id = :id)")
    suspend fun isPoiFavorited(id: String): Boolean

    @Insert
    suspend fun insert(pointOfInterest: PointOfInterest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplacePoi(pointOfInterest: PointOfInterest)

    @Delete
    suspend fun delete(pointOfInterest: PointOfInterest)

    @Query("DELETE FROM point_of_interest WHERE id = :id")
    suspend fun deleteById(id: String)
}