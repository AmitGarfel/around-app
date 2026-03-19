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

    // ✅ חדש: נקודת התחלה של המסלול
    val startLatitude: Double = 0.0,
    val startLongitude: Double = 0.0,

    @get:Exclude
    var isLikedByMe: Boolean = false
)