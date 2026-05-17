package com.avishena.ridingtracker.data.repository

import com.avishena.ridingtracker.data.db.RidingDao
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.data.model.RidingSession
import kotlinx.coroutines.flow.Flow

class RidingRepository(private val dao: RidingDao) {

    val allSessions: Flow<List<RidingSession>> = dao.getAllSessions()

    suspend fun startSession(vehicleType: String, startTime: Long): Long =
        dao.insertSession(RidingSession(vehicleType = vehicleType, startTime = startTime))

    suspend fun finishSession(session: RidingSession) =
        dao.updateSession(session)

    suspend fun saveLocationPoints(points: List<LocationPoint>) =
        dao.insertLocationPoints(points)

    suspend fun getSession(sessionId: Long): RidingSession? =
        dao.getSessionById(sessionId)

    suspend fun getRouteForSession(sessionId: Long): List<LocationPoint> =
        dao.getLocationPointsForSession(sessionId)

    suspend fun deleteSession(session: RidingSession) =
        dao.deleteSession(session)
}
