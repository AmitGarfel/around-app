package com.example.around.ui.helpers

object CreateTourDurationHelper {

    fun countFilledStations(values: List<String>): Int {
        return values.count { it.trim().isNotEmpty() }
    }

    fun suggestDuration(stationsCount: Int): String {
        return when (stationsCount) {
            0, 1, 2 -> "30–60 min"
            3, 4 -> "1–1.5 hours"
            else -> "1.5–2 hours"
        }
    }
}