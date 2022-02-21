package com.example.posebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.posebook.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.fragment_maps.*

class MainActivity : AppCompatActivity(), CameraFragmentDelegate, MapFragmentDelegate {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
        val location = populateLocationForReviewPopup()
        if (location.count() == 2) {
            val reviewPopup = SubmitReviewPopupFragment(
                location[0],
                location[1]
            )
            supportFragmentManager.beginTransaction().add(reviewPopup, reviewPopup.tag).commit()
        }
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

}
