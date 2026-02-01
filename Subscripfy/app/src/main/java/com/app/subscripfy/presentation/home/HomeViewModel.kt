package com.app.subscripfy.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.subscripfy.data.model.Subscription
import com.app.subscripfy.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = SubscriptionRepository()

    // Kita simpan userId di sini agar bisa dipakai saat nge-save budget
    private var currentUserId: String? = null

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State Budget Limit
    private val _budgetLimit = MutableStateFlow(500000.0)
    val budgetLimit: StateFlow<Double> = _budgetLimit.asStateFlow()

    // Load Data (Dipanggil saat Login/Buka Home)
    fun loadSubscriptions(userId: String) {
        currentUserId = userId // Simpan userId ke memori

        viewModelScope.launch {
            // 1. Load Subscription (Paralel)
            launch {
                repository.getSubscriptions(userId)
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        _isLoading.value = false
                        Log.e("HomeViewModel", "Error Load Subs: ${e.message}")
                    }
                    .collectLatest { list ->
                        _isLoading.value = false
                        _subscriptions.value = list
                        calculateTotal(list)
                    }
            }

            // 2. Load Budget Limit dari Firestore (PENTING!)
            launch {
                val savedLimit = repository.getBudgetLimit(userId)
                _budgetLimit.value = savedLimit
                Log.d("HomeViewModel", "Limit dimuat: $savedLimit")
            }
        }
    }

    private fun calculateTotal(list: List<Subscription>) {
        val total = list.sumOf { it.price }
        _totalExpense.value = total
    }

    fun deleteSubscription(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteSubscription(id)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Gagal hapus: ${e.message}")
            }
        }
    }

    // Update Budget (Simpan Permanen)
    fun setBudgetLimit(amount: Double) {
        // 1. Update tampilan UI dulu biar cepat
        _budgetLimit.value = amount

        // 2. Cek apakah User ID ada?
        val uid = currentUserId

        if (uid == null) {
            Log.e("BUDGET_ERROR", "GAWAT! User ID Kosong/Null. Data tidak akan disimpan ke database.")
            // Tips: Pastikan loadSubscriptions sudah terpanggil di HomeScreen
            return
        }

        // 3. Simpan ke Database
        viewModelScope.launch {
            Log.d("BUDGET_DEBUG", "Mencoba menyimpan $amount untuk User: $uid ke Firestore...")
            try {
                repository.saveBudgetLimit(uid, amount)
                Log.d("BUDGET_SUCCESS", "BERHASIL! Data tersimpan di Firestore.")
            } catch (e: Exception) {
                Log.e("BUDGET_ERROR", "GAGAL menyimpan ke Firestore: ${e.message}")
            }
        }
    }
}