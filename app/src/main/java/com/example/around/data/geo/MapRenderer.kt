package com.example.around.data.geo

import android.util.Log
import com.example.around.domain.model.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MapRenderer(private val map: GoogleMap) {

    private var routePolyline: Polyline? = null

    fun renderStations(stations: List<Station>, resolvedPositions: List<LatLng?>) {
        if (stations.isEmpty()) return

        map.clear()
        routePolyline = null

        val boundsBuilder = LatLngBounds.Builder()
        var addedAny = false

        resolvedPositions.forEachIndexed { index, latLng ->
            if (latLng == null || (latLng.latitude == 0.0 && latLng.longitude == 0.0)) {
                return@forEachIndexed
            }

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("${index + 1}. ${stations[index].name}")
            )

            boundsBuilder.include(latLng)
            addedAny = true
        }

        if (!addedAny) return

        val validPoints = resolvedPositions.filterNotNull().filter { it.latitude != 0.0 }

        if (validPoints.size == 1) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(validPoints.first(), 15f))
        } else if (validPoints.size >= 2) {
            try {
                val bounds = boundsBuilder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))

                routePolyline = map.addPolyline(
                    PolylineOptions().addAll(validPoints).width(10f).color(android.graphics.Color.BLUE)
                )
            } catch (e: Exception) {
                Log.e("MAP", "Error: ${e.message}")
            }
        }
    }
}