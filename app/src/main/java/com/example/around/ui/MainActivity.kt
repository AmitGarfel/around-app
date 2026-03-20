package com.example.around.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.around.R
import com.example.around.di.AppGraph

class MainActivity : AppCompatActivity() {

    private val authUseCase = AppGraph.authUseCase

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText

    private lateinit var titleText: TextView
    private lateinit var subtitleText: TextView
    private lateinit var switchModeText: TextView
    private lateinit var actionButton: Button

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (authUseCase.isLoggedIn()) {
            navigateToHome()
            return
        }

        setContentView(R.layout.activity_main)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)

        titleText = findViewById(R.id.tvFormTitle)
        subtitleText = findViewById(R.id.tvFormSubtitle)
        switchModeText = findViewById(R.id.tvSwitchMode)
        actionButton = findViewById(R.id.actionButton)

        updateModeUi()

        actionButton.setOnClickListener {
            if (isLoginMode) {
                login()
            } else {
                register()
            }
        }

        switchModeText.setOnClickListener {
            isLoginMode = !isLoginMode
            updateModeUi()
        }
    }

    private fun updateModeUi() {
        if (isLoginMode) {
            titleText.text = "Welcome back"
            subtitleText.text = "Log in to continue exploring routes around you"
            actionButton.text = "Login"
            confirmPasswordInput.visibility = View.GONE
            switchModeText.text = "Don’t have an account? Sign up"
        } else {
            titleText.text = "Create account"
            subtitleText.text = "Sign up and start building your own tours"
            actionButton.text = "Create Account"
            confirmPasswordInput.visibility = View.VISIBLE
            switchModeText.text = "Already have an account? Login"
        }
    }

    private fun login() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in email and password", Toast.LENGTH_SHORT).show()
            return
        }

        authUseCase.login(
            email = email,
            password = password,
            onSuccess = {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            },
            onError = { e ->
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun register() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        authUseCase.register(
            email = email,
            password = password,
            onSuccess = {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navigateToHome()
            },
            onError = { e ->
                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun navigateToHome() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}