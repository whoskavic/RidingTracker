package com.avishena.ridingtracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.data.model.LocationPoint
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.AccentGlow
import com.avishena.ridingtracker.ui.theme.BgDark
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.BorderHiDark
import com.avishena.ridingtracker.ui.theme.GoodGreen
import com.avishena.ridingtracker.ui.theme.SurfaceDark
import com.avishena.ridingtracker.ui.theme.TextDim

/**
 * Small square mini-map inset shown during active session.
 * Draws a grid + route path + current position marker.
 * [points] are normalized 0..1 pairs (lat/lon mapped to canvas coords).
 */
@Composable
fun MapInset(
    points: List<Pair<Float, Float>>,
    size: Dp = 100.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Background
            drawRect(color = BgDark)

            // Grid
            val gridStep = w / 10f
            var gx = 0f
            while (gx <= w) {
                drawLine(BorderDark.copy(alpha = 0.5f), Offset(gx, 0f), Offset(gx, h), 0.4.dp.toPx())
                gx += gridStep
            }
            var gy = 0f
            while (gy <= h) {
                drawLine(BorderDark.copy(alpha = 0.5f), Offset(0f, gy), Offset(w, gy), 0.4.dp.toPx())
                gy += gridStep
            }

            // Faint background road hints
            val roadStroke = Stroke(width = 0.6.dp.toPx(), cap = StrokeCap.Round)
            val road1 = Path().apply {
                moveTo(0f, h * 0.35f)
                quadraticTo(w * 0.3f, h * 0.3f, w * 0.6f, h * 0.5f)
                quadraticTo(w * 0.8f, h * 0.65f, w, h * 0.7f)
            }
            drawPath(road1, BorderHiDark.copy(alpha = 0.7f), style = roadStroke)
            drawLine(BorderHiDark.copy(alpha = 0.7f), Offset(w * 0.2f, 0f), Offset(w * 0.25f, h), 0.6.dp.toPx())

            // Route
            if (points.size > 1) {
                val routePath = Path().apply {
                    moveTo(points[0].first * w, points[0].second * h)
                    for (i in 1 until points.size) {
                        lineTo(points[i].first * w, points[i].second * h)
                    }
                }
                drawPath(
                    routePath,
                    color = Accent,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap   = StrokeCap.Round,
                        join  = StrokeJoin.Round,
                    ),
                )
            }

            // Current position marker
            val head = if (points.isNotEmpty()) points.last() else Pair(0.5f, 0.5f)
            val hx = head.first * w
            val hy = head.second * h
            drawCircle(AccentGlow, radius = 6.dp.toPx(), center = Offset(hx, hy))
            drawCircle(Accent, radius = 2.5.dp.toPx(), center = Offset(hx, hy))
        }

        // GPS label
        Text(
            text          = "GPS",
            fontSize      = 8.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 1.8.sp,
            color         = TextDim,
            modifier      = Modifier
                .align(Alignment.TopStart)
                .padding(6.dp),
        )

        // Compass N
        Text(
            text          = "N",
            fontSize      = 8.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            color         = Accent,
            modifier      = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp),
        )
    }
}

/**
 * Larger static route replay map for the result/detail screen.
 * Normalizes route points to fit within the canvas.
 */
@Composable
fun ReplayMap(
    points: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            drawRect(color = BgDark)

            // Grid
            val step = w / 10f
            var gx   = 0f
            while (gx <= w) {
                drawLine(BorderDark.copy(alpha = 0.6f), Offset(gx, 0f), Offset(gx, h), 0.6.dp.toPx())
                gx += step
            }
            var gy = 0f
            while (gy <= h) {
                drawLine(BorderDark.copy(alpha = 0.6f), Offset(0f, gy), Offset(w, gy), 0.6.dp.toPx())
                gy += step
            }

            // Faint roads
            val rStroke = Stroke(width = 0.8.dp.toPx(), cap = StrokeCap.Round)
            val r1 = Path().apply {
                moveTo(0f, h * 0.35f)
                quadraticTo(w * 0.3f, h * 0.3f, w * 0.6f, h * 0.5f)
                quadraticTo(w * 0.8f, h * 0.65f, w, h * 0.7f)
            }
            drawPath(r1, BorderHiDark.copy(alpha = 0.6f), style = rStroke)
            drawLine(BorderHiDark.copy(alpha = 0.6f), Offset(w * 0.2f, 0f), Offset(w * 0.25f, h), 0.8.dp.toPx())
            val r2 = Path().apply {
                moveTo(w * 0.7f, 0f)
                quadraticTo(w * 0.65f, h * 0.4f, w * 0.8f, h * 0.6f)
                quadraticTo(w * 0.9f, h * 0.8f, w, h)
            }
            drawPath(r2, BorderHiDark.copy(alpha = 0.6f), style = rStroke)

            if (points.size < 2) return@Canvas

            // Normalize route to canvas
            val minX = points.minOf { it.first }
            val maxX = points.maxOf { it.first }
            val minY = points.minOf { it.second }
            val maxY = points.maxOf { it.second }
            val dx   = (maxX - minX).coerceAtLeast(0.0001f)
            val dy   = (maxY - minY).coerceAtLeast(0.0001f)
            val span = maxOf(dx, dy)
            val scale = 0.8f

            fun fit(p: Pair<Float, Float>): Offset = Offset(
                w / 2f + ((p.first  - (minX + maxX) / 2f) / span) * w * scale,
                h / 2f + ((p.second - (minY + maxY) / 2f) / span) * h * scale,
            )

            val scaled = points.map { fit(it) }

            // Route line
            val routePath = Path().apply {
                moveTo(scaled[0].x, scaled[0].y)
                for (i in 1 until scaled.size) lineTo(scaled[i].x, scaled[i].y)
            }
            drawPath(
                routePath,
                color = Accent,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )

            // Start marker (green)
            drawCircle(GoodGreen, radius = 4.dp.toPx(), center = scaled.first())

            // End marker (accent)
            drawCircle(Accent, radius = 5.dp.toPx(), center = scaled.last())
            drawCircle(
                Accent.copy(alpha = 0.5f),
                radius = 8.dp.toPx(),
                center = scaled.last(),
                style  = Stroke(width = 0.6.dp.toPx()),
            )
        }

        // Legend
        Text(
            text          = "● START",
            fontSize      = 9.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            color         = GoodGreen,
            modifier      = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
        )
        Text(
            text          = "● END",
            fontSize      = 9.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            color         = Accent,
            modifier      = Modifier
                .align(Alignment.TopStart)
                .padding(start = 60.dp, top = 8.dp),
        )
    }
}

/** Convert real GPS LocationPoint list to normalized 0..1 pairs for canvas rendering. */
fun List<LocationPoint>.toNormalized(): List<Pair<Float, Float>> {
    if (size < 2) return map { Pair(0.5f, 0.5f) }
    val minLat = minOf { it.latitude }.toFloat()
    val maxLat = maxOf { it.latitude }.toFloat()
    val minLon = minOf { it.longitude }.toFloat()
    val maxLon = maxOf { it.longitude }.toFloat()
    val dLat   = (maxLat - minLat).coerceAtLeast(0.0001f)
    val dLon   = (maxLon - minLon).coerceAtLeast(0.0001f)
    return map { p ->
        Pair(
            (p.longitude.toFloat() - minLon) / dLon,
            1f - (p.latitude.toFloat() - minLat) / dLat, // invert Y for screen coords
        )
    }
}
