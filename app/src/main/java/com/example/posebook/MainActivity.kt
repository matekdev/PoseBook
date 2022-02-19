package com.example.posebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.posebook.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity(), CameraFragmentDelegate {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the top bar.
        supportActionBar?.hide()
    }

    // Temp Placeholder: Will add actual location later
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
}
