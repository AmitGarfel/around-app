package com.example.around.data.geo

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationHelper(private val context: Context) {

    @SuppressLint("MissingPermission")
    fun getCityName(onResult: (String) -> Unit) {
        val fused = LocationServices.getFusedLocationProviderClient(context)

        fun geocodeCity(lat: Double, lng: Double) {
            try {
                val geocoder = Geocoder(context, Locale.ENGLISH)
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val cityName = addresses?.firstOrNull()?.locality ?: "your area"
                onResult(cityName)
            } catch (_: Exception) {
                onResult("local tours")
            }
        }

        fused.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                geocodeCity(location.latitude, location.longitude)
            } else {
                fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener { loc2 ->
                        if (loc2 != null) geocodeCity(loc2.latitude, loc2.longitude)
                        else onResult("local tours")
                    }
                    .addOnFailureListener { onResult("local tours") }
            }
        }.addOnFailureListener {
            onResult("local tours")
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLatLng(onResult: (LatLng?) -> Unit) {
        val fused = LocationServices.getFusedLocationProviderClient(context)

        fused.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onResult(LatLng(location.latitude, location.longitude))
            } else {
                fused.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener { loc2 ->
                        if (loc2 != null) {
                            onResult(LatLng(loc2.latitude, loc2.longitude))
                        } else {
                            onResult(null)
                        }
                    }
                    .addOnFailureListener {
                        onResult(null)
                    }
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }
}