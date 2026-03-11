package com.example.around.ui.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.around.R
import com.example.around.ui.MenuActivity
import com.example.around.ui.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    protected fun setupBottomNav(selectedItemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation) ?: return

        // highlight current tab
        bottomNav.selectedItemId = selectedItemId

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_menu -> {
                    if (selectedItemId != R.id.nav_menu) {
                        startActivity(Intent(this, MenuActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        })
                        finish()
                    }
                    true
                }

                R.id.nav_settings -> {
                    if (selectedItemId != R.id.nav_settings) {
                        startActivity(Intent(this, SettingsActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        })
                        finish()
                    }
                    true
                }

                else -> false
            }
        }
    }

    protected fun isUserLoggedIn(): Boolean {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        return user != null
    }
}