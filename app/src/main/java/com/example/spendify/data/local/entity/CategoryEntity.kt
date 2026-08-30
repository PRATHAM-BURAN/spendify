package com.example.spendify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendify.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val isCustom: Boolean,
    val isIncome: Boolean,
    val orderIndex: Int
) {
    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            isCustom = isCustom,
            isIncome = isIncome,
            orderIndex = orderIndex
        )
    }

    companion object {
        fun fromDomain(domain: Category): CategoryEntity {
            return CategoryEntity(
                id = domain.id,
                name = domain.name,
                iconName = domain.iconName,
                colorHex = domain.colorHex,
                isCustom = domain.isCustom,
                isIncome = domain.isIncome,
                orderIndex = domain.orderIndex
            )
        }
    }
}
