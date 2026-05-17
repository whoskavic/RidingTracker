package com.avishena.ridingtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.data.model.SessionStats
import com.avishena.ridingtracker.data.model.VehicleType
import com.avishena.ridingtracker.ui.components.HoldButton
import com.avishena.ridingtracker.ui.components.LeanAngleBar
import com.avishena.ridingtracker.ui.components.MapInset
import com.avishena.ridingtracker.ui.components.Speedometer
import com.avishena.ridingtracker.ui.components.StatCellInline
import com.avishena.ridingtracker.ui.components.StatusChip
import com.avishena.ridingtracker.ui.components.formatDuration
import com.avishena.ridingtracker.ui.components.toNormalized
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.BgDark
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.SurfaceDark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun RidingSessionScreen(
    vehicleType: VehicleType,
    stats: SessionStats,
    routePoints: List<LocationPoint>,
    onStop: () -> Unit,
) {
    val hasLean      = vehicleType == VehicleType.MOTOR_SPORT
    val normalizedPts = routePoints.toNormalized()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .drawBehind {
                // Subtle grid backdrop
                val gridStep = 24.dp.toPx()
                val gridColor = BorderDark.copy(alpha = 0.25f)
                var x = 0f
                while (x <= size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), 1f)
                    x += gridStep
                }
                var y = 0f
                while (y <= size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), 1f)
                    y += gridStep
                }
            },
    ) {
        Column(
            modifier          = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar — vehicle mode + REC timer
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🏍", fontSize = 18.sp)
                }

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text          = "MODE",
                        fontSize      = 9.sp,
                        fontFamily    = FontFamily.Monospace,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.8.sp,
                        color         = TextDim,
                    )
                    Text(
                        text          = when (vehicleType) {
                            VehicleType.MOTOR_SPORT -> "Motor Sport"
                            VehicleType.MOTOR_MATIC -> "Motor Matic"
                            VehicleType.MOBIL       -> "Mobil"
                            else                    -> "Sepeda"
                        },
                        fontSize      = 12.sp,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color         = TextPrimary,
                    )
                }

                Spacer(Modifier.weight(1f))

                StatusChip(
                    label = "REC ${formatDuration(stats.durationMs)}",
                    color = Accent,
                    pulse = true,
                )
            }

            // Mini map row: map inset (left) + 100km/h counter (right)
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                MapInset(points = normalizedPts, size = 100.dp)

                // 100 km/h counter block
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text          = "≥ 100 KM/H",
                        fontSize      = 9.sp,
                        fontFamily    = FontFamily.Monospace,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.6.sp,
                        color         = TextDim,
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text          = "%02d".format(stats.count100kmh),
                            fontSize      = 32.sp,
                            fontFamily    = FontFamily.Monospace,
                            fontWeight    = FontWeight.Bold,
                            color         = if (stats.count100kmh > 0) Accent else TextPrimary,
                        )
                        Spacer(Modifier.padding(start = 3.dp))
                        Text(
                            text          = "×",
                            fontSize      = 10.sp,
                            fontFamily    = FontFamily.Monospace,
                            color         = TextFaint,
                            modifier      = Modifier.padding(bottom = 5.dp),
                        )
                    }
                }
            }

            // Speedometer — centered, takes remaining space
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Speedometer(
                    speed = stats.currentSpeedKmh,
                    size  = 260.dp,
                )
            }

            // Lean angle bar (sport only)
            if (hasLean) {
                LeanAngleBar(
                    angle    = stats.currentLeanAngle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }

            // Stats row: TOP | AVG | DIST
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .border(
                        width = 1.dp,
                        color = BorderDark,
                        shape = RoundedCornerShape(0.dp),
                    )
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatCellInline(
                    label    = "TOP",
                    value    = stats.topSpeedKmh.roundToInt().toString(),
                    unit     = "KM/H",
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(BorderDark))
                StatCellInline(
                    label    = "AVG",
                    value    = stats.avgSpeedKmh.roundToInt().toString(),
                    unit     = "KM/H",
                    modifier = Modifier.weight(1f),
                )
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(BorderDark))
                StatCellInline(
                    label    = "DIST",
                    value    = "%.1f".format(stats.totalDistanceKm),
                    unit     = "KM",
                    modifier = Modifier.weight(1f),
                )
            }

            // Hold-to-stop button
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                HoldButton(
                    label         = "HOLD TO STOP",
                    holdDurationMs = 900L,
                    size          = 82.dp,
                    onComplete    = onStop,
                )
            }
        }
    }
}
