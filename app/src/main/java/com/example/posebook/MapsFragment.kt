package com.example.posebook

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.photo_location_viewer_review_template.*


interface MapFragmentDelegate {
    fun showMapReviewPopup()
}

class MapsFragment : Fragment(), OnRemoveButtonTapListener {
    var database = FirebaseDatabase.getInstance().reference
    private lateinit var delegate: MapFragmentDelegate

    /*original code*/
//    private val callback = OnMapReadyCallback { googleMap ->
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        val sydney = LatLng(-34.0, 151.0)
//        val Perth = LatLng(-31.923270751916654, 115.86399911062028)
//
//        readData()
//
//        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        googleMap.addMarker(MarkerOptions().position(Perth).title("Marker in Perth"))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        googleMap.setOnMarkerClickListener(OnMarkerClickListener { //
//            showMapReviewPopup()
//            true
//        }
//        )
//
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)



    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MapFragmentDelegate) {
            delegate = context
        }
    }


    private fun showMapReviewPopup(){
        delegate.showMapReviewPopup();
    }


    private val callback = OnMapReadyCallback { googleMap ->


        var testCopy:Double = 0.0

        database
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val testValue = snapshot.value.toString()
//                    Log.d("MapsFragment", testValue)

                    //num of elements in database
                    val count = snapshot.childrenCount
                    //array to store the coordinate of locations
                    var locationCoorArr = DoubleArray((count*2).toInt()) {i -> 2.0}
                    //arraylist test
                    var locationArr = arrayListOf<Double>()

                    //loop to store the coordinate value
                    for (i in 0 until count){
                        var latitude : String = ""
                        var longitude : String = ""
                        //to get the latitude value
                        database.child((i+1).toString()).child("cityLocation").child("lat").addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                latitude = snapshot.value.toString()

                                locationArr.add(latitude.toDouble())
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }

                        })
                        database.child((i+1).toString()).child("cityLocation").child("long").addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                longitude = snapshot.value.toString()
                                locationArr.add(longitude.toDouble())

                                Log.d("laititude value", latitude)
                                Log.d("longitude value", longitude)
                                googleMap.addMarker(MarkerOptions().position(LatLng(locationArr[(2*i).toInt()], locationArr[(2*i+1).toInt()])).title("Marker"))
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(locationArr[0], locationArr[1])))
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                        Log.d("num of i", i.toString())
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        val sydney = LatLng(-34.0, 151.0)
        val Perth = LatLng(-31.923270751916654, 115.86399911062028)

        googleMap.setOnMarkerClickListener(OnMarkerClickListener { //
            showMapReviewPopup()
            true
        }

        )




    }


    override fun onRemoveButtonTapped() {
        val frag = childFragmentManager.findFragmentByTag("SubmitMapReviewPopupFragment")
        childFragmentManager
            .beginTransaction()
            .remove(frag!!)
            .commit()
    }



}

