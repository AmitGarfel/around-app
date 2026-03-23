package com.example.around.ui.helpers

import android.app.Activity
import android.content.Intent
import com.example.around.ui.AdminActivity
import com.example.around.ui.CreateTourActivity
import com.example.around.ui.HomeActivity
import com.example.around.util.NavigationKeys

object MenuNavigationHelper {

    fun openCreate(activity: Activity) {
        activity.startActivity(Intent(activity, CreateTourActivity::class.java))
    }

    fun openExplore(activity: Activity, city: String) {
        val intent = Intent(activity, HomeActivity::class.java)
        intent.putExtra(NavigationKeys.EXTRA_CITY, city)
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }

    fun openAdmin(activity: Activity) {
        activity.startActivity(Intent(activity, AdminActivity::class.java))
    }
}