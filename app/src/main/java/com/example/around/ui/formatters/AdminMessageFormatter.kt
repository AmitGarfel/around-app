package com.example.around.ui.formatters

object AdminMessageFormatter {

    fun loadError(): String = "Failed to load tours"

    fun statusUpdated(newStatus: String): String =
        "Tour status updated to $newStatus"

    fun updateFailed(): String = "Failed to update tour status"
}