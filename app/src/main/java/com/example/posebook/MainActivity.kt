package com.example.posebook

<<<<<<< Updated upstream
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
=======
import android.content.ClipData
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
>>>>>>> Stashed changes
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.posebook.databinding.ActivityMainBinding
import com.example.posebook.manager.LocationManager
<<<<<<< Updated upstream
=======
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_maps.*
>>>>>>> Stashed changes

class MainActivity : AppCompatActivity(), CameraFragmentDelegate, MapFragmentDelegate {

    /****/
    var database = FirebaseDatabase.getInstance().reference
    /****/

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

        Log.d("MainActivity", "test")
        testRead()
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

    /****/
    private fun testRead(){
        database.child("37_4219983-122_084").child("reviews").child("review")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val testValue = snapshot.value.toString()
                    Log.d("TAG", testValue)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

//        database
//            .addListenerForSingleValueEvent(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val testValue = snapshot.value.toString()
//                    Log.d("TAG", testValue)
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })
    }
    /****/
}
