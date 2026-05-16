package com.example.around.ui.helpers

import com.example.around.domain.model.Station
import com.google.android.gms.maps.model.LatLng

class StationSelectionManager {

    private data class SelectedStationData(
        val name: String,
        val query: String,
        val latLng: LatLng
    )

    private val selectedStations = mutableMapOf<Int, SelectedStationData>()

    fun setStation(
        index: Int,
        name: String,
        query: String,
        latLng: LatLng
    ) {
        selectedStations[index] = SelectedStationData(name, query, latLng)
    }

    fun hasStationAt(index: Int): Boolean =
        selectedStations.containsKey(index)

    fun buildStationsList(): List<Station> =
        selectedStations
            .toSortedMap()
            .values
            .map {
                Station(
                    name = it.name,
                    query = it.query,
                    latitude = it.latLng.latitude,
                    longitude = it.latLng.longitude
                )
            }
}