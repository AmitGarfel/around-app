package com.example.around.ui.providers

import java.util.Calendar

object TimeContextProvider {

    fun getAutomaticTimeContext(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..11 -> "Morning"
            in 12..17 -> "Afternoon"
            else -> "Evening"
        }
    }
}