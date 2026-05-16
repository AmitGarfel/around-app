package com.example.around.data.geo

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class GeocodingRepository(context: Context) {

    private val geocoder = Geocoder(
        context,
        Locale.Builder()
            .setLanguage("he")
            .setRegion("IL")
            .build()
    )

    private val cache = mutableMapOf<String, LatLng?>()

    suspend fun geocode(query: String): LatLng? = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return@withContext null

        cache[trimmedQuery]?.let { return@withContext it }

        val finalQuery = if (
            !trimmedQuery.contains("ישראל", ignoreCase = true) &&
            !trimmedQuery.contains("Israel", ignoreCase = true)
        ) {
            "$trimmedQuery, ישראל"
        } else {
            trimmedQuery
        }

        val result = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocodeApi33(finalQuery)
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocationName(finalQuery, 1)
                    ?.firstOrNull()
                    ?.toLatLng()
            }
        } catch (e: Exception) {
            Log.e("GEO_DEBUG", e.localizedMessage ?: "Error")
            null
        }

        cache[trimmedQuery] = result
        result
    }

    private suspend fun geocodeApi33(query: String): LatLng? =
        suspendCancellableCoroutine { continuation ->
            geocoder.getFromLocationName(
                query,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        continuation.resume(addresses.firstOrNull()?.toLatLng())
                    }

                    override fun onError(errorMessage: String?) {
                        Log.e("GEO_DEBUG", errorMessage ?: "Error")
                        continuation.resume(null)
                    }
                }
            )
        }

    private fun Address.toLatLng(): LatLng = LatLng(latitude, longitude)
}