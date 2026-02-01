package com.app.subscripfy.presentation.home

import android.util.Log
import android.graphics.Color.parseColor
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.subscripfy.data.model.Subscription
import com.app.subscripfy.data.model.UserData
import com.app.subscripfy.utils.ColorHelper
import com.app.subscripfy.utils.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userData: UserData?,
    viewModel: HomeViewModel,
    onSignOut: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    val subs by viewModel.subscriptions.collectAsState()
    val total by viewModel.totalExpense.collectAsState()
    val budgetLimit by viewModel.budgetLimit.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Jembatan Data
    LaunchedEffect(key1 = userData) {
        if (userData != null) {
            Log.d("HomeScreen", "Loading data for user: ${userData.userId}")
            viewModel.loadSubscriptions(userData.userId)
        }
    }

    // State Dialogs
    var showBudgetDialog by remember { mutableStateOf(false) }
    var tempBudgetInput by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<String?>(null) }

    // --- FIX DIALOG BUDGET ---
    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            containerColor = Color(0xFF1E1E1E), // Hanya set warna background container
            title = {
                Text("Set Monthly Budget", color = Color.White) // Warna teks diatur disini
            },
            text = {
                OutlinedTextField(
                    value = tempBudgetInput,
                    onValueChange = { if (it.all { c -> c.isDigit() }) tempBudgetInput = it },
                    label = { Text("Amount (Rp)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFBB86FC),
                        unfocusedBorderColor = Color.Gray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLimit = tempBudgetInput.toDoubleOrNull() ?: budgetLimit
                        viewModel.setBudgetLimit(newLimit)
                        showBudgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC))
                ) { Text("Save", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // --- FIX DIALOG DELETE ---
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF1E1E1E),
            icon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFCF6679)) },
            title = { Text("Delete Subscription?", color = Color.White) },
            text = { Text("This action cannot be undone.", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteSubscription(it) }
                        showDeleteDialog = false
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679))
                ) { Text("Delete", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // UI UTAMA
    Scaffold(
        containerColor = Color(0xFF121212),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFFBB86FC),
                contentColor = Color.Black
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // HEADER ARTISTIK
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SUBSCRIPFY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color(0xFFBB86FC)
                    )
                    Text(
                        text = "Manage like a pro.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Foto Profil (Klik untuk pindah tab)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable { onProfileClick() }
                ) {
                    if (userData?.profilePictureUrl != null) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Budget Card
            CyberBudgetCard(
                total = total,
                limit = budgetLimit,
                onClick = {
                    tempBudgetInput = budgetLimit.toInt().toString()
                    showBudgetDialog = true
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Your Subscriptions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // LIST CONTENT
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFBB86FC))
                }
            } else if (subs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No active subscriptions found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(items = subs, key = { it.id }) { item ->
                        PremiumSubItem(
                            item = item,
                            onClick = { onEditClick(item.id) },
                            onLongClick = {
                                itemToDelete = item.id
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- KOMPONEN UI TAMBAHAN ---

@Composable
fun CyberBudgetCard(total: Double, limit: Double, onClick: () -> Unit) {
    val progress = (total / limit).toFloat().coerceIn(0f, 1f)
    val isSafe = total <= limit
    val progressColor = if (isSafe) Color(0xFF03DAC5) else Color(0xFFCF6679)

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF2C2C2C), Color(0xFF1E1E1E))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Monthly Expenses", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatRupiah(total),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = progressColor,
                    trackColor = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "${(progress * 100).toInt()}% Used",
                        color = progressColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Limit: ${formatRupiah(limit)}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PremiumSubItem(
    item: Subscription,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val cardColor = try { Color(parseColor(item.colorHex)) } catch (e: Exception) { Color(0xFFBB86FC) }

    // Logika warna teks adaptif
    val contentColor = ColorHelper.getContentColor(cardColor)
    val secondaryColor = contentColor.copy(alpha = 0.8f)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Inisial
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name.take(1).uppercase(),
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nama & Tanggal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tgl ${item.billingDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryColor
                )
            }

            // Harga
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatRupiah(item.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}