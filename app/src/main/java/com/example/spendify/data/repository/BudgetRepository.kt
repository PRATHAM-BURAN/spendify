package com.example.spendify.data.repository

import com.example.spendify.data.local.dao.BudgetDao
import com.example.spendify.data.local.dao.TransactionDao
import com.example.spendify.data.local.entity.BudgetEntity
import com.example.spendify.data.remote.FirestoreService
import com.example.spendify.domain.model.Budget
import com.example.spendify.domain.model.BudgetProgress
import com.example.spendify.domain.model.BudgetScope
import com.example.spendify.domain.model.BudgetStatus
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val transactionDao: TransactionDao,
    private val firestoreService: FirestoreService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    fun getAllBudgetsFlow(): Flow<List<Budget>> {
        return budgetDao.getAllBudgetsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getOverallBudgetFlow(): Flow<Budget?> {
        return budgetDao.getOverallBudgetFlow().map { it?.toDomain() }
    }

    fun getBudgetProgressFlow(): Flow<List<BudgetProgress>> {
        val (startOfMonth, endOfMonth) = DateUtils.getCurrentMonthRange()
        return combine(
            budgetDao.getAllBudgetsFlow(),
            transactionDao.getTransactionsByDateRangeFlow(startOfMonth, endOfMonth)
        ) { budgetEntities, transactionEntities ->
            val transactions = transactionEntities.map { it.toDomain() }
            val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

            budgetEntities.map { entity ->
                val budget = entity.toDomain()
                val spent = if (budget.scope == BudgetScope.OVERALL) {
                    expenseTransactions.sumOf { it.amount }
                } else {
                    expenseTransactions
                        .filter { it.categoryId == budget.categoryId }
                        .sumOf { it.amount }
                }

                val remaining = (budget.limitAmount - spent).coerceAtLeast(0.0)
                val percentage = if (budget.limitAmount > 0) {
                    (spent / budget.limitAmount).toFloat()
                } else {
                    0f
                }

                val status = when {
                    percentage >= 1.0f -> BudgetStatus.DANGER
                    percentage >= 0.8f -> BudgetStatus.WARNING
                    else -> BudgetStatus.SAFE
                }

                BudgetProgress(
                    budget = budget,
                    spentAmount = spent,
                    remainingAmount = remaining,
                    percentage = percentage,
                    status = status
                )
            }
        }
    }

    suspend fun insertBudget(budget: Budget, userId: String) {
        budgetDao.insertBudget(BudgetEntity.fromDomain(budget))
        scope.launch {
            firestoreService.saveBudget(userId, budget)
        }
    }

    suspend fun updateBudget(budget: Budget, userId: String) {
        budgetDao.updateBudget(BudgetEntity.fromDomain(budget))
        scope.launch {
            firestoreService.saveBudget(userId, budget)
        }
    }

    suspend fun deleteBudget(budget: Budget, userId: String) {
        budgetDao.deleteBudgetById(budget.id)
        scope.launch {
            firestoreService.deleteBudget(userId, budget.id)
        }
    }

    suspend fun getOverallBudget(): Budget? {
        return budgetDao.getOverallBudget()?.toDomain()
    }
}
