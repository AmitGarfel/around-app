package com.example.around.ui.helpers

import com.example.around.R
import com.example.around.domain.model.Station
import com.example.around.domain.model.Tour
import com.example.around.domain.model.TourStatus

object CreateTourFormHelper {

    fun validateBasicFields(
        tourName: String,
        city: String
    ): String? {
        return when {
            tourName.isBlank() -> "Please enter a tour name"
            city.isBlank() -> "Please enter a city"
            else -> null
        }
    }

    fun validateStations(stations: List<Station>): String? {
        return if (stations.size < 2) {
            "Please add at least 2 stations"
        } else null
    }

    fun buildTour(
        name: String,
        city: String,
        description: String,
        mood: String,
        timeTag: String,
        duration: String,
        stations: List<Station>,
        uid: String
    ): Tour {
        return Tour(
            name = name,
            city = city,
            description = description,
            mood = mood,
            timeTag = timeTag,
            estimatedDuration = duration,
            status = TourStatus.PENDING,
            stations = stations,
            startLatitude = stations.first().latitude,
            startLongitude = stations.first().longitude,
            createdBy = uid
        )
    }

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

    fun stationFieldIds(): List<Int> {
        return listOf(
            R.id.etStation1,
            R.id.etStation2,
            R.id.etStation3,
            R.id.etStation4
        )
    }

    fun stationFieldIdAt(index: Int): Int? {
        return stationFieldIds().getOrNull(index)
    }
}