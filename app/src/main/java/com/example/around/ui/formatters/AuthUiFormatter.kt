package com.example.around.ui.formatters

import com.example.around.ui.models.AuthScreenMode

object AuthUiFormatter {

    data class AuthModeUi(
        val title: String,
        val subtitle: String,
        val actionText: String,
        val switchText: String,
        val showNameFields: Boolean,
        val showConfirmPassword: Boolean
    )

    fun build(mode: AuthScreenMode): AuthModeUi {
        return when (mode) {
            AuthScreenMode.LOGIN -> AuthModeUi(
                title = "Welcome back",
                subtitle = "Log in to continue exploring routes around you",
                actionText = "Login",
                switchText = "Don’t have an account? Sign up",
                showNameFields = false,
                showConfirmPassword = false
            )

            AuthScreenMode.REGISTER -> AuthModeUi(
                title = "Create account",
                subtitle = "Sign up and start building your own tours",
                actionText = "Create Account",
                switchText = "Already have an account? Login",
                showNameFields = true,
                showConfirmPassword = true
            )
        }
    }
}