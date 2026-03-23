package com.example.around.ui.helpers

object AuthFormValidator {

    fun validateLogin(
        email: String,
        password: String
    ): String? {
        return when {
            email.isBlank() -> "Please enter email"
            password.isBlank() -> "Please enter password"
            else -> null
        }
    }

    fun validateRegister(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            firstName.isBlank() -> "Please enter first name"
            lastName.isBlank() -> "Please enter last name"
            email.isBlank() -> "Please enter email"
            password.isBlank() -> "Please enter password"
            confirmPassword.isBlank() -> "Please confirm password"
            password.length < 6 -> "Password must be at least 6 characters"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }
}