package com.example.spendify.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Dashboard : Screen("dashboard")
    object History : Screen("history")
    object Calendar : Screen("calendar")
    object Budget : Screen("budget")
    object Charts : Screen("charts")
    object Categories : Screen("categories")
    object Settings : Screen("settings")
    object AddEditTransaction : Screen("add_edit_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: String? = null): String {
            return if (transactionId != null) {
                "add_edit_transaction?transactionId=$transactionId"
            } else {
                "add_edit_transaction"
            }
        }
    }
}
