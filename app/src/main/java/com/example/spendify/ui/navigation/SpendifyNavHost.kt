package com.example.spendify.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.spendify.domain.model.UserProfile
import com.example.spendify.ui.components.BottomNavTab
import com.example.spendify.ui.components.SpendifyBottomNavBar
import com.example.spendify.ui.screens.auth.AuthScreen
import com.example.spendify.ui.screens.auth.AuthViewModel
import com.example.spendify.ui.screens.auth.OnboardingScreen
import com.example.spendify.ui.screens.budget.BudgetScreen
import com.example.spendify.ui.screens.budget.BudgetViewModel
import com.example.spendify.ui.screens.calendar.CalendarScreen
import com.example.spendify.ui.screens.calendar.CalendarViewModel
import com.example.spendify.ui.screens.categories.CategoryViewModel
import com.example.spendify.ui.screens.categories.ManageCategoriesScreen
import com.example.spendify.ui.screens.charts.ChartsReportsScreen
import com.example.spendify.ui.screens.charts.ChartsViewModel
import com.example.spendify.ui.screens.dashboard.DashboardScreen
import com.example.spendify.ui.screens.dashboard.DashboardViewModel
import com.example.spendify.ui.screens.profile.ProfileScreen
import com.example.spendify.ui.screens.profile.ProfileViewModel
import com.example.spendify.ui.screens.settings.SettingsScreen
import com.example.spendify.ui.screens.settings.SettingsViewModel
import com.example.spendify.ui.screens.transactions.AddEditTransactionScreen
import com.example.spendify.ui.screens.transactions.AddEditTransactionViewModel
import com.example.spendify.ui.screens.transactions.TransactionHistoryScreen
import com.example.spendify.ui.screens.transactions.TransactionHistoryViewModel

sealed class AppDestination {
    object Onboarding : AppDestination()
    object Auth : AppDestination()
    object Dashboard : AppDestination()
    object History : AppDestination()
    object Charts : AppDestination()
    object Budget : AppDestination()
    object Calendar : AppDestination()
    object Categories : AppDestination()
    object Settings : AppDestination()
    object Profile : AppDestination()
    data class AddEditTransaction(val transactionId: String? = null) : AppDestination()
}

@Composable
fun SpendifyNavHost(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    historyViewModel: TransactionHistoryViewModel,
    addEditViewModelFactory: () -> AddEditTransactionViewModel,
    calendarViewModel: CalendarViewModel,
    budgetViewModel: BudgetViewModel,
    chartsViewModel: ChartsViewModel,
    categoryViewModel: CategoryViewModel,
    settingsViewModel: SettingsViewModel,
    profileViewModel: ProfileViewModel,
    modifier: Modifier = Modifier
) {
    val backStack = remember { mutableStateListOf<AppDestination>(AppDestination.Auth) }
    val currentDestination = backStack.lastOrNull() ?: AppDestination.Auth

    var currentTab by remember { mutableStateOf(BottomNavTab.DASHBOARD) }

    fun navigateTo(dest: AppDestination) {
        backStack.add(dest)
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }

    fun switchTab(tab: BottomNavTab) {
        currentTab = tab
        val dest = when (tab) {
            BottomNavTab.DASHBOARD -> AppDestination.Dashboard
            BottomNavTab.CALENDAR -> AppDestination.Calendar
            BottomNavTab.HISTORY -> AppDestination.History
            BottomNavTab.CHARTS -> AppDestination.Charts
            BottomNavTab.BUDGETS -> AppDestination.Budget
        }
        backStack.clear()
        backStack.add(dest)
    }

    val isBottomBarVisible = currentDestination is AppDestination.Dashboard ||
            currentDestination is AppDestination.Calendar ||
            currentDestination is AppDestination.History ||
            currentDestination is AppDestination.Charts ||
            currentDestination is AppDestination.Budget

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screenTransition"
        ) { destination ->
            when (destination) {
                is AppDestination.Onboarding -> {
                    OnboardingScreen(
                        onNavigateToAuth = {
                            backStack.clear()
                            backStack.add(AppDestination.Auth)
                        }
                    )
                }
                is AppDestination.Auth -> {
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = {
                            backStack.clear()
                            backStack.add(AppDestination.Dashboard)
                        },
                        onNavigateBack = if (backStack.size > 1) { { navigateBack() } } else null
                    )
                }
                is AppDestination.Dashboard -> {
                    currentTab = BottomNavTab.DASHBOARD
                    DashboardScreen(
                        viewModel = dashboardViewModel,
                        onNavigateToSettings = { navigateTo(AppDestination.Settings) },
                        onNavigateToProfile = { navigateTo(AppDestination.Profile) },
                        onNavigateToCalendar = { switchTab(BottomNavTab.CALENDAR) },
                        onNavigateToCharts = { switchTab(BottomNavTab.CHARTS) },
                        onNavigateToHistory = { switchTab(BottomNavTab.HISTORY) },
                        onNavigateToBudget = { switchTab(BottomNavTab.BUDGETS) },
                        onNavigateToAddEditTransaction = { txId -> navigateTo(AppDestination.AddEditTransaction(txId)) },
                        onNavigateToCategories = { navigateTo(AppDestination.Categories) }
                    )
                }
                is AppDestination.Calendar -> {
                    currentTab = BottomNavTab.CALENDAR
                    CalendarScreen(
                        viewModel = calendarViewModel,
                        onNavigateToSettings = { navigateTo(AppDestination.Settings) },
                        onNavigateToProfile = { navigateTo(AppDestination.Profile) },
                        onNavigateToAddEditTransaction = { txId -> navigateTo(AppDestination.AddEditTransaction(txId)) }
                    )
                }
                is AppDestination.History -> {
                    currentTab = BottomNavTab.HISTORY
                    TransactionHistoryScreen(
                        viewModel = historyViewModel,
                        onNavigateToSettings = { navigateTo(AppDestination.Settings) },
                        onNavigateToProfile = { navigateTo(AppDestination.Profile) },
                        onNavigateToAddEditTransaction = { txId -> navigateTo(AppDestination.AddEditTransaction(txId)) }
                    )
                }
                is AppDestination.Charts -> {
                    currentTab = BottomNavTab.CHARTS
                    ChartsReportsScreen(
                        viewModel = chartsViewModel,
                        onNavigateToSettings = { navigateTo(AppDestination.Settings) },
                        onNavigateToProfile = { navigateTo(AppDestination.Profile) }
                    )
                }
                is AppDestination.Budget -> {
                    currentTab = BottomNavTab.BUDGETS
                    BudgetScreen(
                        viewModel = budgetViewModel,
                        onNavigateToSettings = { navigateTo(AppDestination.Settings) },
                        onNavigateToProfile = { navigateTo(AppDestination.Profile) }
                    )
                }
                is AppDestination.Categories -> {
                    ManageCategoriesScreen(
                        viewModel = categoryViewModel,
                        onNavigateBack = { navigateBack() }
                    )
                }
                is AppDestination.Settings -> {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateBack = { navigateBack() },
                        onNavigateToCategories = { navigateTo(AppDestination.Categories) },
                        onNavigateToProfile = { navigateTo(AppDestination.Profile) },
                        onLogoutSuccess = {
                            authViewModel.resetAuthState()
                            backStack.clear()
                            backStack.add(AppDestination.Auth)
                        }
                    )
                }
                is AppDestination.Profile -> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onNavigateBack = { navigateBack() },
                        onNavigateToSettings = { navigateTo(AppDestination.Settings) },
                        onNavigateToCategories = { navigateTo(AppDestination.Categories) },
                        onLogoutSuccess = {
                            authViewModel.resetAuthState()
                            backStack.clear()
                            backStack.add(AppDestination.Auth)
                        }
                    )
                }
                is AppDestination.AddEditTransaction -> {
                    val addEditViewModel = remember(destination.transactionId) { addEditViewModelFactory() }
                    AddEditTransactionScreen(
                        viewModel = addEditViewModel,
                        transactionId = destination.transactionId,
                        onNavigateBack = { navigateBack() },
                        onNavigateToCategories = { navigateTo(AppDestination.Categories) }
                    )
                }
            }
        }

        // Floating Bottom Nav Bar on Main Tabs
        if (isBottomBarVisible) {
            SpendifyBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { tab -> switchTab(tab) },
                onFabClick = { navigateTo(AppDestination.AddEditTransaction(null)) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
