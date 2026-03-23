package com.example.around.ui.models

data class AuthFormValues(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)