package com.app.subscripfy.presentation.home

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.subscripfy.data.model.Subscription
import com.app.subscripfy.data.repository.SubscriptionRepository
import com.app.subscripfy.utils.ColorHelper
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    subscriptionId: String,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val repository = remember { SubscriptionRepository() }

    // State Form
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(1) }
    var selectedColorHex by remember { mutableStateOf("#BB86FC") }
    var currentUserId by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load Data
    LaunchedEffect(subscriptionId) {
        val sub = repository.getSubscriptionById(subscriptionId)
        if (sub != null) {
            name = sub.name
            price = sub.price.toString().replace(".0", "")
            selectedDate = sub.billingDate
            selectedColorHex = sub.colorHex
            currentUserId = sub.userId
            isLoading = false
        } else {
            onNavigateBack() // Data tidak ditemukan
        }
    }

    // Pilihan Warna
    val colors = listOf(
        "#E53935", "#D81B60", "#8E24AA", "#5E35B1", "#3949AB",
        "#1E88E5", "#039BE5", "#00ACC1", "#00897B", "#43A047",
        "#7CB342", "#FDD835", "#FB8C00", "#F4511E", "#6D4C41",
        "#757575", "#546E7A", "#212121"
    )

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog) {
        AlertDialog(
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = Color(0xFFCF6679)) },
            title = { Text("Hapus Layanan?") },
            text = { Text("Data ini akan dihapus permanen dan tidak bisa dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            repository.deleteSubscription(subscriptionId)
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679))
                ) { Text("HAPUS", color = Color.Black, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = Color.Gray) }
            }
        )
    }

    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = millis
                        selectedDate = cal.get(Calendar.DAY_OF_MONTH)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal") } }
        ) { DatePicker(state = datePickerState) }
    }

    // UI UTAMA
    Scaffold(
        containerColor = Color(0xFF121212) // Background Dasar Hitam
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFBB86FC))
            }
        } else {
            // Background Gradient Artistik
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF121212), Color(0xFF1E1E1E), Color(0xFF2C2C2C))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(24.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar Custom
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }

                        Text("Edit Subscription", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                        // TOMBOL HAPUS (TONG SAMPAH)
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.background(Color(0xFFCF6679).copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Default.Delete, null, tint = Color(0xFFCF6679))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- PREVIEW CARD (Supaya User tahu hasil editnya) ---
                    val previewColor = try { Color(parseColor(selectedColorHex)) } catch (e: Exception) { Color.Blue }
                    val contentColor = ColorHelper.getContentColor(previewColor)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(previewColor)
                            .border(4.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(name.ifEmpty { "Service Name" }, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                Icon(Icons.Default.Edit, null, tint = contentColor)
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                                Column {
                                    Text("BILLING DATE", color = contentColor.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(selectedDate.toString(), color = contentColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                }
                                Text("Rp ${price.ifEmpty { "0" }}", color = contentColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // --- INPUT FORMS ---
                    EditCustomInput(
                        value = name, onValueChange = { name = it },
                        label = "Service Name", icon = Icons.Default.Label
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    EditCustomInput(
                        value = price, onValueChange = { if(it.all { c -> c.isDigit() }) price = it },
                        label = "Monthly Price", icon = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Picker
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2C2C2C))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFFBB86FC))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Billing Date", color = Color.Gray, fontSize = 12.sp)
                                Text("Every $selectedDate of month", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Theme Color", color = Color.Gray, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Color Picker
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(colors) { colorHex ->
                            val color = Color(parseColor(colorHex))
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColorHex = colorHex }
                                    .border(if (selectedColorHex == colorHex) 3.dp else 0.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedColorHex == colorHex) Icon(Icons.Default.Check, null, tint = ColorHelper.getContentColor(color))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // TOMBOL ACTION
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Tombol Delete (Merah)
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCF6679).copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCF6679))
                        ) {
                            Icon(Icons.Default.Delete, null, tint = Color(0xFFCF6679))
                        }

                        // Tombol Save (Ungu)
                        Button(
                            onClick = {
                                if (name.isNotEmpty() && price.isNotEmpty()) {
                                    val updatedSub = Subscription(
                                        id = subscriptionId,
                                        userId = currentUserId,
                                        name = name,
                                        price = price.toDoubleOrNull() ?: 0.0,
                                        billingDate = selectedDate,
                                        colorHex = selectedColorHex
                                    )
                                    scope.launch {
                                        repository.updateSubscription(updatedSub)
                                        onNavigateBack()
                                    }
                                }
                            },
                            modifier = Modifier.weight(3f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Komponen Input Helper
@Composable
fun EditCustomInput(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, keyboardType: KeyboardType = KeyboardType.Text) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(icon, null, tint = Color(0xFFBB86FC)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2C2C2C),
            unfocusedContainerColor = Color(0xFF2C2C2C),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}