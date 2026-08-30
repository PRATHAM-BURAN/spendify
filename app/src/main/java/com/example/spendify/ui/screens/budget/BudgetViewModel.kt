package com.example.spendify.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.BudgetRepository
import com.example.spendify.data.repository.CategoryRepository
import com.example.spendify.domain.model.Budget
import com.example.spendify.domain.model.BudgetPeriod
import com.example.spendify.domain.model.BudgetProgress
import com.example.spendify.domain.model.BudgetScope
import com.example.spendify.domain.model.Category
import com.example.spendify.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class BudgetUiState(
    val overallBudget: BudgetProgress? = null,
    val categoryBudgets: List<BudgetProgress> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val userProfile: UserProfile? = null,
    val isAddBudgetDialogOpen: Boolean = false,
    val editingBudget: Budget? = null,
    val isLoading: Boolean = false
)

class BudgetViewModel(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAddBudgetDialogOpen = MutableStateFlow(false)
    private val _editingBudget = MutableStateFlow<Budget?>(null)

    val uiState: StateFlow<BudgetUiState> = combine(
        budgetRepository.getBudgetProgressFlow(),
        categoryRepository.getAllCategoriesFlow(),
        authRepository.getUserProfileFlow(),
        _isAddBudgetDialogOpen,
        _editingBudget
    ) { progressList, categories, profile, isDialogOpen, editingBudget ->
        val overall = progressList.find { it.budget.scope == BudgetScope.OVERALL }
        val categoryBudgets = progressList.filter { it.budget.scope == BudgetScope.CATEGORY }
        val expenseCategories = categories.filter { !it.isIncome }

        BudgetUiState(
            overallBudget = overall,
            categoryBudgets = categoryBudgets,
            availableCategories = expenseCategories,
            userProfile = profile,
            isAddBudgetDialogOpen = isDialogOpen,
            editingBudget = editingBudget,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState(isLoading = true)
    )

    fun openAddBudgetDialog(budgetToEdit: Budget? = null) {
        _editingBudget.value = budgetToEdit
        _isAddBudgetDialogOpen.value = true
    }

    fun closeAddBudgetDialog() {
        _editingBudget.value = null
        _isAddBudgetDialogOpen.value = false
    }

    fun saveBudget(
        scope: BudgetScope,
        category: Category?,
        limitAmount: Double,
        period: BudgetPeriod
    ) {
        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            val currentEditing = _editingBudget.value

            val budget = Budget(
                id = currentEditing?.id ?: "budget_${UUID.randomUUID()}",
                scope = scope,
                categoryId = if (scope == BudgetScope.CATEGORY) category?.id else null,
                categoryName = if (scope == BudgetScope.CATEGORY) category?.name else null,
                limitAmount = limitAmount,
                period = period,
                startDateMillis = currentEditing?.startDateMillis ?: System.currentTimeMillis()
            )

            if (currentEditing == null) {
                budgetRepository.insertBudget(budget, userId)
            } else {
                budgetRepository.updateBudget(budget, userId)
            }
            closeAddBudgetDialog()
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            budgetRepository.deleteBudget(budget, userId)
        }
    }
}
