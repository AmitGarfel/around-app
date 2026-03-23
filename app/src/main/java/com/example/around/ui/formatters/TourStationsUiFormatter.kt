package com.example.around.ui.formatters

object TourStationsUiFormatter {

    fun buildSubtitle(city: String): String {
        return "Follow the stations and start exploring in $city ✨"
    }

    fun destinationNotFound(): String {
        return "Destination not found"
    }

    fun noMoreStations(): String {
        return "No more stations in this tour"
    }

    fun noStationsFound(): String {
        return "No stations found"
    }

    fun currentStop(stationName: String): String {
        val safeName = stationName.ifBlank { "next station" }
        return "Current stop: $safeName"
    }

    fun reachedLastStation(): String {
        return "You reached the last station 🎉"
    }

    fun missingTourId(): String {
        return "Missing tour id"
    }

    fun loadFailed(message: String?): String {
        val safeMessage = message?.trim().orEmpty()
        return if (safeMessage.isBlank()) {
            "Load failed"
        } else {
            "Load failed: $safeMessage"
        }
    }
}