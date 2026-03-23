package com.example.around.ui.helpers

import com.example.around.data.geo.MapsNavigationRepository
import com.example.around.domain.model.Station
import com.example.around.ui.models.TravelModeUi
import com.example.around.util.PlaceQueryBuilder

class StationNavigator(
    private val navRepo: MapsNavigationRepository
) {

    fun navigate(
        station: Station,
        city: String,
        travelMode: TravelModeUi
    ): Boolean {

        val searchText = PlaceQueryBuilder.build(station, city)

        // Transit
        if (travelMode.dirMode == "transit") {
            if (searchText.isBlank()) return false
            navRepo.openTransitDirections(searchText)
            return true
        }

        // Search
        if (searchText.isNotBlank()) {
            navRepo.navigateToSearchPlace(searchText, travelMode.navMode)
            return true
        }

        // LatLng fallback
        if (hasSavedCoordinates(station)) {
            navRepo.navigateToLatLng(
                latitude = station.latitude,
                longitude = station.longitude,
                label = station.name,
                navMode = travelMode.navMode
            )
            return true
        }

        return false
    }

    private fun hasSavedCoordinates(station: Station): Boolean {
        return station.latitude != 0.0 && station.longitude != 0.0
    }
}