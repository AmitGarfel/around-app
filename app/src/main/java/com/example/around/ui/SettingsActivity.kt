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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupBottomNav(R.id.nav_settings)

        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val user = auth.currentUser
        tvEmail.text = user?.email ?: "No email available"

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}