package com.example.around.ui.formatters

object MenuQuickInfoFormatter {

    fun build(city: String): String {
        val safeCity = if (city.isBlank()) "your area" else city
        return "Near you: $safeCity Tours"
    }
}