package com.example.around.ui.helpers

import android.app.Activity
import android.content.Intent
import com.example.around.ui.MenuActivity

object AuthNavigationHelper {

    fun openHome(activity: Activity) {
        val intent = Intent(activity, MenuActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }
}