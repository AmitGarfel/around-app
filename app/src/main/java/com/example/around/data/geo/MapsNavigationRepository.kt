package com.example.around.data.geo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.maps.model.LatLng

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
            val webUri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${Uri.encode(searchText)}"
            )
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
            "$latitude,$longitude(${Uri.encode(label)})"
        } else {
            "$latitude,$longitude"
        }

        val mapUri = Uri.parse("google.navigation:q=$query&mode=$navMode")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri).apply {
            setPackage("com.google.android.apps.maps")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(mapIntent)
        } catch (_: ActivityNotFoundException) {
            val webUri = Uri.parse(
                "https://www.google.com/maps/search/?api=1&query=${Uri.encode("$latitude,$longitude")}"
            )
            val webIntent = Intent(Intent.ACTION_VIEW, webUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun openTransitDirections(destination: String) {
        if (destination.isBlank()) return

        val uri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                    "&destination=${Uri.encode(destination)}" +
                    "&travelmode=transit"
        )

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

    fun previewRouteByNames(
        points: List<String>,
        travelModeDir: String
    ) {
        if (points.size < 2) return

        val origin = points.first()
        val destination = points.last()
        val waypoints = points.drop(1).dropLast(1)

        val uri = Uri.parse(buildString {
            append("https://www.google.com/maps/dir/?api=1")
            append("&origin=").append(Uri.encode(origin))
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

    fun previewRouteByLatLng(
        points: List<LatLng>,
        travelModeDir: String
    ) {
        if (points.size < 2) return

        val origin = "${points.first().latitude},${points.first().longitude}"
        val destination = "${points.last().latitude},${points.last().longitude}"
        val waypoints = points.drop(1).dropLast(1)
            .joinToString("|") { "${it.latitude},${it.longitude}" }

        val uri = Uri.parse(buildString {
            append("https://www.google.com/maps/dir/?api=1")
            append("&origin=").append(Uri.encode(origin))
            append("&destination=").append(Uri.encode(destination))
            if (waypoints.isNotBlank()) {
                append("&waypoints=").append(Uri.encode(waypoints))
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