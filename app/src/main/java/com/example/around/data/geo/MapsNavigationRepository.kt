package com.example.around.data.geo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class MapsNavigationRepository(
    private val context: Context
) {

    fun navigateToSearchPlace(searchText: String, navMode: String) {
        if (searchText.isBlank()) return

        val mapUri = Uri.parse(
            "google.navigation:q=${Uri.encode(searchText)}&mode=$navMode"
        )

        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(mapIntent)
        } catch (_: ActivityNotFoundException) {
            // fallback: open search in browser / any maps handler
            val webUri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${Uri.encode(searchText)}"
            )
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun navigateRouteByNames(
        points: List<String>,
        travelModeDir: String
    ) {
        if (points.size < 2) return

        val destination = points.last()
        val waypoints = points.dropLast(1)

        val uri = Uri.parse(buildString {
            append("https://www.google.com/maps/dir/?api=1")
            append("&destination=").append(Uri.encode(destination))
            if (waypoints.isNotEmpty()) {
                append("&waypoints=").append(Uri.encode(waypoints.joinToString("|")))
            }
            append("&travelmode=").append(Uri.encode(travelModeDir))
        })

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
