package com.example.spendify.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.TransactionType
import com.example.spendify.domain.model.UserProfile
import com.example.spendify.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class CalendarDay(
    val dayOfMonth: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val hasTransactions: Boolean = false
)

data class CalendarUiState(
    val currentMonthYearString: String = "",
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedDateMillis: Long = System.currentTimeMillis(),
    val selectedDateTransactions: List<Transaction> = emptyList(),
    val selectedDateExpenseTotal: Double = 0.0,
    val selectedDateIncomeTotal: Double = 0.0,
    val userProfile: UserProfile? = null
)

class CalendarViewModel(
    private val transactionRepository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentCalendar = MutableStateFlow(Calendar.getInstance())
    private val _selectedDateMillis = MutableStateFlow(System.currentTimeMillis())

    val uiState: StateFlow<CalendarUiState> = combine(
        transactionRepository.getAllTransactionsFlow(),
        authRepository.getUserProfileFlow(),
        _currentCalendar,
        _selectedDateMillis
    ) { transactions, profile, currentCal, selectedMillis ->
        val monthYearStr = DateUtils.formatMonthYear(currentCal.timeInMillis)

        // Build days for month grid
        val days = buildMonthDays(currentCal, selectedMillis, transactions)

        // Filter transactions for selected date
        val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedMillis }
        val selectedDateTxs = transactions.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
            DateUtils.isSameDay(txCal, selectedCal)
        }

        val expenseTotal = selectedDateTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val incomeTotal = selectedDateTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

        CalendarUiState(
            currentMonthYearString = monthYearStr,
            calendarDays = days,
            selectedDateMillis = selectedMillis,
            selectedDateTransactions = selectedDateTxs,
            selectedDateExpenseTotal = expenseTotal,
            selectedDateIncomeTotal = incomeTotal,
            userProfile = profile
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    fun onDateSelected(dateMillis: Long) {
        _selectedDateMillis.value = dateMillis
    }

    fun previousMonth() {
        val cal = (_currentCalendar.value.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        _currentCalendar.value = cal
    }

    fun nextMonth() {
        val cal = (_currentCalendar.value.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        _currentCalendar.value = cal
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            transactionRepository.deleteTransaction(transaction, userId)
        }
    }

    private fun buildMonthDays(
        currentCal: Calendar,
        selectedMillis: Long,
        transactions: List<Transaction>
    ): List<CalendarDay> {
        val list = mutableListOf<CalendarDay>()
        val cal = (currentCal.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val today = Calendar.getInstance()
        val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedMillis }

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday
        val offset = firstDayOfWeek - 1

        // Previous month filler days
        val prevCal = (cal.clone() as Calendar).apply {
            add(Calendar.MONTH, -1)
        }
        val maxDaysInPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in offset downTo 1) {
            val day = maxDaysInPrevMonth - i + 1
            prevCal.set(Calendar.DAY_OF_MONTH, day)
            val millis = prevCal.timeInMillis
            list.add(
                CalendarDay(
                    dayOfMonth = day,
                    dateMillis = millis,
                    isCurrentMonth = false,
                    isToday = DateUtils.isSameDay(prevCal, today),
                    isSelected = DateUtils.isSameDay(prevCal, selectedCal)
                )
            )
        }

        // Current month days
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDays) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val millis = cal.timeInMillis
            val dayTxs = transactions.filter { tx ->
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
                DateUtils.isSameDay(txCal, cal)
            }
            val expense = dayTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val income = dayTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

            list.add(
                CalendarDay(
                    dayOfMonth = day,
                    dateMillis = millis,
                    isCurrentMonth = true,
                    isToday = DateUtils.isSameDay(cal, today),
                    isSelected = DateUtils.isSameDay(cal, selectedCal),
                    totalExpense = expense,
                    totalIncome = income,
                    hasTransactions = dayTxs.isNotEmpty()
                )
            )
        }

        // Next month filler days to complete 35 or 42 grid cells
        val totalCells = if (list.size > 35) 42 else 35
        val nextCal = (cal.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }
        var nextDay = 1
        while (list.size < totalCells) {
            nextCal.set(Calendar.DAY_OF_MONTH, nextDay)
            list.add(
                CalendarDay(
                    dayOfMonth = nextDay,
                    dateMillis = nextCal.timeInMillis,
                    isCurrentMonth = false,
                    isToday = DateUtils.isSameDay(nextCal, today),
                    isSelected = DateUtils.isSameDay(nextCal, selectedCal)
                )
            )
            nextDay++
        }

        return list
    }
}
