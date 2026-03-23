package com.example.around.ui.helpers

import android.util.Log
import com.example.around.data.geo.GeocodingRepository
import com.example.around.domain.model.Station
import com.example.around.util.PlaceQueryBuilder
import com.google.android.gms.maps.model.LatLng

class StationMapResolver(
    private val geocodingRepo: GeocodingRepository
) {

    suspend fun resolveAll(
        stations: List<Station>,
        city: String
    ): List<LatLng?> {
        return stations.map { station ->
            resolveStation(station, city)
        }
    }

    private suspend fun resolveStation(
        station: Station,
        city: String
    ): LatLng? {
        if (hasSavedCoordinates(station)) {
            return LatLng(station.latitude, station.longitude)
        }

        val queryText = PlaceQueryBuilder.build(station, city)
        if (queryText.isBlank()) {
            Log.d("MAP_DEBUG", "Blank query for ${station.name}")
            return null
        }

        Log.d("MAP_DEBUG", "Geocoding query for ${station.name}: $queryText")
        return geocodingRepo.geocode(queryText)
    }

    private fun hasSavedCoordinates(station: Station): Boolean {
        return station.latitude != 0.0 && station.longitude != 0.0
    }
}