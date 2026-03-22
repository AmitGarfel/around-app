package com.example.around.util

import com.example.around.domain.model.Station

object PlaceQueryBuilder {

    fun build(station: Station, city: String?): String {
        val base = station.name.trim().ifBlank { station.query.trim() }
        return build(base, city)
    }

    fun build(baseText: String?, city: String?): String {
        val base = baseText?.trim().orEmpty()
        val normalizedCity = CityNormalizer.canonical(city)

        if (base.isBlank()) return ""

        return when {
            normalizedCity.isBlank() -> "$base, Israel"
            base.contains(normalizedCity, ignoreCase = true) -> "$base, Israel"
            else -> "$base, $normalizedCity, Israel"
        }
    }

    fun buildAll(stations: List<Station>, city: String?): List<String> {
        return stations.mapNotNull { station ->
            val text = build(station, city)
            if (text.isBlank()) null else text
        }
    }
}