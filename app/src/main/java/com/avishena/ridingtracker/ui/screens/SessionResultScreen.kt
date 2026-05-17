package com.avishena.ridingtracker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.data.model.RidingSession
import com.avishena.ridingtracker.data.model.SessionStats
import com.avishena.ridingtracker.data.model.VehicleType
import com.avishena.ridingtracker.ui.components.ReplayMap
import com.avishena.ridingtracker.ui.components.ResultCell
import com.avishena.ridingtracker.ui.components.SpeedSample
import com.avishena.ridingtracker.ui.components.SpeedChartView
import com.avishena.ridingtracker.ui.components.StatusChip
import com.avishena.ridingtracker.ui.components.formatDuration
import com.avishena.ridingtracker.ui.components.toNormalized
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.AccentGlow
import com.avishena.ridingtracker.ui.theme.BgDark
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.BorderHiDark
import com.avishena.ridingtracker.ui.theme.GoodGreen
import com.avishena.ridingtracker.ui.theme.SurfaceDark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SessionResultScreen(
    session: RidingSession,
    routePoints: List<LocationPoint>,
    speedSamples: List<SpeedSample>,
    onSave: () -> Unit,
    onDiscard: () -> Unit,
) {
    val dateFmt  = SimpleDateFormat("EEE, d MMM", Locale("id"))
    val timeFmt  = SimpleDateFormat("HH:mm", Locale("id"))
    val date     = Date(session.startTime)
    val dateStr  = dateFmt.format(date).uppercase()
    val timeStr  = timeFmt.format(date)
    val vehicle  = try {
        VehicleType.valueOf(session.vehicleType)
    } catch (_: Exception) {
        VehicleType.OTHER
    }
    val normalizedPts = routePoints.toNormalized()

    BackHandler { onSave() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusChip(label = "SESSION ENDED", color = GoodGreen)
                Text(
                    text          = "#${session.id}",
                    fontSize      = 10.sp,
                    fontFamily    = FontFamily.Monospace,
                    color         = TextFaint,
                    letterSpacing = 1.2.sp,
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text     = when (vehicle) {
                            VehicleType.MOTOR_SPORT -> "🏍"
                            VehicleType.MOTOR_MATIC -> "🛵"
                            VehicleType.MOBIL       -> "🚗"
                            else                    -> "🚲"
                        },
                        fontSize = 24.sp,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text          = when (vehicle) {
                            VehicleType.MOTOR_SPORT -> "MOTOR SPORT"
                            VehicleType.MOTOR_MATIC -> "MOTOR MATIC"
                            VehicleType.MOBIL       -> "MOBIL"
                            else                    -> "SEPEDA"
                        },
                        fontSize      = 22.sp,
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = (-0.1).sp,
                        color         = TextPrimary,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text          = "$dateStr · $timeStr",
                        fontSize      = 11.sp,
                        fontFamily    = FontFamily.Monospace,
                        color         = TextDim,
                        letterSpacing = 0.8.sp,
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderDark))

        // Hero: total distance
        Column(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text          = "TOTAL JARAK",
                fontSize      = 10.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 2.2.sp,
                color         = TextDim,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text          = "%.1f".format(session.totalDistanceKm),
                fontSize      = 64.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = Accent,
            )
            Text(
                text          = "KILOMETER",
                fontSize      = 12.sp,
                fontFamily    = FontFamily.Monospace,
                color         = TextFaint,
                letterSpacing = 2.sp,
            )
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderDark))

        // Stats 2×2 grid
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BorderDark),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                ResultCell(
                    label    = "DURASI",
                    value    = formatDuration(session.durationMs),
                    modifier = Modifier.weight(1f),
                )
                ResultCell(
                    label    = "TOP SPEED",
                    value    = session.topSpeedKmh.roundToInt().toString(),
                    unit     = "KM/H",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(1.dp).background(BorderDark))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BorderDark),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                ResultCell(
                    label    = "AVG SPEED",
                    value    = session.avgSpeedKmh.roundToInt().toString(),
                    unit     = "KM/H",
                    modifier = Modifier.weight(1f),
                )
                ResultCell(
                    label    = "≥ 100 KM/H",
                    value    = session.count100kmh.toString(),
                    unit     = "×",
                    accent   = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderDark))

        // Speed chart
        Column(modifier = Modifier.padding(all = 20.dp)) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text          = "KECEPATAN · TIME",
                    fontSize      = 10.sp,
                    fontFamily    = FontFamily.Monospace,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.2.sp,
                    color         = TextDim,
                )
                Text(
                    text          = "${speedSamples.size} pts",
                    fontSize      = 10.sp,
                    fontFamily    = FontFamily.Monospace,
                    color         = TextFaint,
                    letterSpacing = 0.8.sp,
                )
            }
            Spacer(Modifier.height(10.dp))
            SpeedChartView(samples = speedSamples)
        }

        // Route replay map
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp)) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text          = "ROUTE REPLAY",
                    fontSize      = 10.sp,
                    fontFamily    = FontFamily.Monospace,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.2.sp,
                    color         = TextDim,
                )
                Text(
                    text          = "${routePoints.size} pts · GPS",
                    fontSize      = 10.sp,
                    fontFamily    = FontFamily.Monospace,
                    color         = TextFaint,
                    letterSpacing = 0.8.sp,
                )
            }
            Spacer(Modifier.height(10.dp))
            ReplayMap(points = normalizedPts)
        }

        // Action buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick  = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor   = Color.Black,
                ),
            ) {
                Text(
                    text          = "▸  Simpan Sesi",
                    fontSize      = 14.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                )
            }

            OutlinedButton(
                onClick  = onDiscard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = Accent,
                ),
                border   = androidx.compose.foundation.BorderStroke(1.5.dp, Accent),
            ) {
                Text(
                    text          = "Buang Sesi",
                    fontSize      = 14.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                )
            }
        }
    }
}
