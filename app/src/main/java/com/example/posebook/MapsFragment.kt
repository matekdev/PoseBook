package com.example.posebook

import android.content.Context
import android.os.Bundle
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


interface MapFragmentDelegate {
    fun showMapReviewPopup(data: MarkerData)
}

data class MarkerData(val address: String, val lat: Double, val long: Double, val ratings: ArrayList<Long>, val reviews: ArrayList<String>)

class MapsFragment : Fragment(), OnRemoveButtonTapListener {
    var database = FirebaseDatabase.getInstance().reference
    private lateinit var delegate: MapFragmentDelegate

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

    private fun showMapReviewPopup(data: MarkerData){
        delegate.showMapReviewPopup(data);
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val markerMap : HashMap<Marker, MarkerData> = HashMap()
        database.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val children = snapshot.children
                    children.forEach {
                        val data = it.value as HashMap<*, *>
                        val locationData = data["cityLocation"] as HashMap<*, *>
                        val reviewData = data["reviewData"] as HashMap<*, *>

                        val address = locationData["address"] as String
                        val lat = locationData["lat"] as Double
                        val long = locationData["long"] as Double

                        val rating = reviewData["rating"] as Long
                        val review = reviewData["review"] as String

                        var duplicateMarker = false
                        val latLong = LatLng(lat, long)
                        markerMap.forEach{(key, _) ->
                            if (key.position == latLong) {
                                markerMap[key]?.ratings?.add(rating)
                                markerMap[key]?.reviews?.add(review)
                                duplicateMarker = true
                            }
                        }

                        if (!duplicateMarker)
                        {
                            val marker = googleMap.addMarker(MarkerOptions().position(LatLng(lat, long)).title(address))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, long)))
                            if (marker != null) {
                                markerMap[marker] = MarkerData(address, lat, long, arrayListOf(rating), arrayListOf(review))
                            };
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
        })

        googleMap.setOnMarkerClickListener(OnMarkerClickListener { marker -> // on marker click we are getting the title of our marker
            val data = markerMap[marker]
            if (data != null)
                showMapReviewPopup(data)
            true
        })
    }

    override fun onRemoveButtonTapped() {
        val frag = childFragmentManager.findFragmentByTag("SubmitMapReviewPopupFragment")
        childFragmentManager
            .beginTransaction()
            .remove(frag!!)
            .commit()
    }
}

