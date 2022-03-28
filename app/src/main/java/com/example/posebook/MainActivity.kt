package com.example.posebook

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.posebook.databinding.ActivityMainBinding
import com.example.posebook.databinding.FragmentCameraBinding
import com.example.posebook.manager.LocationManager
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.OutputStream
import java.util.ArrayList

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
