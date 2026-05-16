package com.example.around.ui.helpers

class TourProgressManager(
    private val totalStations: Int
) {
    private var currentIndex: Int = 0

    fun currentIndex(): Int = currentIndex

    fun hasStations(): Boolean = totalStations > 0

    fun canMoveNext(): Boolean = currentIndex < totalStations - 1

    fun moveNext(): Boolean =
        if (canMoveNext()) {
            currentIndex++
            true
        } else {
            false
        }

    fun isValidIndex(index: Int): Boolean =
        index in 0 until totalStations
}