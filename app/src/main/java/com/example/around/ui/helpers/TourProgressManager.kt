package com.example.around.ui.helpers

class TourProgressManager(
    private val totalStations: Int
) {
    private var currentIndex: Int = 0

    fun currentIndex(): Int {
        return currentIndex
    }

    fun hasStations(): Boolean {
        return totalStations > 0
    }

    fun canMoveNext(): Boolean {
        return currentIndex < totalStations - 1
    }

    fun moveNext(): Boolean {
        return if (canMoveNext()) {
            currentIndex++
            true
        } else {
            false
        }
    }

    fun reset() {
        currentIndex = 0
    }

    fun isValidIndex(index: Int): Boolean {
        return index in 0 until totalStations
    }
}