package com.example.spendify.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    fun format(amount: Double, currencySymbol: String = "$", includeDecimals: Boolean = true): String {
        val format = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = if (includeDecimals) 2 else 0
            maximumFractionDigits = if (includeDecimals) 2 else 0
        }
        val formattedNumber = format.format(amount)
        return "$currencySymbol$formattedNumber"
    }

    fun formatSigned(amount: Double, isIncome: Boolean, currencySymbol: String = "$"): String {
        val sign = if (isIncome) "+" else "-"
        val formatted = format(Math.abs(amount), currencySymbol)
        return "$sign$formatted"
    }
}
