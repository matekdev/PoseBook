package com.example.posebook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.posebook.databinding.ActivityMainBinding
import com.example.posebook.manager.LocationManager

class MainActivity : AppCompatActivity(), CameraFragmentDelegate, MapFragmentDelegate {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setupBinding()
        setContentView(binding.root)

        // Hide the top bar.
        supportActionBar?.hide()

        // Setup and link bottom navigation bar.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun showReviewPopup() {
        LocationManager.getCurrentLocation(this)
    }

    override fun showMapReviewPopup(data: MarkerData) {
        val reviewPopup = MapReviewFragment(data)
        supportFragmentManager.beginTransaction().add(reviewPopup, reviewPopup.tag).commit()
    }

    private fun setupBinding() {
        LocationManager.locationObservable.subscribe { location ->
            val reviewPopup = SubmitReviewPopupFragment(
                location.cityName,
                location.country,
                location.coord
            )
            supportFragmentManager.beginTransaction().add(reviewPopup, reviewPopup.tag).commit()
        }
    }
}
