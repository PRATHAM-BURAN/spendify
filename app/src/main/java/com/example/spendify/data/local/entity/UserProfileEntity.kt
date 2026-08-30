package com.example.spendify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendify.domain.model.ThemeMode
import com.example.spendify.domain.model.UserProfile

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val displayName: String,
    val photoUrl: String,
    val currencyCode: String,
    val currencySymbol: String,
    val themeMode: String,
    val budgetAlertEnabled: Boolean,
    val budgetAlertThreshold: Int,
    val recurringRemindersEnabled: Boolean
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            userId = userId,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol,
            themeMode = try { ThemeMode.valueOf(themeMode) } catch (e: Exception) { ThemeMode.DARK },
            budgetAlertEnabled = budgetAlertEnabled,
            budgetAlertThreshold = budgetAlertThreshold,
            recurringRemindersEnabled = recurringRemindersEnabled
        )
    }

    companion object {
        fun fromDomain(domain: UserProfile): UserProfileEntity {
            return UserProfileEntity(
                userId = domain.userId,
                email = domain.email,
                displayName = domain.displayName,
                photoUrl = domain.photoUrl,
                currencyCode = domain.currencyCode,
                currencySymbol = domain.currencySymbol,
                themeMode = domain.themeMode.name,
                budgetAlertEnabled = domain.budgetAlertEnabled,
                budgetAlertThreshold = domain.budgetAlertThreshold,
                recurringRemindersEnabled = domain.recurringRemindersEnabled
            )
        }
    }
}
