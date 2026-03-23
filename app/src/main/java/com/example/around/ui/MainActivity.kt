package com.example.around.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.around.R
import com.example.around.di.AppGraph
import com.example.around.ui.formatters.AuthMessageFormatter
import com.example.around.ui.formatters.AuthUiFormatter
import com.example.around.ui.helpers.AuthFormValidator
import com.example.around.ui.helpers.AuthNavigationHelper
import com.example.around.ui.models.AuthScreenMode

class MainActivity : AppCompatActivity() {

    private val authUseCase = AppGraph.authUseCase

    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText

    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var switchModeText: TextView
    private lateinit var actionButton: Button

    private var screenMode = AuthScreenMode.LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (authUseCase.isLoggedIn()) {
            AuthNavigationHelper.openHome(this)
            return
        }

        setContentView(R.layout.activity_main)

        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)

        titleText = findViewById(R.id.tvFormTitle)
        subtitleText = findViewById(R.id.tvFormSubtitle)
        switchModeText = findViewById(R.id.tvSwitchMode)
        actionButton = findViewById(R.id.actionButton)

        updateModeUi()

        actionButton.setOnClickListener {
            when (screenMode) {
                AuthScreenMode.LOGIN -> login()
                AuthScreenMode.REGISTER -> register()
            }
        }

        switchModeText.setOnClickListener {
            screenMode = when (screenMode) {
                AuthScreenMode.LOGIN -> AuthScreenMode.REGISTER
                AuthScreenMode.REGISTER -> AuthScreenMode.LOGIN
            }
            updateModeUi()
        }
    }

    private fun updateModeUi() {
        val ui = AuthUiFormatter.build(screenMode)

        titleText.text = ui.title
        subtitleText.text = ui.subtitle
        actionButton.text = ui.actionText
        switchModeText.text = ui.switchText

        firstNameInput.visibility = if (ui.showNameFields) View.VISIBLE else View.GONE
        lastNameInput.visibility = if (ui.showNameFields) View.VISIBLE else View.GONE
        confirmPasswordInput.visibility = if (ui.showConfirmPassword) View.VISIBLE else View.GONE
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        val error = AuthFormValidator.validateLogin(email, password)
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        authUseCase.login(
            email = email,
            password = password,
            onSuccess = {
                Toast.makeText(
                    this,
                    AuthMessageFormatter.loginSuccess(),
                    Toast.LENGTH_SHORT
                ).show()
                AuthNavigationHelper.openHome(this)
            },
            onError = { e ->
                Toast.makeText(
                    this,
                    AuthMessageFormatter.loginFailed(e.message),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun register() {
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        val error = AuthFormValidator.validateRegister(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            confirmPassword = confirmPassword
        )

        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        authUseCase.register(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            onSuccess = {
                Toast.makeText(
                    this,
                    AuthMessageFormatter.registerSuccess(),
                    Toast.LENGTH_SHORT
                ).show()
                AuthNavigationHelper.openHome(this)
            },
            onError = { e ->
                Toast.makeText(
                    this,
                    AuthMessageFormatter.registerFailed(e.message),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}