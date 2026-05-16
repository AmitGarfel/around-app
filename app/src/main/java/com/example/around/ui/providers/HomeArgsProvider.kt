package com.example.around.ui.providers

import android.content.Intent
import com.example.around.data.preferences.UserPrefsProvider
import com.example.around.util.CityNormalizer
import com.example.around.util.NavigationKeys

object HomeArgsProvider {

    fun resolveCity(
        intent: Intent?,
        userPrefs: UserPrefsProvider
    ): String =
        CityNormalizer.canonical(
            intent?.getStringExtra(NavigationKeys.EXTRA_CITY)
                ?: userPrefs.getLastCity()
        )
}