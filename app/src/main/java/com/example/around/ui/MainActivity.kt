package com.example.around.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.around.R
import com.example.around.di.AppGraph
import com.example.around.ui.formatters.AuthMessageFormatter
import com.example.around.ui.formatters.AuthUiFormatter
import com.example.around.ui.helpers.AuthFormReader
import com.example.around.ui.helpers.AuthFormValidator
import com.example.around.ui.helpers.AuthModeUiBinder
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

        AuthModeUiBinder.bind(
            ui = ui,
            titleText = titleText,
            subtitleText = subtitleText,
            switchModeText = switchModeText,
            actionButton = actionButton,
            firstNameInput = firstNameInput,
            lastNameInput = lastNameInput,
            confirmPasswordInput = confirmPasswordInput
        )
    }

    private fun login() {
        val form = AuthFormReader.readLogin(
            emailInput = emailInput,
            passwordInput = passwordInput
        )

        val error = AuthFormValidator.validateLogin(
            email = form.email,
            password = form.password
        )
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        authUseCase.login(
            email = form.email,
            password = form.password,
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
        val form = AuthFormReader.readRegister(
            firstNameInput = firstNameInput,
            lastNameInput = lastNameInput,
            emailInput = emailInput,
            passwordInput = passwordInput,
            confirmPasswordInput = confirmPasswordInput
        )

        val error = AuthFormValidator.validateRegister(
            firstName = form.firstName,
            lastName = form.lastName,
            email = form.email,
            password = form.password,
            confirmPassword = form.confirmPassword
        )

        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            return
        }

        authUseCase.register(
            firstName = form.firstName,
            lastName = form.lastName,
            email = form.email,
            password = form.password,
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