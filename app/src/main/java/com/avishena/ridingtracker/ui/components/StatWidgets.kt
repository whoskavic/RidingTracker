package com.avishena.ridingtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.AccentGlow
import com.avishena.ridingtracker.ui.theme.BgDark
import com.avishena.ridingtracker.ui.theme.GoodGreen
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary

/** Inline stat cell used in the 3-column row on the session screen. */
@Composable
fun StatCellInline(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
) {
    Column(
        modifier          = modifier.padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text          = label,
            fontSize      = 9.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 2.2.sp,
            color         = TextDim,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
            Text(
                text          = value,
                fontSize      = 22.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = if (accent) Accent else TextPrimary,
            )
            Spacer(Modifier.padding(start = 3.dp))
            Text(
                text          = unit,
                fontSize      = 9.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = TextFaint,
                letterSpacing = 0.6.sp,
                modifier      = Modifier.padding(bottom = 3.dp),
            )
        }
    }
}

/** 2-col grid cell used on result screen. */
@Composable
fun ResultCell(
    label: String,
    value: String,
    unit: String = "",
    accent: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(BgDark)
            .padding(16.dp),
    ) {
        Text(
            text          = label,
            fontSize      = 9.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 2.2.sp,
            color         = TextDim,
        )
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text          = value,
                fontSize      = 26.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = if (accent) Accent else TextPrimary,
            )
            if (unit.isNotEmpty()) {
                Spacer(Modifier.padding(start = 4.dp))
                Text(
                    text          = unit,
                    fontSize      = 10.sp,
                    fontFamily    = FontFamily.Monospace,
                    fontWeight    = FontWeight.Bold,
                    color         = TextFaint,
                    letterSpacing = 1.sp,
                    modifier      = Modifier.padding(bottom = 3.dp),
                )
            }
        }
    }
}

/** Aggregate summary cell for the history header strip. */
@Composable
fun AggCell(
    label: String,
    value: String,
    unit: String = "",
    accent: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(BgDark)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text          = label,
            fontSize      = 8.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 2.sp,
            color         = TextDim,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text          = value,
                fontSize      = 18.sp,
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

/** Pulsing status dot + label. */
@Composable
fun StatusChip(
    label: String,
    color: Color,
    pulse: Boolean = false,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.padding(start = 6.dp))
        Text(
            text          = label,
            fontSize      = 10.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 1.8.sp,
            color         = color,
        )
    }
}

fun formatDuration(ms: Long): String {
    val sec = ms / 1000
    val h   = sec / 3600
    val m   = (sec % 3600) / 60
    val s   = sec % 60
    return "%02d:%02d:%02d".format(h, m, s)
}

fun formatDurationShort(ms: Long): String {
    val sec = ms / 1000
    val h   = sec / 3600
    val m   = (sec % 3600) / 60
    return if (h > 0) "${h}H ${"%02d".format(m)}M" else "${m}M"
}
