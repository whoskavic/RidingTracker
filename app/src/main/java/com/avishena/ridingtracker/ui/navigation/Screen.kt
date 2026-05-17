package com.avishena.ridingtracker.ui.navigation

sealed class Screen {
    data object VehicleSelect : Screen()
    data object RidingSession : Screen()
    data object SessionResult : Screen()
    data object History : Screen()
}
