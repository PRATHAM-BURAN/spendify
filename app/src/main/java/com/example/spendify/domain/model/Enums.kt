package com.example.spendify.domain.model

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class PaymentMethod(val displayName: String, val iconName: String) {
    CARD("Card", "credit_card"),
    CASH("Cash", "payments"),
    UPI("UPI", "qr_code_2"),
    BANK_TRANSFER("Bank Transfer", "account_balance")
}

enum class RecurrenceFrequency(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

enum class SyncStatus {
    SYNCED,
    PENDING_SYNC,
    PENDING_DELETE
}

enum class BudgetScope {
    OVERALL,
    CATEGORY
}

enum class BudgetPeriod(val displayName: String) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly")
}

enum class BudgetStatus {
    SAFE,
    WARNING,
    DANGER
}

enum class ThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    DARK("Dark Mode"),
    LIGHT("Light Mode")
}

enum class CurrencyOption(val code: String, val symbol: String, val displayName: String) {
    USD("USD", "$", "USD ($)"),
    EUR("EUR", "€", "EUR (€)"),
    INR("INR", "₹", "INR (₹)"),
    GBP("GBP", "£", "GBP (£)"),
    JPY("JPY", "¥", "JPY (¥)"),
    CAD("CAD", "$", "CAD ($)"),
    AUD("AUD", "$", "AUD ($)")
}

enum class PeriodFilter(val displayName: String) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    CUSTOM("Custom")
}
