package com.example.around.data.geo

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Repository responsible for converting address strings into LatLng coordinates.
 */
class GeocodingRepository(context: Context) {

    // הגדרה לעברית משפרת משמעותית את הדיוק בחיפוש מקומות בישראל
    private val geocoder = Geocoder(context, Locale("he", "IL"))

    // Cache מקומי כדי למנוע קריאות מיותרות לשרת עבור אותה כתובת
    private val cache = mutableMapOf<String, LatLng?>()

    /**
     * Converts a location name (e.g., "Cafe Gan Sipur, Hod Hasharon") to LatLng.
     */
    suspend fun geocode(query: String): LatLng? = withContext(Dispatchers.IO) {
        val trimmedQuery = query.trim()

        if (trimmedQuery.isBlank()) return@withContext null

        // בדיקה ב-Cache לפני פנייה ל-Geocoder
        if (cache.containsKey(trimmedQuery)) {
            Log.d("GEO_DEBUG", "Returning cached result for: $trimmedQuery")
            return@withContext cache[trimmedQuery]
        }

        return@withContext try {
            // הוספת "ישראל" לשאילתה אם היא חסרה כדי לצמצם תוצאות מחו"ל
            val finalQuery = if (!trimmedQuery.contains("ישראל") && !trimmedQuery.contains("Israel")) {
                "$trimmedQuery, ישראל"
            } else {
                trimmedQuery
            }

            Log.d("GEO_DEBUG", "Geocoding query: $finalQuery")

            // קבלת תוצאה מה-Geocoder
            val addresses = geocoder.getFromLocationName(finalQuery, 1)

            val result = if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                LatLng(address.latitude, address.longitude)
            } else {
                Log.w("GEO_DEBUG", "No coordinates found for: $finalQuery")
                null
            }

            // שמירה ב-Cache
            cache[trimmedQuery] = result
            result

        } catch (e: Exception) {
            Log.e("GEO_DEBUG", "Geocoding error for $trimmedQuery: ${e.localizedMessage}")
            null
        }
    }

    /**
     * מנקה את ה-Cache במידת הצורך
     */
    fun clearCache() {
        cache.clear()
    }
}