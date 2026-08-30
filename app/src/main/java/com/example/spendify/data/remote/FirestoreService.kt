package com.example.spendify.data.remote

import com.example.spendify.domain.model.Budget
import com.example.spendify.domain.model.Category
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    // --- Transactions ---

    suspend fun saveTransaction(userId: String, transaction: Transaction): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(userId)
                ?.collection("transactions")
                ?.document(transaction.id)
                ?.set(transactionToMap(transaction), SetOptions.merge())
                ?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteTransaction(userId: String, transactionId: String): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(userId)
                ?.collection("transactions")
                ?.document(transactionId)
                ?.delete()
                ?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getTransactionsFlow(userId: String): Flow<List<Transaction>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("users")
            .document(userId)
            .collection("transactions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val transactions = snapshot.documents.mapNotNull { doc ->
                        mapToTransaction(doc.id, doc.data ?: emptyMap())
                    }
                    trySend(transactions)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    // --- Categories ---

    suspend fun saveCategory(userId: String, category: Category): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(userId)
                ?.collection("categories")
                ?.document(category.id)
                ?.set(categoryToMap(category), SetOptions.merge())
                ?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteCategory(userId: String, categoryId: String): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(userId)
                ?.collection("categories")
                ?.document(categoryId)
                ?.delete()
                ?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- Budgets ---

    suspend fun saveBudget(userId: String, budget: Budget): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(userId)
                ?.collection("budgets")
                ?.document(budget.id)
                ?.set(budgetToMap(budget), SetOptions.merge())
                ?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteBudget(userId: String, budgetId: String): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(userId)
                ?.collection("budgets")
                ?.document(budgetId)
                ?.delete()
                ?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- Profile & Settings ---

    suspend fun saveProfile(profile: UserProfile): Boolean {
        return try {
            firestore?.collection("users")
                ?.document(profile.userId)
                ?.set(profileToMap(profile), SetOptions.merge())
                ?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // --- Mapping Helpers ---

    private fun transactionToMap(t: Transaction): Map<String, Any?> = mapOf(
        "id" to t.id,
        "amount" to t.amount,
        "type" to t.type.name,
        "categoryId" to t.categoryId,
        "categoryName" to t.categoryName,
        "categoryIcon" to t.categoryIcon,
        "categoryColor" to t.categoryColor,
        "dateMillis" to t.dateMillis,
        "paymentMethod" to t.paymentMethod.name,
        "note" to t.note,
        "isRecurring" to t.isRecurring,
        "recurrenceFrequency" to t.recurrenceFrequency?.name,
        "createdAt" to t.createdAt
    )

    private fun mapToTransaction(id: String, map: Map<String, Any>): Transaction? {
        return try {
            Transaction(
                id = id,
                amount = (map["amount"] as? Number)?.toDouble() ?: 0.0,
                type = com.example.spendify.domain.model.TransactionType.valueOf(map["type"] as? String ?: "EXPENSE"),
                categoryId = map["categoryId"] as? String ?: "",
                categoryName = map["categoryName"] as? String ?: "",
                categoryIcon = map["categoryIcon"] as? String ?: "restaurant",
                categoryColor = map["categoryColor"] as? String ?: "#4edea3",
                dateMillis = (map["dateMillis"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                paymentMethod = com.example.spendify.domain.model.PaymentMethod.valueOf(map["paymentMethod"] as? String ?: "CARD"),
                note = map["note"] as? String ?: "",
                isRecurring = map["isRecurring"] as? Boolean ?: false,
                recurrenceFrequency = (map["recurrenceFrequency"] as? String)?.let {
                    com.example.spendify.domain.model.RecurrenceFrequency.valueOf(it)
                },
                createdAt = (map["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun categoryToMap(c: Category): Map<String, Any?> = mapOf(
        "id" to c.id,
        "name" to c.name,
        "iconName" to c.iconName,
        "colorHex" to c.colorHex,
        "isCustom" to c.isCustom,
        "isIncome" to c.isIncome,
        "orderIndex" to c.orderIndex
    )

    private fun budgetToMap(b: Budget): Map<String, Any?> = mapOf(
        "id" to b.id,
        "scope" to b.scope.name,
        "categoryId" to b.categoryId,
        "categoryName" to b.categoryName,
        "limitAmount" to b.limitAmount,
        "period" to b.period.name,
        "startDateMillis" to b.startDateMillis
    )

    private fun profileToMap(p: UserProfile): Map<String, Any?> = mapOf(
        "userId" to p.userId,
        "email" to p.email,
        "displayName" to p.displayName,
        "photoUrl" to p.photoUrl,
        "currencyCode" to p.currencyCode,
        "currencySymbol" to p.currencySymbol,
        "themeMode" to p.themeMode.name,
        "budgetAlertEnabled" to p.budgetAlertEnabled,
        "budgetAlertThreshold" to p.budgetAlertThreshold,
        "recurringRemindersEnabled" to p.recurringRemindersEnabled
    )
}
