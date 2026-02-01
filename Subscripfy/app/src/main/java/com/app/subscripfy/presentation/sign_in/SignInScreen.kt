package com.app.subscripfy.presentation.sign_in

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    // Trigger animasi masuk saat layar dibuka
    LaunchedEffect(Unit) {
        isVisible = true
    }

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Animasi Breathing untuk Logo
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)) // Dasar Gelap
    ) {
        // [1. BACKGROUND MESH GRADIENT] - "Mirip Aplikasi Teman"
        // Kita gambar manual pakai Canvas biar terlihat fluid/cair
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Blob Ungu di Kiri Atas
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6200EA), Color.Transparent),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f
                ),
                center = Offset(0f, 0f),
                radius = size.width * 0.8f
            )
            // Blob Biru Muda di Kanan Tengah
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF03DAC5), Color.Transparent),
                    center = Offset(size.width, size.height * 0.4f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width, size.height * 0.4f),
                radius = size.width * 0.6f
            )
            // Blob Pink di Bawah
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFCF6679), Color.Transparent),
                    center = Offset(size.width * 0.2f, size.height),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.2f, size.height),
                radius = size.width * 0.7f
            )
        }

        // Overlay Blur agar warna menyatu lembut (Glass effect base)
        // Jika error di API lama, hapus baris .blur ini
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.3f)))

        // [2. LAYOUT UTAMA]
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {

            // --- BAGIAN ATAS: LOGO & VISUAL ---
            Box(
                modifier = Modifier
                    .weight(1.5f) // Mengambil porsi atas lebih besar
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Dekorasi Lingkaran di belakang logo
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale) // Ikut bernafas
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                )

                // Logo Utama
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.1f)) // Glassy Box
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }
            }

            // --- BAGIAN BAWAH: TEKS & TOMBOL ---
            // Menggunakan AnimatedVisibility agar muncul dari bawah (Slide Up)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(1000)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Teks Besar Kiri
                    Text(
                        text = "Master Your\nSubscriptions",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 44.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Kelola keuangan lebih cerdas dengan tampilan futuristik.",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // TOMBOL LOGIN MODERN (Lebar Penuh)
                    if (state.isSignInSuccessfull) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = onSignInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp), // Tombol Tinggi
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ikon Google Text Placeholder
                                Text("G", fontWeight = FontWeight.Black, fontSize = 24.sp)

                                Text(
                                    text = "Continue with Google",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Icon(Icons.Default.ArrowForward, null)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}