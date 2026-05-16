package com.example.around.data.geo

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationHelper(private val context: Context) {

    @SuppressLint("MissingPermission")
    fun getCityName(onResult: (String) -> Unit) {
        val fused = LocationServices.getFusedLocationProviderClient(context)

        fun geocodeCity(lat: Double, lng: Double) {
            val geocoder = Geocoder(context, Locale.ENGLISH)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use new async API for API 33+
                geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        val city = addresses.firstOrNull()?.locality ?: "your area"
                        onResult(city)
                    }

                    override fun onError(errorMessage: String?) {
                        onResult("local tours")
                    }
                })
            } else {
                try {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    val city = addresses?.firstOrNull()?.locality ?: "your area"
                    onResult(city)
                } catch (_: Exception) {
                    onResult("local tours")
                }
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
                        onResult(loc2?.let { LatLng(it.latitude, it.longitude) })
                    }
                    .addOnFailureListener { onResult(null) }
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }
}