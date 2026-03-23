package com.example.around.ui.formatters

import com.example.around.domain.model.Tour

object TourUiFormatter {

    fun buildDetails(tour: Tour): String {
        val duration = tour.estimatedDuration.ifBlank { "—" }
        val stationsText = when (tour.stations.size) {
            0 -> "no stations"
            1 -> "1 station"
            else -> "${tour.stations.size} stations"
        }

        return "$duration • $stationsText"
    }
}