package com.example.around.ui.helpers

import com.example.around.domain.model.Station
import com.example.around.domain.model.Tour

object CreateTourHelper {

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
            status = "pending",
            stations = stations,
            startLatitude = stations.first().latitude,
            startLongitude = stations.first().longitude,
            createdBy = uid
        )
    }
}