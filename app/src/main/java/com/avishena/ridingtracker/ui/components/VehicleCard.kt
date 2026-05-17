package com.avishena.ridingtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.data.model.VehicleType
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.BgDark
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.BorderHiDark
import com.avishena.ridingtracker.ui.theme.Surface2Dark
import com.avishena.ridingtracker.ui.theme.SurfaceDark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary

data class VehicleOption(
    val type: VehicleType,
    val label: String,
    val spec: String,
    val hasLean: Boolean,
)

val vehicleOptions = listOf(
    VehicleOption(VehicleType.MOTOR_SPORT, "Motor Sport", "GPS · GYRO · LEAN ANGLE", true),
    VehicleOption(VehicleType.MOTOR_MATIC, "Motor Matic", "GPS · SPEEDOMETER", false),
    VehicleOption(VehicleType.MOBIL, "Mobil", "GPS · SPEEDOMETER", false),
    VehicleOption(VehicleType.OTHER, "Sepeda", "GPS · CADENCE", false),
)

@Composable
fun VehicleCard(
    option: VehicleOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) Accent else BorderDark
    val bgColor     = if (selected) Surface2Dark else SurfaceDark

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left accent slash indicator
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                    .background(if (selected) Accent else Color.Transparent),
            )

            Spacer(Modifier.width(12.dp))

            // Vehicle icon
            Box(
                modifier         = Modifier.size(64.dp, 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                    val iconColor = if (selected) Accent else TextDim
                    drawVehicleIcon(option.type, iconColor)
                }
            }

            Spacer(Modifier.width(16.dp))

            // Labels
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text          = option.label,
                    fontSize      = 16.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = TextPrimary,
                    letterSpacing = 0.4.sp,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text          = option.spec,
                    fontSize      = 11.sp,
                    fontFamily    = FontFamily.Monospace,
                    color         = TextFaint,
                    letterSpacing = 0.5.sp,
                )
                if (option.hasLean) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Accent.copy(alpha = 0.85f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text          = "● LEAN ANGLE",
                            fontSize      = 9.sp,
                            fontFamily    = FontFamily.Monospace,
                            fontWeight    = FontWeight.Bold,
                            color         = Accent,
                            letterSpacing = 1.2.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Radio dot
            Box(
                modifier         = Modifier
                    .size(20.dp)
                    .border(
                        width = 1.5.dp,
                        color = if (selected) Accent else BorderHiDark,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Accent),
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawVehicleIcon(type: VehicleType, color: Color) {
    val stroke = Stroke(
        width = 1.6.dp.toPx(),
        cap   = StrokeCap.Round,
        join  = StrokeJoin.Round,
    )
    // Normalize viewBox 48×32 to DrawScope size
    val scaleX = size.width / 48f
    val scaleY = size.height / 32f
    fun o(x: Float, y: Float) = Offset(x * scaleX, y * scaleY)

    when (type) {
        VehicleType.MOTOR_SPORT -> {
            drawCircle(color, 6f * scaleX, o(10f, 24f), style = stroke)
            drawCircle(color, 6f * scaleX, o(38f, 24f), style = stroke)
            val body = Path().apply {
                moveTo(o(10f,24f).x, o(10f,24f).y)
                lineTo(o(16f,14f).x, o(16f,14f).y)
                lineTo(o(26f,12f).x, o(26f,12f).y)
                lineTo(o(34f,10f).x, o(34f,10f).y)
                lineTo(o(40f,14f).x, o(40f,14f).y)
                lineTo(o(38f,24f).x, o(38f,24f).y)
            }
            drawPath(body, color, style = stroke)
            val fairing = Path().apply {
                moveTo(o(16f,14f).x, o(16f,14f).y)
                quadraticTo(o(22f,8f).x, o(22f,8f).y, o(32f,8f).x, o(32f,8f).y)
            }
            drawPath(fairing, color, style = stroke)
            drawLine(color, o(32f,8f), o(36f,12f), stroke.width, StrokeCap.Round)
            drawLine(color, o(24f,12f), o(28f,18f), stroke.width, StrokeCap.Round)
        }
        VehicleType.MOTOR_MATIC -> {
            drawCircle(color, 6f * scaleX, o(10f, 24f), style = stroke)
            drawCircle(color, 6f * scaleX, o(38f, 24f), style = stroke)
            val body = Path().apply {
                moveTo(o(10f,24f).x, o(10f,24f).y)
                lineTo(o(14f,20f).x, o(14f,20f).y)
                lineTo(o(22f,20f).x, o(22f,20f).y)
                lineTo(o(26f,14f).x, o(26f,14f).y)
                lineTo(o(32f,14f).x, o(32f,14f).y)
                lineTo(o(36f,18f).x, o(36f,18f).y)
                lineTo(o(38f,24f).x, o(38f,24f).y)
            }
            drawPath(body, color, style = stroke)
            val hb = Path().apply {
                moveTo(o(26f,14f).x, o(26f,14f).y)
                lineTo(o(24f,6f).x, o(24f,6f).y)
                lineTo(o(30f,6f).x, o(30f,6f).y)
            }
            drawPath(hb, color, style = stroke)
        }
        VehicleType.MOBIL -> {
            drawCircle(color, 5f * scaleX, o(12f, 24f), style = stroke)
            drawCircle(color, 5f * scaleX, o(36f, 24f), style = stroke)
            val car = Path().apply {
                moveTo(o(4f,24f).x, o(4f,24f).y)
                lineTo(o(4f,18f).x, o(4f,18f).y)
                quadraticTo(o(4f,14f).x, o(4f,14f).y, o(8f,14f).x, o(8f,14f).y)
                lineTo(o(16f,8f).x, o(16f,8f).y)
                lineTo(o(32f,8f).x, o(32f,8f).y)
                lineTo(o(36f,14f).x, o(36f,14f).y)
                lineTo(o(44f,14f).x, o(44f,14f).y)
                lineTo(o(44f,18f).x, o(44f,18f).y)
                lineTo(o(44f,24f).x, o(44f,24f).y)
            }
            drawPath(car, color, style = stroke)
            drawLine(color, o(4f,18f), o(44f,18f), stroke.width, StrokeCap.Round)
            drawLine(color, o(16f,8f), o(18f,14f), stroke.width, StrokeCap.Round)
            drawLine(color, o(30f,8f), o(32f,14f), stroke.width, StrokeCap.Round)
            drawLine(color, o(24f,8f), o(24f,14f), stroke.width, StrokeCap.Round)
        }
        else -> {
            drawCircle(color, 6f * scaleX, o(12f, 24f), style = stroke)
            drawCircle(color, 6f * scaleX, o(36f, 24f), style = stroke)
            val frame = Path().apply {
                moveTo(o(12f,24f).x, o(12f,24f).y)
                lineTo(o(22f,12f).x, o(22f,12f).y)
                lineTo(o(28f,24f).x, o(28f,24f).y)
                lineTo(o(36f,24f).x, o(36f,24f).y)
                lineTo(o(22f,12f).x, o(22f,12f).y)
                lineTo(o(30f,12f).x, o(30f,12f).y)
            }
            drawPath(frame, color, style = stroke)
            drawLine(color, o(30f,12f), o(34f,8f), stroke.width, StrokeCap.Round)
            drawLine(color, o(20f,8f), o(24f,8f), stroke.width, StrokeCap.Round)
        }
    }
}
