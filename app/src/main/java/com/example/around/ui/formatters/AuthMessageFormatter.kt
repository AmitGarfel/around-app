package com.example.around.ui.formatters

object AuthMessageFormatter {

    fun loginSuccess(): String {
        return "Welcome back!"
    }

    fun loginFailed(message: String?): String {
        val safeMessage = message?.trim().orEmpty()
        return if (safeMessage.isBlank()) {
            "Login failed"
        } else {
            "Login failed: $safeMessage"
        }
    }

    fun registerSuccess(): String {
        return "Account created successfully!"
    }

    fun registerFailed(message: String?): String {
        val safeMessage = message?.trim().orEmpty()
        return if (safeMessage.isBlank()) {
            "Registration failed"
        } else {
            "Registration failed: $safeMessage"
        }
    }
}