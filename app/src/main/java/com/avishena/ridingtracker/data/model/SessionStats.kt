package com.avishena.ridingtracker.data.model

data class SessionStats(
    val currentSpeedKmh: Float = 0f,
    val topSpeedKmh: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val totalDistanceKm: Float = 0f,
    val durationMs: Long = 0L,
    val count100kmh: Int = 0,
    val currentLeanAngle: Float = 0f,
    val maxLeanAngle: Float = 0f
)
