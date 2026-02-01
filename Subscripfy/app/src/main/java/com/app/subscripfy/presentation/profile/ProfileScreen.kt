package com.app.subscripfy.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // [PENTING] Untuk akses Intent & Toast
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.subscripfy.data.model.UserData

@Composable
fun ProfileScreen(
    userData: UserData?,
    viewModel: HomeViewModel,
    onSignOut: () -> Unit
) {
    val subs by viewModel.subscriptions.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // [CONTEXT] Diperlukan untuk Toast dan Intent
    val context = LocalContext.current

    // --- LOGIC WHATSAPP ---
    val openWhatsApp = {
        val phoneNumber = "6281282196894" // Format internasional tanpa +
        val url = "https://wa.me/$phoneNumber"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show()
        }
    }

    // --- LOGIC PEMANIS ---
    val showDummyToast = {
        Toast.makeText(context, "Fitur Ini hanya pemanis tampilan :)", Toast.LENGTH_SHORT).show()
    }

    // Dialog Logout
    if (showLogoutDialog) {
        AlertDialog(
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                Button(onClick = { showLogoutDialog = false; onSignOut() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679))) { Text("Log Out", color = Color.Black) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel", color = Color.Gray) } }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("My Profile", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        // 1. INFO CARD (Gradient)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF6200EA), Color(0xFF3700B3))))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (userData?.profilePictureUrl != null) {
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(80.dp), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(userData?.username ?: "User", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Free Plan", color = Color.White.copy(alpha=0.7f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.background(Color.White.copy(alpha=0.2f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("${subs.size} Active Subs", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 2. SETTINGS MENU
        Text("General", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        ProfileOptionItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            subtitle = "Manage alerts",
            onClick = showDummyToast // Panggil Toast
        )
        ProfileOptionItem(
            icon = Icons.Default.Security,
            title = "Security",
            subtitle = "Biometric & Password",
            onClick = showDummyToast // Panggil Toast
        )
        ProfileOptionItem(
            icon = Icons.Default.Language,
            title = "Language",
            subtitle = "English (US)",
            onClick = showDummyToast // Panggil Toast
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text("Support", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // Help Center -> WhatsApp
        ProfileOptionItem(
            icon = Icons.Default.Help,
            title = "Help Center",
            subtitle = "FAQ & Contact",
            onClick = openWhatsApp // Panggil WhatsApp Intent
        )

        Spacer(modifier = Modifier.weight(1f))

        // 3. LOGOUT BUTTON
        Button(
            onClick = { showLogoutDialog = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCF6679))
        ) {
            Icon(Icons.Default.Logout, null, tint = Color(0xFFCF6679))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Log Out", color = Color(0xFFCF6679), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Komponen Item Menu (Updated dengan onClick)
@Composable
fun ProfileOptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit // Parameter Baru
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() }, // Aksi Klik
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(44.dp).background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color(0xFFBB86FC), modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}