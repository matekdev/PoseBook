package com.example.posebook.manager

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.*
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.IOException
import java.util.*

data class CityLocation (var cityName: String, var country: String) {
    constructor() : this("", "")
}

class LocationManager {
        companion object {
            val locationObservable: PublishSubject<CityLocation> =
                PublishSubject.create<CityLocation>()
            private var cityLocation = CityLocation()
            const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100

            private fun addCommaIfNeeded() {
                if (cityLocation.country.isNotEmpty()) {
                    cityLocation.country += ", "
                }
            }

            private fun getLocationName(location: String): String {
                val locationNameEndIndex = location.indexOf(",")
                return location.substring(0, locationNameEndIndex)
            }

            private fun getCountryName(location: String): String {
                val locationNameEndIndex = location.indexOf(",")
                return location.substring(locationNameEndIndex + 2, location.length)
            }

            fun getAddressOfCoordinates(context: Context, lat: Double, lon: Double) {
                val geocoder = Geocoder(context, Locale.getDefault())
                cityLocation = CityLocation()
                try {

                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]

                        if (address.getAddressLine(0) != null) {
                            cityLocation.cityName = getLocationName(address.getAddressLine(0))
                        }
                        if (address.locality != null) {
                            cityLocation.country = address.locality
                        }
                        if (address.adminArea != null) {
                            addCommaIfNeeded()
                            cityLocation.country += address.adminArea
                        }
                        if (address.subAdminArea != null) {
                            addCommaIfNeeded()
                            cityLocation.country += address.subAdminArea
                        }
                        if (address.countryName != null) {
                            addCommaIfNeeded()
                            cityLocation.country += address.countryName
                        }
                        Log.i("Geocoder: ", address.toString())
                    }
                } catch (error: IOException) {
                    Log.e("Geocoder Error: ", error.toString())
                }
                Log.i("Geocoder: ", cityLocation.toString())
                locationObservable.onNext(cityLocation)
            }

            fun checkPermission(context: Context, activity: Activity): Boolean {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_REQUEST_ACCESS_FINE_LOCATION
                    )
                    return false
                }
                return true
            }

            @SuppressLint("MissingPermission")
            fun getCurrentLocation(context: Context) {
                val fusedLocationManager = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationManager.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        Log.i("Current Location: ", location.toString())
                        if (location?.latitude != null && location?.longitude != null) {
                            getAddressOfCoordinates(
                                context,
                                location.latitude,
                                location.longitude
                            )
                        }
                    }
            }
        }
}