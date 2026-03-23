package com.example.around.data.preferences

import android.content.Context

class UserPrefsProvider(context: Context) {

    private val prefs = context.getSharedPreferences("around_prefs", Context.MODE_PRIVATE)

    fun saveLastCity(city: String) {
        prefs.edit().putString(KEY_LAST_CITY, city).apply()
    }

    fun getLastCity(): String {
        return prefs.getString(KEY_LAST_CITY, "Tel Aviv") ?: "Tel Aviv"
    }

    companion object {
        private const val KEY_LAST_CITY = "last_detected_city"
    }
}