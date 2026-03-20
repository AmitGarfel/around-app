package com.example.around.domain.model

import com.google.firebase.firestore.Exclude

data class Tour(
    val id: String = "",
    val name: String = "",
    val mood: String = "",
    val timeTag: String = "",
    val description: String = "",
    val estimatedDuration: String = "",
    var likesCount: Int = 0,
    val status: String = "pending",
    val imageUrl: String = "",
    val city: String = "Tel Aviv",
    val stations: List<Station> = emptyList(),
    val startLatitude: Double = 0.0,
    val startLongitude: Double = 0.0,
    val createdBy: String = "",

    @get:Exclude
    var isLikedByMe: Boolean = false
)