package com.app.subscripfy.data.model

import com.google.firebase.Timestamp

data class Subscription(
    val id: String = "",
    val userId: String = "", // Untuk memisahkan data antar user
    val name: String = "",   // Contoh: Netflix, Spotify
    val price: Double = 0.0, // Harga langganan
    val billingDate: Int = 1, // Tanggal tagihan (1-31)
    val colorHex: String = "#FF0000", // Warna Card (Hex code)
    val createdAt: Timestamp = Timestamp.now()
)