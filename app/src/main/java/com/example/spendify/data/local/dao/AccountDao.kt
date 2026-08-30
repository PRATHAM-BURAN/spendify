package com.example.spendify.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spendify.data.local.entity.AccountEntity

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getAccountByEmail(email: String): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getAccountCount(): Int
}
