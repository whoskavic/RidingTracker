package com.avishena.ridingtracker.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_points",
    foreignKeys = [ForeignKey(
        entity = RidingSession::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class LocationPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float,
    val timestamp: Long
)
