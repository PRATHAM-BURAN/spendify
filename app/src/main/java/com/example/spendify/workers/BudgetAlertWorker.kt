package com.example.spendify.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.spendify.data.local.AppDatabase
import com.example.spendify.domain.model.BudgetScope
import com.example.spendify.domain.model.BudgetStatus
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.util.CurrencyFormatter
import com.example.spendify.util.DateUtils
import com.example.spendify.util.NotificationHelper

class BudgetAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val profile = database.userProfileDao().getUserProfile()

            if (profile != null && !profile.budgetAlertEnabled) {
                return Result.success()
            }

            val (startOfMonth, endOfMonth) = DateUtils.getCurrentMonthRange()
            val transactions = database.transactionDao().getTransactionsByDateRange(startOfMonth, endOfMonth)
            val expenseTransactions = transactions.filter { it.type == "EXPENSE" }
            val budgets = database.budgetDao().getAllBudgets()

            val currencySymbol = profile?.currencySymbol ?: "$"

            for ((index, budgetEntity) in budgets.withIndex()) {
                val budget = budgetEntity.toDomain()
                val spent = if (budget.scope == BudgetScope.OVERALL) {
                    expenseTransactions.sumOf { it.amount }
                } else {
                    expenseTransactions
                        .filter { it.categoryId == budget.categoryId }
                        .sumOf { it.amount }
                }

                val percentage = if (budget.limitAmount > 0) (spent / budget.limitAmount).toFloat() else 0f

                if (percentage >= 1.0f) {
                    val name = budget.categoryName ?: "Monthly Budget"
                    NotificationHelper.showBudgetAlertNotification(
                        applicationContext,
                        2000 + index,
                        "⚠️ Budget Exceeded: $name",
                        "You have spent ${CurrencyFormatter.format(spent, currencySymbol)} of your ${CurrencyFormatter.format(budget.limitAmount, currencySymbol)} limit."
                    )
                } else if (percentage >= 0.8f) {
                    val name = budget.categoryName ?: "Monthly Budget"
                    NotificationHelper.showBudgetAlertNotification(
                        applicationContext,
                        2000 + index,
                        "⚡ Budget Warning: $name",
                        "You have reached ${(percentage * 100).toInt()}% of your ${CurrencyFormatter.format(budget.limitAmount, currencySymbol)} budget."
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
