package com.avishena.ridingtracker.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.avishena.ridingtracker.data.db.RidingDatabase
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.data.model.RidingSession
import com.avishena.ridingtracker.data.model.SessionStats
import com.avishena.ridingtracker.data.model.VehicleType
import com.avishena.ridingtracker.data.repository.RidingRepository
import com.avishena.ridingtracker.service.TrackingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RidingSessionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RidingRepository(
        RidingDatabase.getInstance(application).ridingDao()
    )

    // Mirror service state directly — no conversion needed
    val isTracking: StateFlow<Boolean> = TrackingService.isTracking
    val liveStats: StateFlow<SessionStats> = TrackingService.stats
    val routePoints: StateFlow<List<LocationPoint>> = TrackingService.routePoints

    // Result after stopAndSave() — UI navigates to ResultScreen when non-null
    private val _savedSession = MutableStateFlow<RidingSession?>(null)
    val savedSession: StateFlow<RidingSession?> = _savedSession.asStateFlow()

    val allSessions: Flow<List<RidingSession>> = repository.allSessions

    private var currentSessionId: Long = -1L
    private var selectedVehicleType: VehicleType = VehicleType.MOTOR_SPORT

    fun setVehicleType(type: VehicleType) {
        selectedVehicleType = type
    }

    fun startSession() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            currentSessionId = repository.startSession(selectedVehicleType.name, startTime)

            val intent = Intent(getApplication(), TrackingService::class.java).apply {
                action = TrackingService.ACTION_START
                putExtra(TrackingService.EXTRA_VEHICLE_TYPE, selectedVehicleType.name)
                putExtra(TrackingService.EXTRA_SESSION_ID, currentSessionId)
            }
            getApplication<Application>().startForegroundService(intent)
        }
    }

    fun stopSession() {
        val intent = Intent(getApplication(), TrackingService::class.java).apply {
            action = TrackingService.ACTION_STOP
        }
        getApplication<Application>().startService(intent)
    }

    // Stops service and persists the session + route to Room.
    // Observe savedSession to know when done.
    fun stopAndSave() {
        stopSession()
        viewModelScope.launch {
            val stats = TrackingService.stats.value
            val route = TrackingService.routePoints.value
            if (currentSessionId == -1L) return@launch

            val base = repository.getSession(currentSessionId) ?: return@launch
            val finished = base.copy(
                endTime = System.currentTimeMillis(),
                totalDistanceKm = stats.totalDistanceKm,
                topSpeedKmh = stats.topSpeedKmh,
                avgSpeedKmh = stats.avgSpeedKmh,
                durationMs = stats.durationMs,
                count100kmh = stats.count100kmh,
                maxLeanAngle = stats.maxLeanAngle
            )
            repository.finishSession(finished)
            if (route.isNotEmpty()) repository.saveLocationPoints(route)

            _savedSession.value = finished
            currentSessionId = -1L
        }
    }

    // Stops service and removes the draft session from DB (user pressed "Buang").
    fun stopAndDiscard() {
        stopSession()
        viewModelScope.launch {
            if (currentSessionId == -1L) return@launch
            val session = repository.getSession(currentSessionId)
            session?.let { repository.deleteSession(it) }
            currentSessionId = -1L
        }
    }

    // Call after navigating away from ResultScreen so the state resets
    fun clearSavedSession() {
        _savedSession.value = null
    }
}
