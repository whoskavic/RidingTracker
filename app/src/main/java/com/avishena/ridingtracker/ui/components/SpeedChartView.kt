package com.avishena.ridingtracker.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.BorderDark
import com.avishena.ridingtracker.ui.theme.SurfaceDark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

data class SpeedSample(val timeSec: Float, val speedKmh: Float)

@Composable
fun SpeedChartView(
    samples: List<SpeedSample>,
    maxY: Float = 220f,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp)),
        factory  = { ctx ->
            LineChart(ctx).apply {
                setBackgroundColor(SurfaceDark.toArgb())
                description.isEnabled = false
                legend.isEnabled      = false
                setTouchEnabled(false)
                setDrawGridBackground(false)

                // X axis
                xAxis.apply {
                    position       = XAxis.XAxisPosition.BOTTOM
                    textColor      = TextFaint.toArgb()
                    gridColor      = BorderDark.toArgb()
                    axisLineColor  = BorderDark.toArgb()
                    textSize       = 8f
                    setDrawAxisLine(true)
                    setDrawGridLines(true)
                }

                // Left Y axis
                axisLeft.apply {
                    textColor         = TextFaint.toArgb()
                    gridColor         = BorderDark.toArgb()
                    axisLineColor     = BorderDark.toArgb()
                    textSize          = 8f
                    axisMinimum       = 0f
                    axisMaximum       = maxY
                    setDrawAxisLine(true)
                    setDrawGridLines(true)

                    // 100 km/h limit line
                    val ll = LimitLine(100f).apply {
                        lineWidth    = 1.5f
                        lineColor    = Accent.toArgb()
                        enableDashedLine(0f, 0f, 0f)
                        textColor    = Accent.toArgb()
                        textSize     = 7f
                        label        = "100"
                    }
                    addLimitLine(ll)
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            if (samples.size < 2) {
                chart.clear()
                return@AndroidView
            }

            val entries = samples.map { Entry(it.timeSec, it.speedKmh) }
            val dataSet = LineDataSet(entries, "Speed").apply {
                color               = Accent.toArgb()
                lineWidth           = 2f
                setDrawCircles(false)
                setDrawValues(false)
                fillColor           = Accent.copy(alpha = 0.35f).toArgb()
                fillAlpha           = 90
                setDrawFilled(true)
                mode                = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        },
    )
}
