package com.example.around.ui.formatters

object MenuQuickInfoFormatter {

    fun build(city: String): String =
        "${city.ifBlank { "your area" }} Tours"
}