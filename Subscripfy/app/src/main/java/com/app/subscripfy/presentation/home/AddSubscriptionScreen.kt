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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Label
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
fun AddSubscriptionScreen(userId: String, onNavigateBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(1) }
    var selectedColorHex by remember { mutableStateOf("#BB86FC") }

    var showDatePicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository = remember { SubscriptionRepository() }

    val colors = listOf("#E53935", "#D81B60", "#8E24AA", "#5E35B1", "#3949AB", "#1E88E5", "#039BE5", "#00ACC1", "#00897B", "#43A047", "#7CB342", "#FDD835", "#FB8C00", "#F4511E", "#6D4C41", "#757575", "#546E7A", "#212121")

    // Logic Date Picker (Sama seperti sebelumnya, disembunyikan utk hemat tempat di sini)
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { millis -> val cal = Calendar.getInstance(); cal.timeInMillis = millis; selectedDate = cal.get(Calendar.DAY_OF_MONTH) }; showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    // BACKGROUND ARTISTIK
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
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.background(Color.White.copy(alpha=0.1f), CircleShape)) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("New Subscription", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- PREVIEW CARD GLOWING ---
            val previewColor = try { Color(parseColor(selectedColorHex)) } catch (e: Exception) { Color.Blue }
            val contentColor = ColorHelper.getContentColor(previewColor)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(previewColor)
                    // Efek Glow sederhana (Border tebal transparan)
                    .border(4.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(name.ifEmpty { "Service Name" }, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Icon(Icons.Default.AttachMoney, null, tint = contentColor)
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

            // --- MODERN INPUT FIELDS ---
            // Name Input
            CustomDarkInput(
                value = name, onValueChange = { name = it },
                label = "Service Name",
                icon = Icons.Default.Label
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price Input
            CustomDarkInput(
                value = price, onValueChange = { if(it.all { c -> c.isDigit() }) price = it },
                label = "Monthly Price",
                icon = Icons.Default.AttachMoney,
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Selection
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

            // Color Picker
            Text("Accent Color", color = Color.Gray, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(colors) { colorHex ->
                    val color = Color(parseColor(colorHex))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColorHex = colorHex }
                            .border(if(selectedColorHex == colorHex) 3.dp else 0.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedColorHex == colorHex) Icon(Icons.Default.Check, null, tint = ColorHelper.getContentColor(color))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = { if (name.isNotEmpty() && price.isNotEmpty()) { scope.launch { repository.addSubscription(Subscription(userId = userId, name = name, price = price.toDoubleOrNull() ?: 0.0, billingDate = selectedDate, colorHex = selectedColorHex)); onNavigateBack() } } },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBB86FC)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("SAVE SUBSCRIPTION", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Komponen Input Custom agar tidak Kaku
@Composable
fun CustomDarkInput(value: String, onValueChange: (String) -> Unit, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, keyboardType: KeyboardType = KeyboardType.Text) {
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