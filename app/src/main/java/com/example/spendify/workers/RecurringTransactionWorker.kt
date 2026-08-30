package com.example.spendify.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spendify.data.local.AppDatabase
import com.example.spendify.data.local.entity.TransactionEntity
import com.example.spendify.domain.model.RecurrenceFrequency
import com.example.spendify.domain.model.SyncStatus
import com.example.spendify.util.NotificationHelper
import java.util.Calendar
import java.util.UUID

class RecurringTransactionWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val recurringList = database.transactionDao().getRecurringTransactions()
            val now = System.currentTimeMillis()
            var generatedCount = 0

            for (item in recurringList) {
                val freq = item.recurrenceFrequency?.let {
                    try { RecurrenceFrequency.valueOf(it) } catch (e: Exception) { null }
                } ?: continue

                val lastDate = item.dateMillis
                val isDue = checkIfDue(lastDate, now, freq)

                if (isDue) {
                    val newTransaction = item.copy(
                        id = "rec_${UUID.randomUUID()}",
                        dateMillis = now,
                        createdAt = now,
                        syncStatus = SyncStatus.PENDING_SYNC.name
                    )
                    database.transactionDao().insertTransaction(newTransaction)
                    generatedCount++
                }
            }

            if (generatedCount > 0) {
                NotificationHelper.showRecurringGeneratedNotification(
                    applicationContext,
                    1001,
                    "Recurring Expenses Generated",
                    "$generatedCount recurring transaction(s) have been automatically recorded."
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun checkIfDue(lastMillis: Long, currentMillis: Long, frequency: RecurrenceFrequency): Boolean {
        val lastCal = Calendar.getInstance().apply { timeInMillis = lastMillis }
        val currentCal = Calendar.getInstance().apply { timeInMillis = currentMillis }

        return when (frequency) {
            RecurrenceFrequency.DAILY -> {
                currentMillis - lastMillis >= 24 * 60 * 60 * 1000L
            }
            RecurrenceFrequency.WEEKLY -> {
                currentMillis - lastMillis >= 7 * 24 * 60 * 60 * 1000L
            }
            RecurrenceFrequency.MONTHLY -> {
                val monthsDiff = (currentCal.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR)) * 12 +
                        (currentCal.get(Calendar.MONTH) - lastCal.get(Calendar.MONTH))
                monthsDiff >= 1
            }
            RecurrenceFrequency.YEARLY -> {
                currentCal.get(Calendar.YEAR) > lastCal.get(Calendar.YEAR)
            }
        }
    }
}
