package com.example.around.data.geo

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class GeocodingRepository(context: Context) {

    private val geocoder = Geocoder(context, Locale.ENGLISH)

    // ✅ cache
    private val cache = mutableMapOf<String, LatLng?>()

    suspend fun geocode(query: String): LatLng? {
        val key = query.trim()
        if (key.isBlank()) return null

        if (cache.containsKey(key)) return cache[key]

        return try {
            val results = geocoder.getFromLocationName(key, 1)
            val latLng = results?.firstOrNull()?.let { LatLng(it.latitude, it.longitude) }
            cache[key] = latLng
            latLng
        } catch (_: Exception) {
            cache[key] = null
            null
        }
    }
}