package com.example.around.ui.formatters

// Builds greeting message based on time of day and user name
object HomeGreetingFormatter {

    fun buildGreeting(firstName: String?, timeContext: String): String {
        val safeName = firstName?.takeIf { it.isNotBlank() } ?: "there"

        val (greeting, emoji) = when (timeContext) {
            "Morning" -> "Good morning" to "☀️"
            "Afternoon" -> "Good afternoon" to "🌤️"
            else -> "Good evening" to "🌙"
        }

        return "$greeting, $safeName $emoji"
    }
}