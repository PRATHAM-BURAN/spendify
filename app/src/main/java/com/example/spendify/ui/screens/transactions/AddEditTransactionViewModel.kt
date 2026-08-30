package com.example.spendify.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.CategoryRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.domain.model.Category
import com.example.spendify.domain.model.DefaultCategories
import com.example.spendify.domain.model.PaymentMethod
import com.example.spendify.domain.model.RecurrenceFrequency
import com.example.spendify.domain.model.Transaction
import com.example.spendify.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AddEditTransactionUiState(
    val transactionId: String? = null,
    val amountString: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: Category? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.CARD,
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurrenceFrequency: RecurrenceFrequency = RecurrenceFrequency.MONTHLY,
    val categories: List<Category> = emptyList(),
    val currencySymbol: String = "$",
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddEditTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTransactionUiState())
    val uiState: StateFlow<AddEditTransactionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategoriesFlow().collect { allCategories ->
                val profile = authRepository.getUserProfile()
                _uiState.update { current ->
                    val filtered = allCategories.filter { it.isIncome == (current.type == TransactionType.INCOME) }
                    val defaultCat = current.selectedCategory ?: filtered.firstOrNull() ?: DefaultCategories.expenseCategories.first()
                    current.copy(
                        categories = filtered,
                        selectedCategory = defaultCat,
                        currencySymbol = profile.currencySymbol
                    )
                }
            }
        }
    }

    fun loadTransaction(transactionId: String) {
        viewModelScope.launch {
            val tx = transactionRepository.getTransactionById(transactionId) ?: return@launch
            val category = categoryRepository.getCategoryById(tx.categoryId)
            _uiState.update {
                it.copy(
                    transactionId = tx.id,
                    amountString = if (tx.amount % 1.0 == 0.0) tx.amount.toInt().toString() else String.format("%.2f", tx.amount),
                    type = tx.type,
                    selectedCategory = category,
                    paymentMethod = tx.paymentMethod,
                    dateMillis = tx.dateMillis,
                    note = tx.note,
                    isRecurring = tx.isRecurring,
                    recurrenceFrequency = tx.recurrenceFrequency ?: RecurrenceFrequency.MONTHLY
                )
            }
        }
    }

    fun onAmountChanged(newAmount: String) {
        // Sanitize numeric input
        val clean = newAmount.filter { it.isDigit() || it == '.' }
        val parts = clean.split('.')
        val formatted = if (parts.size > 2) {
            parts[0] + "." + parts.subList(1, parts.size).joinToString("")
        } else {
            clean
        }
        _uiState.update { it.copy(amountString = formatted, errorMessage = null) }
    }

    fun onTypeChanged(type: TransactionType) {
        viewModelScope.launch {
            categoryRepository.getAllCategoriesFlow().collect { allCategories ->
                val filtered = allCategories.filter { it.isIncome == (type == TransactionType.INCOME) }
                _uiState.update { current ->
                    current.copy(
                        type = type,
                        categories = filtered,
                        selectedCategory = filtered.firstOrNull()
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun onDateChanged(millis: Long) {
        _uiState.update { it.copy(dateMillis = millis) }
    }

    fun onNoteChanged(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onRecurringToggled(enabled: Boolean) {
        _uiState.update { it.copy(isRecurring = enabled) }
    }

    fun onFrequencySelected(frequency: RecurrenceFrequency) {
        _uiState.update { it.copy(recurrenceFrequency = frequency) }
    }

    fun saveTransaction() {
        val state = _uiState.value
        val amount = state.amountString.toDoubleOrNull() ?: 0.0

        if (amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid amount greater than 0") }
            return
        }

        val category = state.selectedCategory ?: DefaultCategories.expenseCategories.first()

        val transaction = Transaction(
            id = state.transactionId ?: "tx_${UUID.randomUUID()}",
            amount = amount,
            type = state.type,
            categoryId = category.id,
            categoryName = category.name,
            categoryIcon = category.iconName,
            categoryColor = category.colorHex,
            dateMillis = state.dateMillis,
            paymentMethod = state.paymentMethod,
            note = state.note.trim(),
            isRecurring = state.isRecurring,
            recurrenceFrequency = if (state.isRecurring) state.recurrenceFrequency else null,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            if (state.transactionId == null) {
                transactionRepository.insertTransaction(transaction, userId)
            } else {
                transactionRepository.updateTransaction(transaction, userId)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
