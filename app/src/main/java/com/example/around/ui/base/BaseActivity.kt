package com.example.around.ui.base

import android.content.Intent
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.around.R
import com.example.around.ui.HomeActivity
import com.example.around.ui.MenuActivity
import com.example.around.ui.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                        overridePendingTransition(0, 0)
                    }
                    true
                }

                R.id.nav_home -> {
                    if (this !is HomeActivity) {
                        val savedCity = getSharedPreferences("around_prefs", MODE_PRIVATE)
                            .getString("last_detected_city", "Tel Aviv")
                            .orEmpty()

                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra(com.example.around.util.NavigationKeys.EXTRA_CITY, savedCity)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }

                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                    true
                }

                R.id.nav_settings -> {
                    if (this !is SettingsActivity) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                        overridePendingTransition(0, 0)
                    }
                    true
                }

                else -> false
            }
        }

        bottomNav.setOnItemReselectedListener {
            // do nothing
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
        ViewCompat.setLayoutDirection(bottomNav, ViewCompat.LAYOUT_DIRECTION_LTR)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bottomNav.itemIconTintList = getColorStateList(R.color.bottom_nav_item_colors)
            bottomNav.itemTextColor = getColorStateList(R.color.bottom_nav_item_colors)
        } else {
            @Suppress("DEPRECATION")
            run {
                bottomNav.itemIconTintList =
                    resources.getColorStateList(R.color.bottom_nav_item_colors)
                bottomNav.itemTextColor =
                    resources.getColorStateList(R.color.bottom_nav_item_colors)
            }
        }
    }

    protected fun forceLtr(view: View?) {
        view ?: return
        ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_LTR)
    }

    protected fun isUserLoggedIn(): Boolean {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        return user != null
    }
}