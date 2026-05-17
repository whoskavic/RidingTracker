package com.avishena.ridingtracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.ui.components.SpeedSample
import com.avishena.ridingtracker.ui.navigation.Screen
import com.avishena.ridingtracker.ui.screens.HistoryScreen
import com.avishena.ridingtracker.ui.screens.PermissionScreen
import com.avishena.ridingtracker.ui.screens.RidingSessionScreen
import com.avishena.ridingtracker.ui.screens.SessionResultScreen
import com.avishena.ridingtracker.ui.screens.VehicleSelectScreen
import com.avishena.ridingtracker.ui.theme.RidingTrackerTheme
import com.avishena.ridingtracker.viewmodel.RidingSessionViewModel

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

class MainActivity : ComponentActivity() {

    private val viewModel: RidingSessionViewModel by viewModels()

    // Hoisted at Activity level so onResume can update it
    private val permissionGranted   = mutableStateOf(false)
    private val isPermanentlyDenied = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionGranted.value = isLocationGranted()
        enableEdgeToEdge()
        setContent {
            RidingTrackerTheme {
                val granted         by permissionGranted
                val permanentDenied by isPermanentlyDenied

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { result ->
                    val ok = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                             result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    permissionGranted.value = ok
                    if (!ok) {
                        isPermanentlyDenied.value = !shouldShowRequestPermissionRationale(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    }
                }

                if (granted) {
                    RidingTrackerApp(viewModel = viewModel)
                } else {
                    val context = LocalContext.current
                    PermissionScreen(
                        isPermanentlyDenied = permanentDenied,
                        onRequestPermission = { permissionLauncher.launch(LOCATION_PERMISSIONS) },
                        onOpenSettings      = {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null),
                                )
                            )
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-check after returning from system permission dialog or Settings
        if (isLocationGranted()) {
            permissionGranted.value   = true
            isPermanentlyDenied.value = false
        }
    }

    private fun isLocationGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun RidingTrackerApp(viewModel: RidingSessionViewModel) {
    val liveStats    by viewModel.liveStats.collectAsState()
    val routePoints  by viewModel.routePoints.collectAsState()
    val savedSession by viewModel.savedSession.collectAsState()
    val allSessions  by viewModel.allSessions.collectAsState(initial = emptyList())

    var screen          by remember { mutableStateOf<Screen>(Screen.VehicleSelect) }
    var selectedVehicle by remember { mutableStateOf<com.avishena.ridingtracker.data.model.VehicleType?>(null) }

    var frozenRoute   by remember { mutableStateOf<List<LocationPoint>>(emptyList()) }
    var frozenSamples by remember { mutableStateOf<List<SpeedSample>>(emptyList()) }

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
            onStart       = {
                viewModel.startSession()
                screen = Screen.RidingSession
            },
            onOpenHistory = { screen = Screen.History },
        )

        Screen.RidingSession -> RidingSessionScreen(
            vehicleType = selectedVehicle
                ?: com.avishena.ridingtracker.data.model.VehicleType.MOTOR_SPORT,
            stats       = liveStats,
            routePoints = routePoints,
            onStop      = { viewModel.stopAndSave() },
        )

        Screen.SessionResult -> {
            val session = savedSession
            if (session != null) {
                SessionResultScreen(
                    session      = session,
                    routePoints  = frozenRoute,
                    speedSamples = frozenSamples,
                    onSave       = {
                        viewModel.clearSavedSession()
                        screen = Screen.History
                    },
                    onDiscard    = {
                        viewModel.deleteSavedSession()
                        screen = Screen.VehicleSelect
                    },
                )
            } else {
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
