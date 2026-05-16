package com.example.around.ui.helpers

import android.widget.EditText
import com.example.around.ui.models.AuthFormValues

object AuthFormReader {

    fun readLogin(
        emailInput: EditText,
        passwordInput: EditText
    ): AuthFormValues =
        AuthFormValues(
            email = emailInput.value(),
            password = passwordInput.value()
        )

    fun readRegister(
        firstNameInput: EditText,
        lastNameInput: EditText,
        emailInput: EditText,
        passwordInput: EditText,
        confirmPasswordInput: EditText
    ): AuthFormValues =
        AuthFormValues(
            firstName = firstNameInput.value(),
            lastName = lastNameInput.value(),
            email = emailInput.value(),
            password = passwordInput.value(),
            confirmPassword = confirmPasswordInput.value()
        )

    // Extracts trimmed string from EditText
    private fun EditText.value(): String =
        text.toString().trim()
}