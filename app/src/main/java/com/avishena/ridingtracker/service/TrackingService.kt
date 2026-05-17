package com.avishena.ridingtracker.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.data.model.SessionStats
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrackingService : Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "riding_tracker_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "com.avishena.ridingtracker.ACTION_START"
        const val ACTION_STOP = "com.avishena.ridingtracker.ACTION_STOP"
        const val EXTRA_VEHICLE_TYPE = "vehicleType"
        const val EXTRA_SESSION_ID = "sessionId"

        // Shared state flows — readable by ViewModel without binding
        private val _stats = MutableStateFlow(SessionStats())
        val stats: StateFlow<SessionStats> = _stats.asStateFlow()

        private val _isTracking = MutableStateFlow(false)
        val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

        private val _routePoints = MutableStateFlow<List<LocationPoint>>(emptyList())
        val routePoints: StateFlow<List<LocationPoint>> = _routePoints.asStateFlow()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sensorTracker: SensorTracker
    private lateinit var notificationManager: NotificationManager

    private var sessionId: Long = -1L
    private var startTime: Long = 0L
    private var lastLocation: Location? = null
    private var totalDistanceKm: Float = 0f
    private var topSpeedKmh: Float = 0f
    private var count100kmh: Int = 0
    private var wasAbove100: Boolean = false
    private val routeBuffer = mutableListOf<LocationPoint>()

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorTracker = SensorTracker(this)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        setupLocationCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val vehicleType = intent.getStringExtra(EXTRA_VEHICLE_TYPE) ?: "MOTOR_SPORT"
                val sid = intent.getLongExtra(EXTRA_SESSION_ID, -1L)
                val notification = buildNotification("Memulai tracking...")
                ServiceCompat.startForeground(
                    this, NOTIFICATION_ID, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                )
                startTracking(vehicleType, sid)
            }
            ACTION_STOP -> stopTracking()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    private fun startTracking(vehicleType: String, sid: Long) {
        sessionId = sid
        startTime = System.currentTimeMillis()
        totalDistanceKm = 0f
        topSpeedKmh = 0f
        count100kmh = 0
        wasAbove100 = false
        lastLocation = null
        routeBuffer.clear()

        _isTracking.value = true
        _stats.value = SessionStats()
        _routePoints.value = emptyList()

        requestLocationUpdates()
        sensorTracker.reset()
        sensorTracker.start()
        startTimer()
    }

    private fun stopTracking() {
        _isTracking.value = false
        timerJob?.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorTracker.stop()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTimer() {
        timerJob = serviceScope.launch {
            while (_isTracking.value) {
                delay(1000L)
                val elapsed = System.currentTimeMillis() - startTime
                _stats.value = _stats.value.copy(durationMs = elapsed)
                updateNotification()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
            .setMinUpdateIntervalMillis(500L)
            .setMaxUpdateDelayMillis(2000L)
            .build()
        fusedLocationClient.requestLocationUpdates(request, locationCallback, mainLooper)
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onNewLocation(it) }
            }
        }
    }

    private fun onNewLocation(location: Location) {
        val speedKmh = location.speed * 3.6f

        // Accumulate distance between consecutive points
        lastLocation?.let { prev ->
            val distanceM = prev.distanceTo(location)
            totalDistanceKm += distanceM / 1000f
        }
        lastLocation = location

        if (speedKmh > topSpeedKmh) topSpeedKmh = speedKmh

        // State machine with 5 km/h hysteresis to avoid flickering counter
        if (speedKmh >= 100f && !wasAbove100) {
            count100kmh++
            wasAbove100 = true
        } else if (speedKmh < 95f) {
            wasAbove100 = false
        }

        // avgSpeed = total distance / total elapsed time (formula dari handover)
        val elapsedHours = (System.currentTimeMillis() - startTime) / 3_600_000f
        val avgSpeed = if (elapsedHours > 0f) totalDistanceKm / elapsedHours else 0f

        routeBuffer.add(
            LocationPoint(
                sessionId = sessionId,
                latitude = location.latitude,
                longitude = location.longitude,
                speedKmh = speedKmh,
                timestamp = location.time
            )
        )

        _stats.value = _stats.value.copy(
            currentSpeedKmh = speedKmh,
            topSpeedKmh = topSpeedKmh,
            avgSpeedKmh = avgSpeed,
            totalDistanceKm = totalDistanceKm,
            count100kmh = count100kmh,
            currentLeanAngle = sensorTracker.leanAngle.value,
            maxLeanAngle = sensorTracker.maxLeanAngle.value
        )
        _routePoints.value = routeBuffer.toList()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Riding Tracker",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Tracking sesi berkendara aktif"
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(contentText: String): Notification =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("RidingTracker")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setSilent(true)
            .build()

    private fun updateNotification() {
        val s = _stats.value
        val text = "${s.currentSpeedKmh.toInt()} km/h | %.2f km | ${formatDuration(s.durationMs)}"
            .format(s.totalDistanceKm)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun formatDuration(ms: Long): String {
        val h = ms / 3_600_000L
        val m = (ms % 3_600_000L) / 60_000L
        val s = (ms % 60_000L) / 1_000L
        return "%02d:%02d:%02d".format(h, m, s)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        sensorTracker.stop()
    }
}
