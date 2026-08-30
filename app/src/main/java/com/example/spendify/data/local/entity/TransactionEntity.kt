package com.example.spendify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendify.domain.model.PaymentMethod
import com.example.spendify.domain.model.RecurrenceFrequency
import com.example.spendify.domain.model.SyncStatus
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: String, // "EXPENSE", "INCOME"
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val dateMillis: Long,
    val paymentMethod: String, // "CARD", "CASH", "UPI", "BANK_TRANSFER"
    val note: String,
    val isRecurring: Boolean,
    val recurrenceFrequency: String?, // "DAILY", "WEEKLY", "MONTHLY", "YEARLY"
    val syncStatus: String, // "SYNCED", "PENDING_SYNC", "PENDING_DELETE"
    val createdAt: Long
) {
    fun toDomain(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            type = TransactionType.valueOf(type),
            categoryId = categoryId,
            categoryName = categoryName,
            categoryIcon = categoryIcon,
            categoryColor = categoryColor,
            dateMillis = dateMillis,
            paymentMethod = try {
                PaymentMethod.valueOf(paymentMethod)
            } catch (e: Exception) {
                PaymentMethod.CARD
            },
            note = note,
            isRecurring = isRecurring,
            recurrenceFrequency = recurrenceFrequency?.let {
                try {
                    RecurrenceFrequency.valueOf(it)
                } catch (e: Exception) {
                    null
                }
            },
            syncStatus = try {
                SyncStatus.valueOf(syncStatus)
            } catch (e: Exception) {
                SyncStatus.SYNCED
            },
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(domain: Transaction): TransactionEntity {
            return TransactionEntity(
                id = domain.id,
                amount = domain.amount,
                type = domain.type.name,
                categoryId = domain.categoryId,
                categoryName = domain.categoryName,
                categoryIcon = domain.categoryIcon,
                categoryColor = domain.categoryColor,
                dateMillis = domain.dateMillis,
                paymentMethod = domain.paymentMethod.name,
                note = domain.note,
                isRecurring = domain.isRecurring,
                recurrenceFrequency = domain.recurrenceFrequency?.name,
                syncStatus = domain.syncStatus.name,
                createdAt = domain.createdAt
            )
        }
    }
}
