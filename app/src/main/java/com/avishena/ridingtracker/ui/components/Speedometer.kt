package com.avishena.ridingtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.Accent2
import com.avishena.ridingtracker.ui.theme.AccentGlow
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun Speedometer(
    speed: Float,
    modifier: Modifier = Modifier,
    max: Float = 220f,
    redline: Float = 180f,
    size: Dp = 260.dp,
) {
    val pct          = (speed / max).coerceIn(0f, 1f)
    val redlinePct   = redline / max
    val startAngle   = 135f
    val sweepTotal   = 270f
    val isRedlining  = speed >= redline

    Box(
        modifier          = modifier.size(size),
        contentAlignment  = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx     = this.size.width / 2f
            val cy     = this.size.height / 2f
            val radius = this.size.width * 0.42f
            val stroke = 8.dp.toPx()

            fun polar(angleDeg: Float): Offset {
                val rad = Math.toRadians((angleDeg - 90.0))
                return Offset(cx + radius * cos(rad).toFloat(), cy + radius * sin(rad).toFloat())
            }

            // Track
            drawArc(
                color      = BorderDark,
                startAngle = startAngle,
                sweepAngle = sweepTotal,
                useCenter  = false,
                topLeft    = Offset(cx - radius, cy - radius),
                size       = Size(radius * 2, radius * 2),
                style      = Stroke(width = stroke, cap = StrokeCap.Round),
            )

            // Redline zone dim strip
            val redlineStart = startAngle + sweepTotal * redlinePct
            val redlineSweep = sweepTotal * (1f - redlinePct)
            drawArc(
                color      = Accent.copy(alpha = 0.35f),
                startAngle = redlineStart,
                sweepAngle = redlineSweep,
                useCenter  = false,
                topLeft    = Offset(cx - radius, cy - radius),
                size       = Size(radius * 2, radius * 2),
                style      = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
            )

            // Filled arc
            if (pct > 0.001f) {
                drawArc(
                    brush      = Brush.sweepGradient(
                        colorStops = arrayOf(0f to Accent2, pct to Accent),
                        center     = Offset(cx, cy),
                    ),
                    startAngle = startAngle,
                    sweepAngle = sweepTotal * pct,
                    useCenter  = false,
                    topLeft    = Offset(cx - radius, cy - radius),
                    size       = Size(radius * 2, radius * 2),
                    style      = Stroke(width = stroke, cap = StrokeCap.Round),
                )
            }

            // Tick marks every 20 km/h
            var v = 0
            while (v <= max.toInt()) {
                val angle   = startAngle + sweepTotal * (v / max)
                val rad     = Math.toRadians((angle - 90.0))
                val major   = v % 40 == 0
                val inRed   = v >= redline
                val tickLen = if (major) 16.dp.toPx() else 9.dp.toPx()

                val outerPt = Offset(cx + radius * cos(rad).toFloat(), cy + radius * sin(rad).toFloat())
                val innerPt = Offset(
                    cx + (radius - tickLen) * cos(rad).toFloat(),
                    cy + (radius - tickLen) * sin(rad).toFloat(),
                )

                drawLine(
                    color       = if (inRed) Accent else TextFaint,
                    start       = innerPt,
                    end         = outerPt,
                    strokeWidth = if (major) 2.dp.toPx() else 1.dp.toPx(),
                    alpha       = if (major) 0.85f else 0.45f,
                )
                v += 20
            }
        }

        // Center: digit + unit label
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = speed.roundToInt().toString().padStart(3),
                fontSize   = (size.value * 0.36).sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color      = if (isRedlining) Accent else TextPrimary,
                textAlign  = TextAlign.Center,
                letterSpacing = 2.sp,
            )
            Text(
                text          = "KM/H",
                fontSize      = 11.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.SemiBold,
                color         = TextDim,
                letterSpacing = 2.2.sp,
            )
        }
    }
}
