package com.example.spendify.domain.model

data class UserProfile(
    val userId: String,
    val email: String,
    val displayName: String = "",
    val photoUrl: String = "",
    val currencyCode: String = "USD",
    val currencySymbol: String = "$",
    val themeMode: ThemeMode = ThemeMode.DARK,
    val budgetAlertEnabled: Boolean = true,
    val budgetAlertThreshold: Int = 80, // percentage
    val recurringRemindersEnabled: Boolean = true
)
