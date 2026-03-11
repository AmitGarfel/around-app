package com.example.around.data.mappers

import com.example.around.domain.model.Station
import com.example.around.domain.model.Tour
import com.google.firebase.firestore.DocumentSnapshot

private fun mapToStation(m: Map<*, *>): Station {
    return Station(
        name = m["name"] as? String ?: "",
        query = m["query"] as? String ?: "",
        latitude = (m["latitude"] as? Number)?.toDouble() ?: 0.0,
        longitude = (m["longitude"] as? Number)?.toDouble() ?: 0.0
    )
}

private fun parseStations(raw: Any?): List<Station> {
    return when (raw) {
        is List<*> -> raw.mapNotNull { it as? Map<*, *> }.map { mapToStation(it) }

        is Map<*, *> -> raw.toList()
            .sortedBy { (it.first as? String)?.toIntOrNull() ?: Int.MAX_VALUE }
            .mapNotNull { (_, v) -> v as? Map<*, *> }
            .map { mapToStation(it) }

        else -> emptyList()
    }
}

fun DocumentSnapshot.toTourSafe(): Tour {
    val stationsList = parseStations(get("stations"))

    return Tour(
        id = id,
        name = getString("name") ?: "",
        mood = getString("mood") ?: "",
        timeTag = getString("timeTag") ?: "",
        description = getString("description") ?: "",
        estimatedDuration = getString("estimatedDuration") ?: "",
        likesCount = (getLong("likesCount") ?: 0L).toInt(),
        status = getString("status") ?: "pending",
        imageUrl = getString("imageUrl") ?: "",
        city = getString("city") ?: "Tel Aviv",
        stations = stationsList
    )
}
