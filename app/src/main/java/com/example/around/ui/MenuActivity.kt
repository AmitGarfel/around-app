package com.example.around.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.around.R
import com.example.around.data.geo.LocationHelper
import com.example.around.data.preferences.UserPrefsProvider
import com.example.around.di.AppGraph
import com.example.around.ui.base.BaseActivity
import com.example.around.ui.formatters.MenuQuickInfoFormatter
import com.example.around.ui.helpers.MenuNavigationHelper

class MenuActivity : BaseActivity() {

    private val usersRepo = AppGraph.usersRepo
    private val auth = AppGraph.auth

    private lateinit var tvQuickInfo: TextView
    private lateinit var locationHelper: LocationHelper
    private lateinit var userPrefs: UserPrefsProvider

    private var detectedCity: String = "Tel Aviv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        setupBottomNav(R.id.nav_menu)

        val mainLayout = findViewById<LinearLayout>(R.id.menuMainLayout)

        val layoutRegularActions = findViewById<LinearLayout>(R.id.layoutRegularActions)
        val layoutAdminActions = findViewById<LinearLayout>(R.id.layoutAdminActions)

        val btnCreateRegular = findViewById<ImageButton>(R.id.btnCreateRegular)
        val btnExploreRegular = findViewById<ImageButton>(R.id.btnExploreRegular)

        val btnCreateAdmin = findViewById<ImageButton>(R.id.btnCreateAdmin)
        val btnExploreAdmin = findViewById<ImageButton>(R.id.btnExploreAdmin)
        val btnAdmin = findViewById<ImageButton>(R.id.btnAdmin)

        tvQuickInfo = findViewById(R.id.tvQuickInfo)
        locationHelper = LocationHelper(this)
        userPrefs = UserPrefsProvider(this)

        detectedCity = userPrefs.getLastCity()
        tvQuickInfo.text = MenuQuickInfoFormatter.build(detectedCity)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        mainLayout.startAnimation(fadeIn)

        checkLocationPermission()

        if (!isUserLoggedIn()) {
            goToLogin()
            return
        }

        val uid = auth.currentUser?.uid ?: return
        checkAdminStatus(uid, layoutRegularActions, layoutAdminActions)

        bindCreateClick(btnCreateRegular)
        bindExploreClick(btnExploreRegular)
        bindCreateClick(btnCreateAdmin)
        bindExploreClick(btnExploreAdmin)
        bindAdminClick(btnAdmin)
    }

    override fun onResume() {
        super.onResume()
        refreshBottomNavSelection(R.id.nav_menu)
    }

    private fun bindCreateClick(button: ImageButton) {
        button.setOnClickListener {
            MenuNavigationHelper.openCreate(this)
        }
    }

    private fun bindExploreClick(button: ImageButton) {
        button.setOnClickListener {
            MenuNavigationHelper.openExplore(this, detectedCity)
        }
    }

    private fun bindAdminClick(button: ImageButton) {
        button.setOnClickListener {
            MenuNavigationHelper.openAdmin(this)
        }
    }

    private fun checkLocationPermission() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fetchCity()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun fetchCity() {
        locationHelper.getCityName { cityName ->
            runOnUiThread {
                applyDetectedCity(cityName)
            }
        }
    }

    private fun applyDetectedCity(cityName: String) {
        detectedCity = cityName
        userPrefs.saveLastCity(cityName)
        tvQuickInfo.text = MenuQuickInfoFormatter.build(cityName)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handleLocationPermissionResult(requestCode, grantResults)
    }

    private fun handleLocationPermissionResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (
            requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            fetchCity()
        }
    }

    private fun checkAdminStatus(
        uid: String,
        regularLayout: View,
        adminLayout: View
    ) {
        usersRepo.isAdmin(uid) { isAdmin ->
            runOnUiThread {
                if (isAdmin) {
                    regularLayout.visibility = View.GONE
                    adminLayout.visibility = View.VISIBLE
                } else {
                    regularLayout.visibility = View.VISIBLE
                    adminLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}