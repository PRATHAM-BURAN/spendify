package com.example.spendify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendify.domain.model.Budget
import com.example.spendify.domain.model.BudgetPeriod
import com.example.spendify.domain.model.BudgetScope

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val scope: String, // "OVERALL", "CATEGORY"
    val categoryId: String?,
    val categoryName: String?,
    val limitAmount: Double,
    val period: String, // "WEEKLY", "MONTHLY"
    val startDateMillis: Long
) {
    fun toDomain(): Budget {
        return Budget(
            id = id,
            scope = try { BudgetScope.valueOf(scope) } catch (e: Exception) { BudgetScope.OVERALL },
            categoryId = categoryId,
            categoryName = categoryName,
            limitAmount = limitAmount,
            period = try { BudgetPeriod.valueOf(period) } catch (e: Exception) { BudgetPeriod.MONTHLY },
            startDateMillis = startDateMillis
        )
    }

    companion object {
        fun fromDomain(domain: Budget): BudgetEntity {
            return BudgetEntity(
                id = domain.id,
                scope = domain.scope.name,
                categoryId = domain.categoryId,
                categoryName = domain.categoryName,
                limitAmount = domain.limitAmount,
                period = domain.period.name,
                startDateMillis = domain.startDateMillis
            )
        }
    }
}
