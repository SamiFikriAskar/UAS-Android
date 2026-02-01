package com.app.subscripfy.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object ColorUtils {
    // Fungsi untuk menentukan apakah warna teks harus Putih atau Hitam
    // berdasarkan warna background kartu
    fun getBestTextColor(backgroundColor: Color): Color {
        val contrastWithWhite = ColorUtils.calculateContrast(Color.White.toArgb(), backgroundColor.toArgb())
        // Jika kontras dengan putih rendah (< 1.5), berarti backgroundnya terang -> pakai teks Hitam
        return if (contrastWithWhite < 1.6) Color.Black else Color.White
    }
}