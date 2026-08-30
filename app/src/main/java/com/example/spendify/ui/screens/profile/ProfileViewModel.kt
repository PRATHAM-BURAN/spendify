package com.example.spendify.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val totalTransactionsCount: Int = 0,
    val totalSpent: Double = 0.0,
    val totalIncome: Double = 0.0,
    val isLoading: Boolean = false,
    val isSignedOut: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            authRepository.getUserProfileFlow().collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
            }
        }

        viewModelScope.launch {
            transactionRepository.getAllTransactionsFlow().collect { txs ->
                val expenseTotal = txs.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
                val incomeTotal = txs.filter { it.type.name == "INCOME" }.sumOf { it.amount }
                _uiState.update {
                    it.copy(
                        totalTransactionsCount = txs.size,
                        totalSpent = expenseTotal,
                        totalIncome = incomeTotal
                    )
                }
            }
        }
    }

    fun updateDisplayName(newName: String) {
        val current = _uiState.value.userProfile ?: return
        val updated = current.copy(displayName = newName.trim())
        viewModelScope.launch {
            authRepository.updateProfile(updated)
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update { it.copy(isSignedOut = true) }
            onSuccess()
        }
    }
}
