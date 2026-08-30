package com.example.spendify.domain.model

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val dateMillis: Long,
    val paymentMethod: PaymentMethod,
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurrenceFrequency: RecurrenceFrequency? = null,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val createdAt: Long = System.currentTimeMillis()
)
