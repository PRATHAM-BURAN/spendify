package com.example.spendify.ui.screens.charts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.CategoryRepository
import com.example.spendify.data.repository.ExportRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.domain.model.PeriodFilter
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.domain.model.UserProfile
import com.example.spendify.ui.components.CategoryIconHelper
import com.example.spendify.ui.components.PieChartData
import com.example.spendify.ui.components.TrendPoint
import com.example.spendify.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChartsUiState(
    val selectedPeriod: PeriodFilter = PeriodFilter.MONTHLY,
    val totalSpent: Double = 0.0,
    val totalEarned: Double = 0.0,
    val pieChartData: List<PieChartData> = emptyList(),
    val trendPoints: List<TrendPoint> = emptyList(),
    val currentPeriodTransactions: List<Transaction> = emptyList(),
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false
)

class ChartsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository,
    private val exportRepository: ExportRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(PeriodFilter.MONTHLY)

    val uiState: StateFlow<ChartsUiState> = combine(
        transactionRepository.getAllTransactionsFlow(),
        categoryRepository.getAllCategoriesFlow(),
        authRepository.getUserProfileFlow(),
        _selectedPeriod
    ) { transactions, categories, profile, period ->
        // Calculate date range
        val (startMillis, endMillis) = when (period) {
            PeriodFilter.WEEKLY -> DateUtils.getCurrentWeekRange()
            PeriodFilter.MONTHLY -> DateUtils.getCurrentMonthRange()
            PeriodFilter.YEARLY -> DateUtils.getCurrentYearRange()
            PeriodFilter.CUSTOM -> Pair(0L, Long.MAX_VALUE)
        }

        val filteredTransactions = transactions.filter { it.dateMillis in startMillis..endMillis }
        val expenseTransactions = filteredTransactions.filter { it.type == TransactionType.EXPENSE }
        val totalSpent = expenseTransactions.sumOf { it.amount }
        val totalEarned = filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

        // Category Pie Chart Segments
        val groupedByCategory = expenseTransactions.groupBy { it.categoryId }
        val pieData = groupedByCategory.mapNotNull { (catId, txs) ->
            val cat = categories.find { it.id == catId } ?: return@mapNotNull null
            val sum = txs.sumOf { it.amount }
            val percentage = if (totalSpent > 0) (sum / totalSpent).toFloat() else 0f
            PieChartData(
                categoryName = cat.name,
                amount = sum,
                color = CategoryIconHelper.parseColorHex(cat.colorHex),
                percentage = percentage
            )
        }.sortedByDescending { it.amount }

        // 6-Month Trend Points
        val past6Months = DateUtils.getPast6MonthsRanges()
        val trendPoints = past6Months.map { (label, start, end) ->
            val monthTxs = transactions.filter { it.dateMillis in start..end && it.type == TransactionType.EXPENSE }
            val monthSum = monthTxs.sumOf { it.amount }
            TrendPoint(label = label, value = monthSum)
        }

        ChartsUiState(
            selectedPeriod = period,
            totalSpent = totalSpent,
            totalEarned = totalEarned,
            pieChartData = pieData,
            trendPoints = trendPoints,
            currentPeriodTransactions = filteredTransactions,
            userProfile = profile,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChartsUiState(isLoading = true)
    )

    fun onPeriodSelected(period: PeriodFilter) {
        _selectedPeriod.value = period
    }

    fun exportCsv() {
        val txs = uiState.value.currentPeriodTransactions
        exportRepository.exportAndShareCsv(txs)
    }

    fun exportPdf() {
        val txs = uiState.value.currentPeriodTransactions
        val currencySymbol = uiState.value.userProfile?.currencySymbol ?: "$"
        exportRepository.exportAndSharePdf(txs, uiState.value.selectedPeriod.displayName, currencySymbol)
    }
}
