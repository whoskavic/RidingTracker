package com.avishena.ridingtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.data.model.VehicleType
import com.avishena.ridingtracker.ui.components.StatusChip
import com.avishena.ridingtracker.ui.components.VehicleCard
import com.avishena.ridingtracker.ui.components.vehicleOptions
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

@Composable
fun VehicleSelectScreen(
    selectedVehicle: VehicleType?,
    sessionCount: Int,
    onSelectVehicle: (VehicleType) -> Unit,
    onStart: () -> Unit,
    onOpenHistory: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding(),
    ) {
        // Top bar
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // App logo + name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Accent),
                ) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(1.dp))
                            .background(BgDark),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text          = "RIDING TRACKER",
                    fontSize      = 10.sp,
                    fontFamily    = FontFamily.Monospace,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.2.sp,
                    color         = TextPrimary,
                )
            }

            Spacer(Modifier.weight(1f))

            StatusChip(label = "GPS READY", color = GoodGreen, pulse = true)
            Spacer(Modifier.width(10.dp))

            // History button
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderHiDark, RoundedCornerShape(8.dp))
                    .clickable(onClick = onOpenHistory)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text          = "LOG",
                        fontSize      = 10.sp,
                        fontFamily    = FontFamily.Monospace,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color         = TextPrimary,
                    )
                    if (sessionCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(Accent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text       = sessionCount.coerceAtMost(99).toString(),
                                fontSize   = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = Color.Black,
                            )
                        }
                    }
                }
            }
        }

        // Title section
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)) {
            Text(
                text          = "// SESSION SETUP",
                fontSize      = 10.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 2.2.sp,
                color         = Accent,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text       = "Pilih\nKendaraan",
                fontSize   = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 34.sp,
                color      = TextPrimary,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text      = "Konfigurasi mode telemetri sesuai kendaraan yang akan dipakai.",
                fontSize  = 13.sp,
                color     = TextDim,
                lineHeight = 19.sp,
            )
        }

        Spacer(Modifier.height(20.dp))

        // Vehicle list
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            vehicleOptions.forEach { option ->
                VehicleCard(
                    option   = option,
                    selected = selectedVehicle == option.type,
                    onClick  = { onSelectVehicle(option.type) },
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        // CTA
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Button(
                onClick  = onStart,
                enabled  = selectedVehicle != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor         = Accent,
                    contentColor           = Color.Black,
                    disabledContainerColor = BorderHiDark,
                    disabledContentColor   = TextFaint,
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (selectedVehicle != null) 8.dp else 0.dp,
                ),
            ) {
                Text(
                    text          = "▸  Mulai Tracking",
                    fontSize      = 14.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text          = "Tracking berjalan di background · Foreground service",
                fontSize      = 10.sp,
                fontFamily    = FontFamily.Monospace,
                color         = TextFaint,
                letterSpacing = 0.6.sp,
                modifier      = Modifier.fillMaxWidth(),
                textAlign     = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}
