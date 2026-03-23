package com.example.around.ui.providers

import android.widget.Spinner
import com.example.around.util.CityNormalizer

object HomeSelectionProvider {

    fun resolveSelectedTime(
        timeSpinner: Spinner,
        fallbackTime: String
    ): String {
        return timeSpinner.selectedItem?.toString() ?: fallbackTime
    }

    fun resolveSelectedCity(
        citySpinner: Spinner,
        detectedCity: String
    ): String {
        val selected = citySpinner.selectedItem?.toString()?.trim().orEmpty()

        return if (selected.startsWith("Near me", ignoreCase = true) || selected.isBlank()) {
            detectedCity
        } else {
            CityNormalizer.canonical(selected)
        }
    }
}