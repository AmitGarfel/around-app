package com.example.around.ui.formatters

object TourStationsUiFormatter {

    fun buildSubtitle(city: String): String =
        "Follow the stations and start exploring in $city ✨"

    fun destinationNotFound(): String = "Destination not found"

    fun noMoreStations(): String = "No more stations in this tour"

    fun noStationsFound(): String = "No stations found"

    fun currentStop(stationName: String): String {
        val safeName = stationName.ifBlank { "next station" }
        return "Current stop: $safeName"
    }

    fun reachedLastStation(): String = "You reached the last station 🎉"

    fun missingTourId(): String = "Missing tour id"

    fun loadFailed(message: String?): String {
        val safeMessage = message?.trim().orEmpty()
        return if (safeMessage.isBlank()) {
            "Load failed"
        } else {
            "Load failed: $safeMessage"
        }
    }
}