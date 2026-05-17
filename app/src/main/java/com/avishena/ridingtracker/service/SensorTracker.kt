package com.avishena.ridingtracker.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class SensorTracker(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    // False if device has no rotation vector sensor
    val isAvailable: Boolean get() = rotationSensor != null

    private val _leanAngle = MutableStateFlow(0f)
    val leanAngle: StateFlow<Float> = _leanAngle.asStateFlow()

    private val _maxLeanAngle = MutableStateFlow(0f)
    val maxLeanAngle: StateFlow<Float> = _maxLeanAngle.asStateFlow()

    fun start() {
        rotationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun reset() {
        _leanAngle.value = 0f
        _maxLeanAngle.value = 0f
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        val orientationAngles = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // Roll: positif = miring kiri, negatif = miring kanan
        val rollDegrees = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
        val absRoll = abs(rollDegrees)

        _leanAngle.value = rollDegrees
        if (absRoll > _maxLeanAngle.value) {
            _maxLeanAngle.value = absRoll
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
}
