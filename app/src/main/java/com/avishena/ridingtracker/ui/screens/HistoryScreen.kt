package com.avishena.ridingtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.data.model.RidingSession
import com.avishena.ridingtracker.data.model.VehicleType
import com.avishena.ridingtracker.ui.components.AggCell
import com.avishena.ridingtracker.ui.components.ReplayMap
import com.avishena.ridingtracker.ui.components.SpeedChartView
import com.avishena.ridingtracker.ui.components.SpeedSample
import com.avishena.ridingtracker.ui.components.StatusChip
import com.avishena.ridingtracker.ui.components.formatDuration
import com.avishena.ridingtracker.ui.components.formatDurationShort
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.AccentGlow
import com.avishena.ridingtracker.ui.theme.BgDark
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.BorderHiDark
import com.avishena.ridingtracker.ui.theme.GoodGreen
import com.avishena.ridingtracker.ui.theme.SurfaceDark
import com.avishena.ridingtracker.ui.theme.Surface2Dark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private enum class SortMode { DATE, DISTANCE, TOP_SPEED }

@Composable
fun HistoryScreen(
    sessions: List<RidingSession>,
    onBack: () -> Unit,
    onNewSession: () -> Unit,
    onDeleteSession: (Long) -> Unit,
) {
    var filterVehicle  by remember { mutableStateOf<VehicleType?>(null) }
    var sortMode       by remember { mutableStateOf(SortMode.DATE) }
    var selectedSession by remember { mutableStateOf<RidingSession?>(null) }

    val filtered = remember(sessions, filterVehicle, sortMode) {
        var list = if (filterVehicle == null) sessions
                   else sessions.filter { it.vehicleType == filterVehicle?.name }
        list = when (sortMode) {
            SortMode.DATE      -> list.sortedByDescending { it.startTime }
            SortMode.DISTANCE  -> list.sortedByDescending { it.totalDistanceKm }
            SortMode.TOP_SPEED -> list.sortedByDescending { it.topSpeedKmh }
        }
        list
    }

    val totalDist = filtered.sumOf { it.totalDistanceKm.toDouble() }.toFloat()
    val totalTime = filtered.sumOf { it.durationMs }
    val bestSpeed = filtered.maxOfOrNull { it.topSpeedKmh } ?: 0f

    val filterOpts: List<Pair<String, VehicleType?>> = listOf(
        "ALL" to null,
        "SPORT" to VehicleType.MOTOR_SPORT,
        "MATIC" to VehicleType.MOTOR_MATIC,
        "MOBIL" to VehicleType.MOBIL,
        "SEPEDA" to VehicleType.OTHER,
    )

    if (selectedSession != null) {
        SessionDetailOverlay(
            session   = selectedSession!!,
            onBack    = { selectedSession = null },
            onDelete  = { id ->
                onDeleteSession(id)
                selectedSession = null
            },
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
    ) {
        // Top bar
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .border(width = 0.dp, color = Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderHiDark, RoundedCornerShape(8.dp))
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Text("←", color = TextPrimary, fontSize = 16.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text          = "// LOG",
                    fontSize      = 9.sp,
                    fontFamily    = FontFamily.Monospace,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 2.2.sp,
                    color         = Accent,
                )
                Text(
                    text          = "Riding History",
                    fontSize      = 18.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 0.4.sp,
                    color         = TextPrimary,
                )
            }

            // New session button
            Button(
                onClick  = onNewSession,
                modifier = Modifier.height(36.dp),
                shape    = RoundedCornerShape(8.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor   = Color.Black,
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
            ) {
                Text(
                    text          = "+ New",
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                )
            }
        }

        // Aggregate strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BorderDark),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            AggCell("SESI", filtered.size.toString(), modifier = Modifier.weight(1f))
            AggCell("JARAK", "%.0f".format(totalDist), unit = "KM", modifier = Modifier.weight(1f))
            AggCell("TIME", formatDurationShort(totalTime), modifier = Modifier.weight(1f))
            AggCell("BEST", bestSpeed.roundToInt().toString(), unit = "KM/H", accent = true, modifier = Modifier.weight(1f))
        }

        // Filter chips
        LazyRow(
            contentPadding    = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            items(filterOpts) { (label, type) ->
                FilterChip(
                    label    = label,
                    active   = filterVehicle == type,
                    onClick  = { filterVehicle = type },
                )
            }
        }

        // Sort buttons + count
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text          = "${filtered.size} SESI",
                fontSize      = 10.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.8.sp,
                color         = TextDim,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SortButton("NEWEST",   active = sortMode == SortMode.DATE)      { sortMode = SortMode.DATE }
                SortButton("DISTANCE", active = sortMode == SortMode.DISTANCE)  { sortMode = SortMode.DISTANCE }
                SortButton("TOP SPD",  active = sortMode == SortMode.TOP_SPEED) { sortMode = SortMode.TOP_SPEED }
            }
        }

        // Session list
        if (filtered.isEmpty()) {
            Box(
                modifier         = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .border(1.5.dp, BorderHiDark, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("📋", fontSize = 28.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text          = "NO SESSIONS",
                        fontSize      = 11.sp,
                        fontFamily    = FontFamily.Monospace,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.4.sp,
                        color         = TextDim,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text     = "Belum ada sesi tersimpan.",
                        fontSize = 12.sp,
                        color    = TextFaint,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier           = Modifier.weight(1f),
                contentPadding     = PaddingValues(horizontal = 14.dp, top = 4.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filtered, key = { it.id }) { session ->
                    SessionCard(session = session, onClick = { selectedSession = session })
                }
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (active) Accent else Color.Transparent)
            .border(1.dp, if (active) Accent else BorderHiDark, RoundedCornerShape(100.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text          = label,
            fontSize      = 10.sp,
            fontWeight    = FontWeight.ExtraBold,
            letterSpacing = 1.4.sp,
            color         = if (active) Color.Black else TextDim,
        )
    }
}

@Composable
private fun SortButton(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .border(
                width = if (active) 0.dp else 0.dp,
                color = if (active) Accent else Color.Transparent,
                shape = RoundedCornerShape(0.dp),
            ),
    ) {
        Text(
            text          = label,
            fontSize      = 9.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 1.4.sp,
            color         = if (active) Accent else TextFaint,
        )
    }
}

@Composable
private fun SessionCard(session: RidingSession, onClick: () -> Unit) {
    val dateFmt = SimpleDateFormat("dd MMM", Locale("id"))
    val timeFmt = SimpleDateFormat("HH:mm", Locale("id"))
    val date    = Date(session.startTime)
    val vehicle = try { VehicleType.valueOf(session.vehicleType) } catch (_: Exception) { VehicleType.OTHER }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top row: icon + label + date
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BgDark)
                        .border(1.dp, BorderHiDark, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text     = when (vehicle) {
                            VehicleType.MOTOR_SPORT -> "🏍"
                            VehicleType.MOTOR_MATIC -> "🛵"
                            VehicleType.MOBIL       -> "🚗"
                            else                    -> "🚲"
                        },
                        fontSize = 22.sp,
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text          = when (vehicle) {
                            VehicleType.MOTOR_SPORT -> "Motor Sport"
                            VehicleType.MOTOR_MATIC -> "Motor Matic"
                            VehicleType.MOBIL       -> "Mobil"
                            else                    -> "Sepeda"
                        },
                        fontSize      = 14.sp,
                        fontWeight    = FontWeight.Bold,
                        color         = TextPrimary,
                        letterSpacing = 0.2.sp,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text          = "${dateFmt.format(date).uppercase()} · ${timeFmt.format(date)} · #${session.id}",
                        fontSize      = 10.sp,
                        fontFamily    = FontFamily.Monospace,
                        color         = TextDim,
                        letterSpacing = 1.sp,
                    )
                }

                if (session.count100kmh > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(3.dp))
                            .background(Surface2Dark)
                            .border(1.dp, Accent, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text          = "${session.count100kmh}× 100+",
                            fontSize      = 9.sp,
                            fontFamily    = FontFamily.Monospace,
                            fontWeight    = FontWeight.Bold,
                            color         = Accent,
                            letterSpacing = 1.4.sp,
                        )
                    }
                }
            }

            // Divider
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderDark))

            // Bottom stats
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MiniStat("DIST",  "%.1f".format(session.totalDistanceKm), "KM")
                MiniStat("TOP",   session.topSpeedKmh.roundToInt().toString(), "KM/H",
                    accent = session.topSpeedKmh >= 100f)
                MiniStat("TIME",  formatDuration(session.durationMs), "")
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, unit: String, accent: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text          = label,
            fontSize      = 8.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 2.sp,
            color         = TextFaint,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text          = value,
                fontSize      = 16.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = if (accent) Accent else TextPrimary,
            )
            if (unit.isNotEmpty()) {
                Spacer(Modifier.padding(start = 3.dp))
                Text(
                    text          = unit,
                    fontSize      = 8.sp,
                    fontFamily    = FontFamily.Monospace,
                    fontWeight    = FontWeight.Bold,
                    color         = TextFaint,
                    modifier      = Modifier.padding(bottom = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun SessionDetailOverlay(
    session: RidingSession,
    onBack: () -> Unit,
    onDelete: (Long) -> Unit,
) {
    var confirmDelete by remember { mutableStateOf(false) }
    val dateFmt = SimpleDateFormat("EEE, d MMM yyyy", Locale("id"))
    val timeFmt = SimpleDateFormat("HH:mm", Locale("id"))
    val date    = Date(session.startTime)
    val vehicle = try { VehicleType.valueOf(session.vehicleType) } catch (_: Exception) { VehicleType.OTHER }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderHiDark, RoundedCornerShape(8.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("←", color = TextPrimary, fontSize = 14.sp)
                }

                Spacer(Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text          = "#${session.id} · DETAIL",
                        fontSize      = 9.sp,
                        fontFamily    = FontFamily.Monospace,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.8.sp,
                        color         = TextDim,
                    )
                    Text(
                        text       = when (vehicle) {
                            VehicleType.MOTOR_SPORT -> "Motor Sport"
                            VehicleType.MOTOR_MATIC -> "Motor Matic"
                            VehicleType.MOBIL       -> "Mobil"
                            else                    -> "Sepeda"
                        },
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextPrimary,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceDark)
                        .border(1.dp, BorderHiDark, RoundedCornerShape(8.dp))
                        .clickable { confirmDelete = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("🗑", fontSize = 14.sp)
                }
            }

            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderDark))

                // Vehicle + date
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceDark)
                            .border(1.dp, BorderHiDark, RoundedCornerShape(10.dp)),
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
                            fontSize      = 14.sp,
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 0.4.sp,
                            color         = TextPrimary,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text          = "${dateFmt.format(date).uppercase()} · ${timeFmt.format(date)}",
                            fontSize      = 10.sp,
                            fontFamily    = FontFamily.Monospace,
                            color         = TextDim,
                            letterSpacing = 0.8.sp,
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(BorderDark))

                // Distance hero
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
                        fontSize      = 62.sp,
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

                // Stats grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BorderDark),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    com.avishena.ridingtracker.ui.components.ResultCell(
                        "DURASI", formatDuration(session.durationMs),
                        modifier = Modifier.weight(1f),
                    )
                    com.avishena.ridingtracker.ui.components.ResultCell(
                        "TOP SPEED", session.topSpeedKmh.roundToInt().toString(), "KM/H",
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(1.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BorderDark),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    com.avishena.ridingtracker.ui.components.ResultCell(
                        "AVG SPEED", session.avgSpeedKmh.roundToInt().toString(), "KM/H",
                        modifier = Modifier.weight(1f),
                    )
                    com.avishena.ridingtracker.ui.components.ResultCell(
                        "≥ 100 KM/H", session.count100kmh.toString(), "×",
                        accent = true,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        // Confirm delete bottom sheet
        if (confirmDelete) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.72f))
                    .clickable { confirmDelete = false },
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .background(SurfaceDark)
                        .border(
                            width = 1.dp,
                            color = BorderHiDark,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        )
                        .padding(24.dp)
                        .clickable {  }, // absorb clicks
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(BorderHiDark)
                            .align(Alignment.CenterHorizontally),
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text          = "// CONFIRM",
                        fontSize      = 10.sp,
                        fontFamily    = FontFamily.Monospace,
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 2.2.sp,
                        color         = Accent,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text       = "Hapus Sesi?",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextPrimary,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text      = "Sesi #${session.id} · %.1f km akan dihapus permanen dari database.".format(session.totalDistanceKm),
                        fontSize  = 12.sp,
                        color     = TextDim,
                        lineHeight = 18.sp,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick  = { onDelete(session.id); confirmDelete = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.Black),
                    ) {
                        Text(
                            text          = "Ya, Hapus",
                            fontSize      = 13.sp,
                            fontWeight    = FontWeight.ExtraBold,
                            letterSpacing = 1.4.sp,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick  = { confirmDelete = false },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, BorderHiDark),
                    ) {
                        Text(
                            text          = "Batal",
                            fontSize      = 13.sp,
                            fontWeight    = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        )
                    }
                }
            }
        }
    }
}
