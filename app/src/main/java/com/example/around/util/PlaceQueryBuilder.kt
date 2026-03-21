package com.example.around.util

import com.example.around.domain.model.Station

object PlaceQueryBuilder {

    fun build(station: Station, city: String?): String {
        val base = station.query.trim().ifBlank { station.name.trim() }
        return build(base, city)
    }

    fun build(baseText: String?, city: String?): String {
        val base = baseText?.trim().orEmpty()
        val normalizedCity = CityNormalizer.canonical(city)

        if (base.isBlank()) return ""

        return if (
            normalizedCity.isNotBlank() &&
            !base.contains(normalizedCity, ignoreCase = true)
        ) {
            "$base, $normalizedCity"
        } else {
            base
        }
    }

    fun buildAll(stations: List<Station>, city: String?): List<String> {
        return stations.mapNotNull { station ->
            val text = build(station, city)
            if (text.isBlank()) null else text
        }
    }
}