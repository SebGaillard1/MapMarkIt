package com.example.mapmarkit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapmarkit.model.PointOfInterest

@Database(entities = [PointOfInterest::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pointOfInterestDao(): PoiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "point_of_interest_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}