package com.example.spendify.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.BudgetRepository
import com.example.spendify.data.repository.CategoryRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.domain.model.BudgetProgress
import com.example.spendify.domain.model.BudgetStatus
import com.example.spendify.domain.model.Category
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.domain.model.UserProfile
import com.example.spendify.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TopCategorySpend(
    val category: Category,
    val totalSpent: Double
)

data class DashboardUiState(
    val userProfile: UserProfile? = null,
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val overallBudget: BudgetProgress? = null,
    val topCategories: List<TopCategorySpend> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false
)

class DashboardViewModel(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        transactionRepository.getAllTransactionsFlow(),
        budgetRepository.getBudgetProgressFlow(),
        categoryRepository.getAllCategoriesFlow(),
        authRepository.getUserProfileFlow()
    ) { transactions, budgetProgressList, categories, profile ->
        val (startOfMonth, endOfMonth) = DateUtils.getCurrentMonthRange()
        val currentMonthTransactions = transactions.filter { it.dateMillis in startOfMonth..endOfMonth }

        val monthlyIncome = currentMonthTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val monthlyExpense = currentMonthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val totalBalance = monthlyIncome - monthlyExpense

        // Overall budget progress
        val overallBudget = budgetProgressList.find { it.budget.scope == com.example.spendify.domain.model.BudgetScope.OVERALL }
            ?: budgetProgressList.firstOrNull()

        // Top categories calculation
        val expenseTransactions = currentMonthTransactions.filter { it.type == TransactionType.EXPENSE }
        val categorySpends = expenseTransactions
            .groupBy { it.categoryId }
            .mapNotNull { (catId, txs) ->
                val category = categories.find { it.id == catId } ?: return@mapNotNull null
                TopCategorySpend(category = category, totalSpent = txs.sumOf { it.amount })
            }
            .sortedByDescending { it.totalSpent }
            .take(6)

        // Fallback default category chips if no transactions
        val displayTopCategories = if (categorySpends.isEmpty()) {
            categories.filter { !it.isIncome }.take(4).map { TopCategorySpend(it, 0.0) }
        } else {
            categorySpends
        }

        val recentTransactions = transactions.take(6)

        DashboardUiState(
            userProfile = profile,
            totalBalance = totalBalance,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            overallBudget = overallBudget,
            topCategories = displayTopCategories,
            recentTransactions = recentTransactions,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(isLoading = true)
    )

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            transactionRepository.deleteTransaction(transaction, userId)
        }
    }
}
