package com.avishena.ridingtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.AccentGlow
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.BorderHiDark
import com.avishena.ridingtracker.ui.theme.Surface2Dark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary
import kotlin.math.abs

@Composable
fun LeanAngleBar(
    angle: Float,
    modifier: Modifier = Modifier,
    max: Float = 50f,
) {
    val pct      = (angle / max).coerceIn(-1f, 1f)
    val absAngle = abs(angle)
    val dir      = when {
        angle > 0.5f  -> "R"
        angle < -0.5f -> "L"
        else           -> ""
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text          = "LEAN ANGLE",
                fontSize      = 9.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.8.sp,
                color         = TextDim,
                modifier      = Modifier.weight(1f),
            )
            Text(
                text          = "${absAngle.toInt()}° $dir",
                fontSize      = 14.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = if (absAngle > 30f) Accent else TextPrimary,
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
        ) {
            val w = this.size.width
            val h = this.size.height

            // Background track
            drawRect(color = Surface2Dark, size = this.size)
            drawRect(
                color = BorderDark,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
                size  = this.size,
            )

            // Tick marks
            val maxVal = max.toInt()
            var v      = -maxVal
            while (v <= maxVal) {
                val tickPct = (v + maxVal).toFloat() / (maxVal * 2)
                val tx      = tickPct * w
                val isMajor = v == 0
                drawLine(
                    color  = if (isMajor) TextDim else BorderHiDark,
                    start  = Offset(tx, 0f),
                    end    = Offset(tx, h),
                    strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx(),
                )
                v += 10
            }

            // Filled indicator
            val centerX = w / 2f
            val barW    = abs(pct) * (w / 2f)
            if (barW > 0f) {
                val left  = if (pct >= 0f) centerX else centerX - barW
                drawRect(
                    brush   = Brush.horizontalGradient(
                        colors = if (pct >= 0f)
                            listOf(AccentGlow, Accent)
                        else
                            listOf(Accent, AccentGlow),
                        startX = left,
                        endX   = left + barW,
                    ),
                    topLeft = Offset(left, 2.dp.toPx()),
                    size    = Size(barW, h - 4.dp.toPx()),
                )
            }

            // Center crosshair
            drawLine(
                color  = TextPrimary.copy(alpha = 0.6f),
                start  = Offset(centerX, -3.dp.toPx()),
                end    = Offset(centerX, h + 3.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
            )
        }

        // Side labels
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text          = "L",
                fontSize      = 9.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = TextFaint,
                modifier      = Modifier.weight(1f),
            )
            Text(
                text          = "R",
                fontSize      = 9.sp,
                fontFamily    = FontFamily.Monospace,
                fontWeight    = FontWeight.Bold,
                color         = TextFaint,
            )
        }

        Text(
            text          = "* Akurasi tergantung posisi HP",
            fontSize      = 9.sp,
            fontFamily    = FontFamily.Monospace,
            color         = TextFaint,
            letterSpacing = 0.4.sp,
            modifier      = Modifier.padding(top = 4.dp),
        )
    }
}
