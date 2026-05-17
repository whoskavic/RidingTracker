package com.avishena.ridingtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.AccentGlow
import com.avishena.ridingtracker.ui.theme.BorderHiDark
import com.avishena.ridingtracker.ui.theme.Surface2Dark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun HoldButton(
    label: String       = "HOLD TO STOP",
    holdDurationMs: Long = 900L,
    size: Dp            = 82.dp,
    onComplete: () -> Unit,
) {
    var holding  by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(holding) {
        if (!holding) {
            progress = 0f
            return@LaunchedEffect
        }
        val start    = System.currentTimeMillis()
        val interval = 16L
        while (holding) {
            val elapsed = System.currentTimeMillis() - start
            progress = (elapsed.toFloat() / holdDurationMs).coerceIn(0f, 1f)
            if (progress >= 1f) {
                holding  = false
                progress = 0f
                onComplete()
                return@LaunchedEffect
            }
            delay(interval)
        }
        progress = 0f
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier         = Modifier
                .size(size)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress  = {
                            holding = true
                            tryAwaitRelease()
                            holding = false
                        }
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            val r        = size.value / 2f - 4f
            val strokePx = size.value * 0.045f

            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx   = this.size.width / 2f
                val cy   = this.size.height / 2f
                val rPx  = r.dp.toPx()
                val rect = Size(rPx * 2, rPx * 2)
                val tl   = Offset(cx - rPx, cy - rPx)

                // Background ring
                drawArc(
                    color      = BorderHiDark,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter  = false,
                    topLeft    = tl,
                    size       = rect,
                    style      = Stroke(width = strokePx.dp.toPx(), cap = StrokeCap.Round),
                )

                // Progress ring
                if (progress > 0f) {
                    drawArc(
                        color      = Accent,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter  = false,
                        topLeft    = tl,
                        size       = rect,
                        style      = Stroke(
                            width = (strokePx + 1f).dp.toPx(),
                            cap   = StrokeCap.Round,
                        ),
                    )
                }

                // Inner circle fill
                val innerR = (r - 10f).dp.toPx()
                drawCircle(
                    color  = if (progress > 0f)
                        Accent.copy(alpha = 0.2f + progress * 0.5f)
                    else Surface2Dark,
                    radius = innerR,
                    center = Offset(cx, cy),
                )
                drawCircle(
                    color  = Accent,
                    radius = innerR,
                    center = Offset(cx, cy),
                    style  = Stroke(width = 1.5.dp.toPx()),
                )

                // Stop icon (square)
                val sqHalf = (r * 0.22f).dp.toPx()
                drawRect(
                    color  = if (progress > 0.4f) TextPrimary else Accent,
                    topLeft = Offset(cx - sqHalf, cy - sqHalf),
                    size    = Size(sqHalf * 2, sqHalf * 2),
                )
            }
        }

        Text(
            text          = if (progress > 0f) "HOLDING… ${(progress * 100).roundToInt()}%" else label,
            fontSize      = 9.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 2.2.sp,
            color         = if (progress > 0f) Accent else TextDim,
        )
    }
}
