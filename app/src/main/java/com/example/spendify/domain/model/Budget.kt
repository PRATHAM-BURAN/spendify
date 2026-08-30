package com.example.spendify.domain.model

data class Budget(
    val id: String,
    val scope: BudgetScope = BudgetScope.OVERALL,
    val categoryId: String? = null,
    val categoryName: String? = null,
    val limitAmount: Double,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val startDateMillis: Long = System.currentTimeMillis()
)

data class BudgetProgress(
    val budget: Budget,
    val spentAmount: Double,
    val remainingAmount: Double,
    val percentage: Float, // 0.0 to 1.0+
    val status: BudgetStatus
)
