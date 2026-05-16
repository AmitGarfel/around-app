package com.example.around.ui.base

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.around.R
import com.example.around.ui.HomeActivity
import com.example.around.ui.MenuActivity
import com.example.around.ui.SettingsActivity
import com.example.around.util.NavigationKeys
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

abstract class BaseActivity : AppCompatActivity() {

    private var ignoreNextSelection = false

    protected fun setupBottomNav(selectedItemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation) ?: return

        styleBottomNav(bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            if (ignoreNextSelection) {
                ignoreNextSelection = false
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.nav_menu -> {
                    if (this !is MenuActivity) {
                        val intent = Intent(this, MenuActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                        startActivity(intent)
                        applyNoAnimationTransition()
                    }
                    true
                }

                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        val savedCity = getSharedPreferences("around_prefs", MODE_PRIVATE)
                            .getString("last_detected_city", "Tel Aviv")
                            .orEmpty()

                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra(NavigationKeys.EXTRA_CITY, savedCity)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }

                        startActivity(intent)
                        applyNoAnimationTransition()
                    }
                    true
                }

                R.id.nav_settings -> {
                    if (this !is SettingsActivity) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        applyNoAnimationTransition()
                    }
                    true
                }

                else -> false
            }
        }

        bottomNav.setOnItemReselectedListener {
            // No action on reselect
        }

        refreshBottomNavSelection(selectedItemId)
    }

    protected fun refreshBottomNavSelection(selectedItemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation) ?: return

        styleBottomNav(bottomNav)

        if (bottomNav.selectedItemId != selectedItemId) {
            ignoreNextSelection = true
            bottomNav.selectedItemId = selectedItemId
        }
    }

    private fun styleBottomNav(bottomNav: BottomNavigationView) {
        bottomNav.layoutDirection = View.LAYOUT_DIRECTION_LTR

        val colors = ContextCompat.getColorStateList(this, R.color.bottom_nav_item_colors)
        bottomNav.itemIconTintList = colors
        bottomNav.itemTextColor = colors
    }

    private fun applyNoAnimationTransition() {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    protected fun isUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}