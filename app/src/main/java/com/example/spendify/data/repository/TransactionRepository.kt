package com.example.spendify.data.repository

import com.example.spendify.data.local.dao.TransactionDao
import com.example.spendify.data.local.entity.TransactionEntity
import com.example.spendify.data.remote.FirestoreService
import com.example.spendify.domain.model.SyncStatus
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val firestoreService: FirestoreService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    fun getAllTransactionsFlow(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactionsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTransactionsByDateRangeFlow(startMillis: Long, endMillis: Long): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRangeFlow(startMillis, endMillis).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getTransactionsByDateRange(startMillis: Long, endMillis: Long): List<Transaction> {
        return transactionDao.getTransactionsByDateRange(startMillis, endMillis).map { it.toDomain() }
    }

    suspend fun getTransactionById(id: String): Transaction? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }

    suspend fun getRecurringTransactions(): List<Transaction> {
        return transactionDao.getRecurringTransactions().map { it.toDomain() }
    }

    suspend fun insertTransaction(transaction: Transaction, userId: String) {
        val entity = TransactionEntity.fromDomain(transaction.copy(syncStatus = SyncStatus.PENDING_SYNC))
        transactionDao.insertTransaction(entity)

        // Asynchronously sync to Firestore
        scope.launch {
            val success = firestoreService.saveTransaction(userId, transaction)
            if (success) {
                transactionDao.updateTransaction(
                    entity.copy(syncStatus = SyncStatus.SYNCED.name)
                )
            }
        }
    }

    suspend fun updateTransaction(transaction: Transaction, userId: String) {
        val entity = TransactionEntity.fromDomain(transaction.copy(syncStatus = SyncStatus.PENDING_SYNC))
        transactionDao.updateTransaction(entity)

        scope.launch {
            val success = firestoreService.saveTransaction(userId, transaction)
            if (success) {
                transactionDao.updateTransaction(
                    entity.copy(syncStatus = SyncStatus.SYNCED.name)
                )
            }
        }
    }

    suspend fun deleteTransaction(transaction: Transaction, userId: String) {
        transactionDao.deleteTransactionById(transaction.id)

        scope.launch {
            firestoreService.deleteTransaction(userId, transaction.id)
        }
    }

    suspend fun syncWithFirestore(userId: String) {
        val pending = transactionDao.getPendingSyncTransactions()
        for (item in pending) {
            val domain = item.toDomain()
            val success = firestoreService.saveTransaction(userId, domain)
            if (success) {
                transactionDao.updateTransaction(item.copy(syncStatus = SyncStatus.SYNCED.name))
            }
        }
    }

    suspend fun clearLocalData() {
        transactionDao.clearAll()
    }
}
