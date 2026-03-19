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
import com.example.around.di.AppGraph
import com.example.around.ui.base.BaseActivity

class MenuActivity : BaseActivity() {

    private val usersRepo = AppGraph.usersRepo
    private val auth = AppGraph.auth

    private lateinit var tvQuickInfo: TextView
    private lateinit var locationHelper: LocationHelper

    private var detectedCity: String = "Tel Aviv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        setupBottomNav(R.id.nav_menu)

        val mainLayout = findViewById<LinearLayout>(R.id.menuMainLayout)
        val adminLayout = findViewById<LinearLayout>(R.id.layoutAdmin)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        val btnCreate = findViewById<ImageButton>(R.id.btnCreate)
        val btnSelect = findViewById<ImageButton>(R.id.btnSelect)
        val btnAdmin = findViewById<ImageButton>(R.id.btnAdmin)
        tvQuickInfo = findViewById(R.id.tvQuickInfo)

        locationHelper = LocationHelper(this)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        mainLayout.startAnimation(fadeIn)

        checkLocationPermission()

        if (!isUserLoggedIn()) {
            goToLogin()
            return
        }

        val uid = auth.currentUser?.uid ?: return
        checkAdminStatus(uid, adminLayout)

        btnCreate.setOnClickListener {
            startActivity(Intent(this, CreateTourActivity::class.java))
        }

        btnSelect.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("CITY", detectedCity)
            startActivity(intent)
        }

        btnAdmin.setOnClickListener {
            startActivity(Intent(this, AdminActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            goToLogin()
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
                100
            )
        }
    }

    private fun fetchCity() {
        locationHelper.getCityName { cityName ->
            detectedCity = cityName
            tvQuickInfo.text = "Near you: $cityName Tours"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            fetchCity()
        }
    }

    private fun checkAdminStatus(uid: String, adminLayout: View) {
        usersRepo.isAdmin(uid) { isAdmin ->
            if (isAdmin) {
                adminLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}