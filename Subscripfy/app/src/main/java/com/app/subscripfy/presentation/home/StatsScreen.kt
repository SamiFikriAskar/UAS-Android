package com.app.subscripfy.presentation.home

import android.graphics.Color.parseColor
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.subscripfy.utils.formatRupiah

@Composable
fun StatsScreen(viewModel: HomeViewModel) {
    val subs by viewModel.subscriptions.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val sortedByPrice = subs.sortedByDescending { it.price }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Background Hitam
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Where your money goes",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (subs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No data available", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(sortedByPrice) { item ->
                    val progress = if (totalExpense > 0) (item.price / totalExpense).toFloat() else 0f
                    DarkStatItem(item.name, item.price, progress, item.colorHex)
                }
            }
        }
    }
}

@Composable
fun DarkStatItem(name: String, price: Double, percentage: Float, colorHex: String) {
    val accentColor = try { Color(parseColor(colorHex)) } catch (e: Exception) { Color.Gray }
    val animatedProgress by animateFloatAsState(targetValue = percentage, animationSpec = tween(1000), label = "")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontWeight = FontWeight.Bold, color = Color.White)
            Text("${(percentage * 100).toInt()}%", fontWeight = FontWeight.Bold, color = accentColor)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dark Track Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)) // Track Abu Gelap
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(accentColor) // Bar warna user
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(formatRupiah(price), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}