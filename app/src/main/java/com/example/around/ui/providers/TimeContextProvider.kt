package com.example.around.ui.providers

import java.util.Calendar

object TimeContextProvider {

    fun getAutomaticTimeContext(): String =
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Morning"
            in 12..17 -> "Afternoon"
            else -> "Evening"
        }
}