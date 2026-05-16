package com.example.around.ui.formatters

// Provides user-facing authentication messages
object AuthMessageFormatter {

    fun loginSuccess(): String = "Welcome back!"

    fun loginFailed(message: String?): String =
        formatError("Login failed", message)

    fun registerSuccess(): String = "Account created successfully!"

    fun registerFailed(message: String?): String =
        formatError("Registration failed", message)

    // Appends backend error message only if it exists
    private fun formatError(base: String, message: String?): String {
        val safeMessage = message?.trim().orEmpty()
        return if (safeMessage.isBlank()) base else "$base: $safeMessage"
    }
}