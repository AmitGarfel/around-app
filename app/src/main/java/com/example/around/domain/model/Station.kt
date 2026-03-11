package com.example.around.domain.model

data class Station(
    val name: String = "",
    val query: String = "",        // חדש: שם מקום/כתובת לחיפוש במפות
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)