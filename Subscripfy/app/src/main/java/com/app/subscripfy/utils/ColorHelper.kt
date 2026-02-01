package com.app.subscripfy.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object ColorHelper {
    // Fungsi ini menentukan: Kalau background gelap, teks putih. Kalau terang, teks hitam.
    fun getContentColor(backgroundColor: Color): Color {
        val contrast = ColorUtils.calculateContrast(Color.White.toArgb(), backgroundColor.toArgb())
        // Jika kontras dengan putih rendah (< 1.6), berarti background terang -> Pakai Teks Hitam
        return if (contrast < 1.6) Color.Black else Color.White
    }
}