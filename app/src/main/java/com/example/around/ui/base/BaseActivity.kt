package com.example.around.ui.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.around.R
import com.example.around.ui.HomeActivity
import com.example.around.ui.MenuActivity
import com.example.around.ui.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    protected fun setupBottomNav(selectedItemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation) ?: return

        bottomNav.selectedItemId = selectedItemId

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_menu -> {
                    if (this !is MenuActivity) {
                        startActivity(Intent(this, MenuActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }

                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }

                R.id.nav_settings -> {
                    if (this !is SettingsActivity) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                    true
                }

                else -> false
            }
        }

        bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {

                R.id.nav_menu -> {
                    if (this !is MenuActivity) {
                        startActivity(Intent(this, MenuActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                }

                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                }

                R.id.nav_settings -> {
                    if (this !is SettingsActivity) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    }
                }
            }
        }
    }

    protected fun isUserLoggedIn(): Boolean {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        return user != null
    }
}