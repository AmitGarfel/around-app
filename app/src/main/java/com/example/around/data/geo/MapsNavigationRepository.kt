package com.example.around.data.geo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
class MapsNavigationRepository(
    private val context: Context
) {

    fun navigateToSearchPlace(searchText: String, navMode: String) {
        if (searchText.isBlank()) return

        val mapUri =
            "google.navigation:q=${android.net.Uri.encode(searchText)}&mode=$navMode".toUri()

        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(mapIntent)
        } catch (_: ActivityNotFoundException) {
            val webUri =
                "https://www.google.com/maps/search/?api=1&query=${android.net.Uri.encode(searchText)}".toUri()

            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun navigateToLatLng(
        latitude: Double,
        longitude: Double,
        label: String = "",
        navMode: String = "d"
    ) {
        val query = if (label.isNotBlank()) {
            "$latitude,$longitude(${android.net.Uri.encode(label)})"
        } else {
            "$latitude,$longitude"
        }

        val mapUri = "google.navigation:q=$query&mode=$navMode".toUri()

        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(mapIntent)
        } catch (_: ActivityNotFoundException) {
            val webUri =
                "https://www.google.com/maps/search/?api=1&query=${android.net.Uri.encode("$latitude,$longitude")}".toUri()

            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun openTransitDirections(destination: String) {
        if (destination.isBlank()) return

        val uri = (
                "https://www.google.com/maps/dir/?api=1" +
                        "&destination=${android.net.Uri.encode(destination)}" +
                        "&travelmode=transit"
                ).toUri()

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            val fallback = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }
}