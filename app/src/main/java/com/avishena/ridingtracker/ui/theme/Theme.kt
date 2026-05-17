package com.avishena.ridingtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RacingDarkColorScheme = darkColorScheme(
    primary            = Accent,
    onPrimary          = Color(0xFF000000),
    primaryContainer   = SurfaceDark,
    onPrimaryContainer = TextPrimary,
    secondary          = Accent2,
    onSecondary        = Color(0xFF000000),
    background         = BgDark,
    surface            = SurfaceDark,
    surfaceVariant     = Surface2Dark,
    onBackground       = TextPrimary,
    onSurface          = TextPrimary,
    onSurfaceVariant   = TextDim,
    outline            = BorderDark,
    outlineVariant     = BorderHiDark,
    error              = Accent,
)

@Composable
fun RidingTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RacingDarkColorScheme,
        typography  = Typography,
        content     = content,
    )
}
