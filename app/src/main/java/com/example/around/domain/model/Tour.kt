package com.example.around.domain.model

import com.example.around.domain.model.Station
import com.google.firebase.firestore.Exclude

data class Tour(
    val id: String = "",
    val name: String = "",
    val mood: String = "",          // culinary, culture, relax, surprise
    val timeTag: String = "",       // Morning, Afternoon, Evening
    val description: String = "",
    val estimatedDuration: String = "",
    var likesCount: Int = 0,        // שיניתי ל-var כדי שנוכל לעדכן מקומית ב-Adapter
    val status: String = "pending", // pending / approved / rejected
    val imageUrl: String = "",
    val city: String = "Tel Aviv",
    val stations: List<Station> = emptyList(),

    // --- השדה החדש לפתרון הדיליי ---
    @get:Exclude // אומר ל-Firebase לא לנסות לשמור/לקרוא את השדה הזה מה-DB
    var isLikedByMe: Boolean = false
)