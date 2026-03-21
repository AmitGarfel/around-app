package com.example.around.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.around.R
import com.example.around.di.AppGraph
import com.example.around.ui.base.BaseActivity

class SettingsActivity : BaseActivity() {

    private val auth = AppGraph.auth
    private val usersRepo = AppGraph.usersRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupBottomNav(R.id.nav_settings)

        val tvFirstName = findViewById<TextView>(R.id.tvFirstName)
        val tvLastName = findViewById<TextView>(R.id.tvLastName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val user = auth.currentUser
        val uid = user?.uid

        tvEmail.text = user?.email ?: "No email available"
        tvFirstName.text = "—"
        tvLastName.text = "—"

        if (uid != null) {
            usersRepo.getUserProfile(uid) { firstName, lastName, firestoreEmail ->
                runOnUiThread {
                    tvFirstName.text = if (!firstName.isNullOrBlank()) firstName else "—"
                    tvLastName.text = if (!lastName.isNullOrBlank()) lastName else "—"

                    val finalEmail = when {
                        !user.email.isNullOrBlank() -> user.email
                        !firestoreEmail.isNullOrBlank() -> firestoreEmail
                        else -> "No email available"
                    }

                    tvEmail.text = finalEmail
                }
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshBottomNavSelection(R.id.nav_settings)
    }
}