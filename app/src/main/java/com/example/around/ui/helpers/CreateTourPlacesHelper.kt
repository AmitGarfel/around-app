package com.example.around.ui.helpers

object CreateTourPlacesHelper {

    fun buildInitialQuery(
        currentText: String,
        city: String
    ): String {
        return when {
            currentText.isNotBlank() && city.isNotBlank() -> "$currentText $city"
            currentText.isNotBlank() -> currentText
            city.isNotBlank() -> city
            else -> ""
        }
    }
}