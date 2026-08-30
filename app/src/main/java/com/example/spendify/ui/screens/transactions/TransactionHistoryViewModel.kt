package com.example.spendify.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.CategoryRepository
import com.example.spendify.data.repository.ExportRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.domain.model.Category
import com.example.spendify.domain.model.PeriodFilter
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.UserProfile
import com.example.spendify.ui.components.TransactionFilterState
import com.example.spendify.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DateGroupedTransactions(
    val header: String,
    val transactions: List<Transaction>
)

data class TransactionHistoryUiState(
    val searchQuery: String = "",
    val filterState: TransactionFilterState = TransactionFilterState(),
    val categories: List<Category> = emptyList(),
    val groupedTransactions: List<DateGroupedTransactions> = emptyList(),
    val filteredTransactionsCount: Int = 0,
    val userProfile: UserProfile? = null,
    val isFilterSheetOpen: Boolean = false,
    val isLoading: Boolean = false
)

private data class SearchFilterParams(
    val query: String,
    val filter: TransactionFilterState,
    val isFilterOpen: Boolean
)

class TransactionHistoryViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository,
    private val exportRepository: ExportRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filterState = MutableStateFlow(TransactionFilterState())
    private val _isFilterSheetOpen = MutableStateFlow(false)

    private val _searchFilterParams = combine(
        _searchQuery,
        _filterState,
        _isFilterSheetOpen
    ) { query, filter, isFilterOpen ->
        SearchFilterParams(query, filter, isFilterOpen)
    }

    val uiState: StateFlow<TransactionHistoryUiState> = combine(
        transactionRepository.getAllTransactionsFlow(),
        categoryRepository.getAllCategoriesFlow(),
        authRepository.getUserProfileFlow(),
        _searchFilterParams
    ) { allTransactions, categories, profile, params ->
        val query = params.query
        val filter = params.filter
        val isFilterOpen = params.isFilterOpen

        // Apply filters
        var filtered = allTransactions

        // 1. Text Search query
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtered = filtered.filter {
                it.note.lowercase().contains(q) ||
                        it.categoryName.lowercase().contains(q) ||
                        it.paymentMethod.displayName.lowercase().contains(q) ||
                        String.format("%.2f", it.amount).contains(q)
            }
        }

        // 2. Transaction Type
        if (filter.selectedType != null) {
            filtered = filtered.filter { it.type == filter.selectedType }
        }

        // 3. Category Filter
        if (filter.selectedCategoryIds.isNotEmpty()) {
            filtered = filtered.filter { filter.selectedCategoryIds.contains(it.categoryId) }
        }

        // 4. Payment Method Filter
        if (filter.selectedPaymentMethods.isNotEmpty()) {
            filtered = filtered.filter { filter.selectedPaymentMethods.contains(it.paymentMethod) }
        }

        // 5. Date Period Filter
        val (start, end) = when (filter.selectedPeriod) {
            PeriodFilter.WEEKLY -> DateUtils.getCurrentWeekRange()
            PeriodFilter.MONTHLY -> DateUtils.getCurrentMonthRange()
            PeriodFilter.YEARLY -> DateUtils.getCurrentYearRange()
            PeriodFilter.CUSTOM -> Pair(0L, Long.MAX_VALUE)
        }
        if (filter.selectedPeriod != PeriodFilter.CUSTOM) {
            filtered = filtered.filter { it.dateMillis in start..end }
        }

        // Group by date
        val groupedMap = filtered.groupBy { DateUtils.getDateGroupHeader(it.dateMillis) }
        val groupedList = groupedMap.map { (header, txs) ->
            DateGroupedTransactions(header = header, transactions = txs)
        }

        TransactionHistoryUiState(
            searchQuery = query,
            filterState = filter,
            categories = categories,
            groupedTransactions = groupedList,
            filteredTransactionsCount = filtered.size,
            userProfile = profile,
            isFilterSheetOpen = isFilterOpen,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionHistoryUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openFilterSheet() {
        _isFilterSheetOpen.value = true
    }

    fun closeFilterSheet() {
        _isFilterSheetOpen.value = false
    }

    fun applyFilter(filter: TransactionFilterState) {
        _filterState.value = filter
    }

    fun resetFilter() {
        _filterState.value = TransactionFilterState()
        _searchQuery.value = ""
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            transactionRepository.deleteTransaction(transaction, userId)
        }
    }

    fun exportFilteredCsv() {
        val transactions = uiState.value.groupedTransactions.flatMap { it.transactions }
        exportRepository.exportAndShareCsv(transactions)
    }

    fun exportFilteredPdf() {
        val transactions = uiState.value.groupedTransactions.flatMap { it.transactions }
        val currencySymbol = uiState.value.userProfile?.currencySymbol ?: "$"
        exportRepository.exportAndSharePdf(transactions, uiState.value.filterState.selectedPeriod.displayName, currencySymbol)
    }
}
