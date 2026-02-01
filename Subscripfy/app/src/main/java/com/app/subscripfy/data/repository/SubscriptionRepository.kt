package com.app.subscripfy.data.repository

import android.util.Log
import com.app.subscripfy.data.model.Subscription
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SubscriptionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection: CollectionReference = firestore.collection("subscriptions")

    // READ
    fun getSubscriptions(userId: String): Flow<List<Subscription>> = callbackFlow {
        val query = collection
            .whereEqualTo("userId", userId)
        // .orderBy("billingDate", Query.Direction.ASCENDING) // <--- MATIKAN BARIS INI (BERI KOMENTAR //)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Jika error, biasanya karena butuh index
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val subs = snapshot.toObjects(Subscription::class.java)
                trySend(subs)
            }
        }
        awaitClose { listener.remove() }
    }

    // CREATE
    suspend fun addSubscription(subscription: Subscription) {
        try {
            val document = collection.document()
            val newSub = subscription.copy(id = document.id)
            document.set(newSub).await()
            Log.d("REPO", "Berhasil simpan data: ${newSub.name} untuk UserID: ${newSub.userId}")
        } catch (e: Exception) {
            Log.e("REPO", "Gagal simpan data: ${e.message}")
            throw e
        }
    }

    // UPDATE
    suspend fun updateSubscription(subscription: Subscription) {
        try {
            collection.document(subscription.id).set(subscription).await()
            Log.d("REPO", "Berhasil update: ${subscription.name}")
        } catch (e: Exception) {
            Log.e("REPO", "Gagal update: ${e.message}")
        }
    }

    // DELETE
    suspend fun deleteSubscription(subId: String) {
        try {
            collection.document(subId).delete().await()
            Log.d("REPO", "Berhasil hapus ID: $subId")
        } catch (e: Exception) {
            Log.e("REPO", "Gagal hapus: ${e.message}")
        }
    }

    // GET BY ID
    suspend fun getSubscriptionById(id: String): Subscription? {
        val snapshot = collection.document(id).get().await()
        return snapshot.toObject(Subscription::class.java)
    }

    suspend fun saveBudgetLimit(userId: String, amount: Double) {
        try {
            val data = hashMapOf("budgetLimit" to amount)
            // Simpan di dokumen khusus user tersebut
            firestore.collection("user_settings").document(userId).set(data).await()
            Log.d("REPO", "Budget berhasil disimpan: $amount")
        } catch (e: Exception) {
            Log.e("REPO", "Gagal simpan budget: ${e.message}")
        }
    }

    // [FITUR BARU] Ambil Budget dari Firestore
    suspend fun getBudgetLimit(userId: String): Double {
        return try {
            val snapshot = firestore.collection("user_settings").document(userId).get().await()
            // Jika ada data, ambil. Jika tidak ada, kembalikan default 500.000
            snapshot.getDouble("budgetLimit") ?: 500000.0
        } catch (e: Exception) {
            Log.e("REPO", "Gagal ambil budget: ${e.message}")
            500000.0 // Default jika error
        }
    }
}