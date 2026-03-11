package com.example.around.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.around.R
import com.example.around.di.AppGraph

class MainActivity : AppCompatActivity() {

    private val authUseCase = AppGraph.authUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // אם המשתמש כבר מחובר – דלג על מסך הלוגין
        if (authUseCase.isLoggedIn()) {
            navigateToHome()
            return
        }

        setContentView(R.layout.activity_main)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // ---------------- REGISTER ----------------
        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "נא למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authUseCase.register(
                email = email,
                password = password,
                onSuccess = {
                    Toast.makeText(this, "נרשמת בהצלחה, עמיתוש!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                },
                onError = { e ->
                    Toast.makeText(this, "שגיאה בהרשמה: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }

        // ---------------- LOGIN ----------------
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "נא למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authUseCase.login(
                email = email,
                password = password,
                onSuccess = {
                    Toast.makeText(this, "כיף שחזרת!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                },
                onError = { e ->
                    Toast.makeText(this, "שגיאה בכניסה: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}
