package com.example.spendify.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.ExportRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.domain.model.CurrencyOption
import com.example.spendify.domain.model.ThemeMode
import com.example.spendify.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userProfile: UserProfile? = null,
    val isLoggedOut: Boolean = false,
    val isLoading: Boolean = false
)

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    private val exportRepository: ExportRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = authRepository.getUserProfileFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        ).let { flow ->
            val stateFlow = MutableStateFlow(SettingsUiState())
            viewModelScope.launch {
                flow.collect { profile ->
                    stateFlow.value = stateFlow.value.copy(userProfile = profile)
                }
            }
            stateFlow.asStateFlow()
        }

    fun onThemeChanged(themeMode: ThemeMode) {
        viewModelScope.launch {
            val current = authRepository.getUserProfile()
            authRepository.updateProfile(current.copy(themeMode = themeMode))
        }
    }

    fun onCurrencyChanged(currency: CurrencyOption) {
        viewModelScope.launch {
            val current = authRepository.getUserProfile()
            authRepository.updateProfile(
                current.copy(
                    currencyCode = currency.code,
                    currencySymbol = currency.symbol
                )
            )
        }
    }

    fun onBudgetAlertsToggled(enabled: Boolean) {
        viewModelScope.launch {
            val current = authRepository.getUserProfile()
            authRepository.updateProfile(current.copy(budgetAlertEnabled = enabled))
        }
    }

    fun onRecurringRemindersToggled(enabled: Boolean) {
        viewModelScope.launch {
            val current = authRepository.getUserProfile()
            authRepository.updateProfile(current.copy(recurringRemindersEnabled = enabled))
        }
    }

    fun exportAllDataCsv() {
        viewModelScope.launch {
            val transactions = transactionRepository.getAllTransactionsFlow().first()
            exportRepository.exportAndShareCsv(transactions)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
