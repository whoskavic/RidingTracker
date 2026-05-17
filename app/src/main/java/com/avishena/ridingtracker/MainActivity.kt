package com.avishena.ridingtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.ui.components.SpeedSample
import com.avishena.ridingtracker.ui.navigation.Screen
import com.avishena.ridingtracker.ui.screens.HistoryScreen
import com.avishena.ridingtracker.ui.screens.RidingSessionScreen
import com.avishena.ridingtracker.ui.screens.SessionResultScreen
import com.avishena.ridingtracker.ui.screens.VehicleSelectScreen
import com.avishena.ridingtracker.ui.theme.RidingTrackerTheme
import com.avishena.ridingtracker.viewmodel.RidingSessionViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RidingSessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RidingTrackerTheme {
                RidingTrackerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RidingTrackerApp(viewModel: RidingSessionViewModel) {
    val liveStats     by viewModel.liveStats.collectAsState()
    val routePoints   by viewModel.routePoints.collectAsState()
    val savedSession  by viewModel.savedSession.collectAsState()
    val allSessions   by viewModel.allSessions.collectAsState(initial = emptyList())

    var screen          by remember { mutableStateOf<Screen>(Screen.VehicleSelect) }
    var selectedVehicle by remember { mutableStateOf<com.avishena.ridingtracker.data.model.VehicleType?>(null) }

    // Snapshot of route points captured when session stops (for result/chart)
    var frozenRoute   by remember { mutableStateOf<List<LocationPoint>>(emptyList()) }
    var frozenSamples by remember { mutableStateOf<List<SpeedSample>>(emptyList()) }

    // Navigate to result screen automatically when session is saved
    LaunchedEffect(savedSession) {
        if (savedSession != null) {
            frozenRoute   = routePoints
            frozenSamples = routePoints.mapIndexed { i, pt ->
                SpeedSample(timeSec = (pt.timestamp / 1000f), speedKmh = pt.speedKmh)
            }
            screen = Screen.SessionResult
        }
    }

    when (screen) {
        Screen.VehicleSelect -> VehicleSelectScreen(
            selectedVehicle = selectedVehicle,
            sessionCount    = allSessions.size,
            onSelectVehicle = {
                selectedVehicle = it
                viewModel.setVehicleType(it)
            },
            onStart         = {
                viewModel.startSession()
                screen = Screen.RidingSession
            },
            onOpenHistory   = { screen = Screen.History },
        )

        Screen.RidingSession -> RidingSessionScreen(
            vehicleType = selectedVehicle
                ?: com.avishena.ridingtracker.data.model.VehicleType.MOTOR_SPORT,
            stats       = liveStats,
            routePoints = routePoints,
            onStop      = {
                viewModel.stopAndSave()
                // Result navigation happens via LaunchedEffect(savedSession)
            },
        )

        Screen.SessionResult -> {
            val session = savedSession
            if (session != null) {
                SessionResultScreen(
                    session       = session,
                    routePoints   = frozenRoute,
                    speedSamples  = frozenSamples,
                    onSave        = {
                        viewModel.clearSavedSession()
                        screen = Screen.History
                    },
                    onDiscard     = {
                        viewModel.deleteSavedSession()
                        screen = Screen.VehicleSelect
                    },
                )
            } else {
                // Fallback if session state cleared before UI renders
                screen = Screen.VehicleSelect
            }
        }

        Screen.History -> HistoryScreen(
            sessions        = allSessions,
            onBack          = { screen = Screen.VehicleSelect },
            onNewSession    = { screen = Screen.VehicleSelect },
            onDeleteSession = { id -> viewModel.deleteSessionById(id) },
        )
    }
}
