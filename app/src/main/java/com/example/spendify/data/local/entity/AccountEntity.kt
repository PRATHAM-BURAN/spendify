package com.example.spendify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val email: String,
    val userId: String,
    val fullName: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)
