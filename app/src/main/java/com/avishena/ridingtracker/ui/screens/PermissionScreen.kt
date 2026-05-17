package com.avishena.ridingtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avishena.ridingtracker.ui.theme.Accent
import com.avishena.ridingtracker.ui.theme.BgDark
import com.avishena.ridingtracker.ui.theme.BorderHiDark
import com.avishena.ridingtracker.ui.theme.SurfaceDark
import com.avishena.ridingtracker.ui.theme.TextDim
import com.avishena.ridingtracker.ui.theme.TextFaint
import com.avishena.ridingtracker.ui.theme.TextPrimary

@Composable
fun PermissionScreen(
    isPermanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .statusBarsPadding()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // App label
        Text(
            text          = "// RIDING TRACKER",
            fontSize      = 10.sp,
            fontFamily    = FontFamily.Monospace,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 2.4.sp,
            color         = Accent,
        )

        Spacer(Modifier.height(32.dp))

        // Icon
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderHiDark, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text     = if (isPermanentlyDenied) "🔒" else "📍",
                fontSize = 40.sp,
            )
        }

        Spacer(Modifier.height(28.dp))

        // Title
        Text(
            text       = if (isPermanentlyDenied) "Akses Ditolak" else "Izin Lokasi Diperlukan",
            fontSize   = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color      = TextPrimary,
            textAlign  = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        // Description
        Text(
            text = if (isPermanentlyDenied)
                "Kamu telah menolak izin lokasi secara permanen. Buka Pengaturan lalu izinkan akses lokasi agar app dapat merekam rute perjalanan."
            else
                "App ini membutuhkan akses lokasi GPS untuk merekam rute, kecepatan, dan jarak perjalananmu secara real-time.",
            fontSize   = 13.sp,
            color      = TextDim,
            textAlign  = TextAlign.Center,
            lineHeight = 20.sp,
        )

        Spacer(Modifier.height(8.dp))

        if (!isPermanentlyDenied) {
            Text(
                text          = "Data lokasi hanya digunakan selama sesi aktif dan tidak dikirim ke server manapun.",
                fontSize      = 11.sp,
                fontFamily    = FontFamily.Monospace,
                color         = TextFaint,
                textAlign     = TextAlign.Center,
                letterSpacing = 0.3.sp,
                lineHeight    = 17.sp,
            )
        }

        Spacer(Modifier.height(36.dp))

        // Primary CTA
        Button(
            onClick  = if (isPermanentlyDenied) onOpenSettings else onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape  = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor   = Color.Black,
            ),
        ) {
            Text(
                text          = if (isPermanentlyDenied) "Buka Pengaturan" else "Izinkan Akses Lokasi",
                fontSize      = 14.sp,
                fontWeight    = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
            )
        }

        if (isPermanentlyDenied) {
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick  = onRequestPermission,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderHiDark),
            ) {
                Text(
                    text          = "Coba Lagi",
                    fontSize      = 13.sp,
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                )
            }
        }
    }
}
