package com.example.around.domain.model

data class TourStationsData(
    val tourName: String,
    val city: String,
    val stations: List<Station>
)