package com.example.posebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.posebook.databinding.ActivityMainBinding
import com.example.posebook.manager.LocationManager

class MainActivity : AppCompatActivity(), CameraFragmentDelegate, MapFragmentDelegate {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    // TODO: Temp Placeholder: Will add actual location later
    private fun populateLocationForReviewPopup(): Array<String>{
        return arrayOf("Surrey", "British Columbia, Canada")
    }

    override fun showReviewPopup() {
        LocationManager.getCurrentLocation(this)
    }

    override fun showMapReviewPopup() {
        val location = populateLocationForReviewPopup()
        if (location.count() == 2) {
            val reviewPopup = MapReviewFragment(
                location[0],
                location[1]
            )
            supportFragmentManager.beginTransaction().add(reviewPopup, reviewPopup.tag).commit()
        }
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
