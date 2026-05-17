package com.avishena.ridingtracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.data.model.RidingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface RidingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: RidingSession): Long

    @Update
    suspend fun updateSession(session: RidingSession)

    @Delete
    suspend fun deleteSession(session: RidingSession)

    @Query("SELECT * FROM riding_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<RidingSession>>

    @Query("SELECT * FROM riding_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): RidingSession?

    @Insert
    suspend fun insertLocationPoints(points: List<LocationPoint>)

    @Query("SELECT * FROM location_points WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getLocationPointsForSession(sessionId: Long): List<LocationPoint>
}
