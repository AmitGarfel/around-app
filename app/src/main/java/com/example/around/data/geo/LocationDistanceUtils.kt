package com.example.around.data.geo

import android.location.Location

object LocationDistanceUtils {

    fun distanceInKm(
        fromLat: Double,
        fromLng: Double,
        toLat: Double,
        toLng: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(fromLat, fromLng, toLat, toLng, results)
        return results[0] / 1000f
    }
}