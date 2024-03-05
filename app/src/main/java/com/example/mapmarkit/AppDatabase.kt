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
            // Si l'instance n'est pas null, la retourner
            return INSTANCE ?: synchronized(this) {
                // Sinon, créer l'instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "point_of_interest_database"
                )
                    // MigrationStrategy ici si nécessaire
                    //.fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // Retourner l'instance créée
                instance
            }
        }
    }
}