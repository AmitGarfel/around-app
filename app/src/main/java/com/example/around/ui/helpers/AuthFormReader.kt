package com.example.around.ui.helpers

import android.widget.EditText
import com.example.around.ui.models.AuthFormValues

object AuthFormReader {

    fun readLogin(
        emailInput: EditText,
        passwordInput: EditText
    ): AuthFormValues {
        return AuthFormValues(
            email = emailInput.text.toString().trim(),
            password = passwordInput.text.toString().trim()
        )
    }

    fun readRegister(
        firstNameInput: EditText,
        lastNameInput: EditText,
        emailInput: EditText,
        passwordInput: EditText,
        confirmPasswordInput: EditText
    ): AuthFormValues {
        return AuthFormValues(
            firstName = firstNameInput.text.toString().trim(),
            lastName = lastNameInput.text.toString().trim(),
            email = emailInput.text.toString().trim(),
            password = passwordInput.text.toString().trim(),
            confirmPassword = confirmPasswordInput.text.toString().trim()
        )
    }
}