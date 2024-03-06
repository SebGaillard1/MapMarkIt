package com.example.mapmarkit

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.mapmarkit.databinding.ActivityMainBinding
import com.google.android.libraries.places.api.Places

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val startWelcomeActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        setupMainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)

        if (firstStart) {
            startWelcomeActivityForResult.launch(Intent(this, WelcomeActivity::class.java))
        } else {
            setupMainActivity()
        }

        Places.initializeWithNewPlacesApiEnabled(applicationContext, "AIzaSyC8x6iTjvcg3Rgmj-UgdkZbrOD2FaVoV0o")
    }

    private fun setupMainActivity() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_dashboard, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}