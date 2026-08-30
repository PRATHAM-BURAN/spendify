package com.example.spendify.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.CategoryRepository
import com.example.spendify.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class CategoryUiState(
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val isAddDialogOpen: Boolean = false,
    val isLoading: Boolean = false
)

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isAddDialogOpen = MutableStateFlow(false)

    val uiState: StateFlow<CategoryUiState> = combine(
        categoryRepository.getAllCategoriesFlow(),
        _isAddDialogOpen
    ) { allCategories, isDialogOpen ->
        CategoryUiState(
            expenseCategories = allCategories.filter { !it.isIncome },
            incomeCategories = allCategories.filter { it.isIncome },
            isAddDialogOpen = isDialogOpen,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryUiState(isLoading = true)
    )

    fun openAddDialog() {
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
    }

    fun addCustomCategory(name: String, iconName: String, colorHex: String, isIncome: Boolean) {
        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            val category = Category(
                id = "custom_cat_${UUID.randomUUID()}",
                name = name.trim(),
                iconName = iconName,
                colorHex = colorHex,
                isCustom = true,
                isIncome = isIncome,
                orderIndex = 99
            )
            categoryRepository.insertCategory(category, userId)
            closeAddDialog()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            val userId = authRepository.getUserProfile().userId
            categoryRepository.deleteCategory(category, userId)
        }
    }
}
