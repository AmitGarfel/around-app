package com.example.around.data.geo

import com.example.around.domain.model.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MapRenderer(
    private val map: GoogleMap
) {

    private var routePolyline: Polyline? = null

    fun renderStations(
        stations: List<Station>,
        resolvedPositions: List<LatLng?>
    ) {
        if (stations.isEmpty()) return

        map.clear()
        routePolyline = null

        val boundsBuilder = LatLngBounds.Builder()
        var addedAny = false

        resolvedPositions.forEachIndexed { index, latLng ->
            if (latLng == null) return@forEachIndexed

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("${index + 1}. ${stations[index].name}")
            )

            boundsBuilder.include(latLng)
            addedAny = true
        }

        if (!addedAny) return

        val validPoints = resolvedPositions.filterNotNull()

        if (validPoints.size == 1) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(validPoints.first(), 14f))
        } else {
            try {
                val bounds = boundsBuilder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
            } catch (_: Exception) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(validPoints.first(), 13f))
            }
        }

        if (validPoints.size >= 2) {
            routePolyline = map.addPolyline(
                PolylineOptions().addAll(validPoints)
            )
        }
    }
}
