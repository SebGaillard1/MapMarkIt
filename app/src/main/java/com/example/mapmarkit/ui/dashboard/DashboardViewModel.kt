package com.example.mapmarkit.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapmarkit.AppDatabase
import com.example.mapmarkit.model.PointOfInterest

    class DashboardViewModel(application: Application) : AndroidViewModel(application) {
        private val db = AppDatabase.getDatabase(application)
        val allPointsOfInterest: LiveData<List<PointOfInterest>> = db.pointOfInterestDao().getAllPointsOfInterest()
    }