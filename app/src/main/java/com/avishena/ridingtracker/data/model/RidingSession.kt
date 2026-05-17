package com.avishena.ridingtracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riding_sessions")
data class RidingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleType: String,
    val startTime: Long,
    val endTime: Long = 0L,
    val totalDistanceKm: Float = 0f,
    val topSpeedKmh: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val durationMs: Long = 0L,
    val count100kmh: Int = 0,
    val maxLeanAngle: Float = 0f
)
