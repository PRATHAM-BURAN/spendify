package com.example.spendify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.spendify.domain.model.ThemeMode
import com.example.spendify.ui.navigation.SpendifyNavHost
import com.example.spendify.ui.screens.auth.AuthViewModel
import com.example.spendify.ui.screens.budget.BudgetViewModel
import com.example.spendify.ui.screens.calendar.CalendarViewModel
import com.example.spendify.ui.screens.categories.CategoryViewModel
import com.example.spendify.ui.screens.charts.ChartsViewModel
import com.example.spendify.ui.screens.dashboard.DashboardViewModel
import com.example.spendify.ui.screens.profile.ProfileViewModel
import com.example.spendify.ui.screens.settings.SettingsViewModel
import com.example.spendify.ui.screens.transactions.AddEditTransactionViewModel
import com.example.spendify.ui.screens.transactions.TransactionHistoryViewModel
import com.example.spendify.ui.theme.SpendifyTheme
import com.example.spendify.ui.theme.SurfaceDark

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SpendifyApp

        val authViewModel = AuthViewModel(app.authRepository)
        val dashboardViewModel = DashboardViewModel(
            app.transactionRepository,
            app.budgetRepository,
            app.categoryRepository,
            app.authRepository
        )
        val historyViewModel = TransactionHistoryViewModel(
            app.transactionRepository,
            app.categoryRepository,
            app.authRepository,
            app.exportRepository
        )
        val calendarViewModel = CalendarViewModel(
            app.transactionRepository,
            app.authRepository
        )
        val budgetViewModel = BudgetViewModel(
            app.budgetRepository,
            app.categoryRepository,
            app.authRepository
        )
        val chartsViewModel = ChartsViewModel(
            app.transactionRepository,
            app.categoryRepository,
            app.authRepository,
            app.exportRepository
        )
        val categoryViewModel = CategoryViewModel(
            app.categoryRepository,
            app.authRepository
        )
        val settingsViewModel = SettingsViewModel(
            app.authRepository,
            app.transactionRepository,
            app.exportRepository
        )
        val profileViewModel = ProfileViewModel(
            app.authRepository,
            app.transactionRepository
        )

        setContent {
            val settingsState by settingsViewModel.uiState.collectAsState()
            val themeMode = settingsState.userProfile?.themeMode ?: ThemeMode.DARK

            SpendifyTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SurfaceDark
                ) {
                    SpendifyNavHost(
                        authViewModel = authViewModel,
                        dashboardViewModel = dashboardViewModel,
                        historyViewModel = historyViewModel,
                        addEditViewModelFactory = {
                            AddEditTransactionViewModel(
                                app.transactionRepository,
                                app.categoryRepository,
                                app.authRepository
                            )
                        },
                        calendarViewModel = calendarViewModel,
                        budgetViewModel = budgetViewModel,
                        chartsViewModel = chartsViewModel,
                        categoryViewModel = categoryViewModel,
                        settingsViewModel = settingsViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}
