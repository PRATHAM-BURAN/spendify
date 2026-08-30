package com.example.spendify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.spendify.data.local.dao.AccountDao
import com.example.spendify.data.local.dao.BudgetDao
import com.example.spendify.data.local.dao.CategoryDao
import com.example.spendify.data.local.dao.TransactionDao
import com.example.spendify.data.local.dao.UserProfileDao
import com.example.spendify.data.local.entity.AccountEntity
import com.example.spendify.data.local.entity.BudgetEntity
import com.example.spendify.data.local.entity.CategoryEntity
import com.example.spendify.data.local.entity.TransactionEntity
import com.example.spendify.data.local.entity.UserProfileEntity
import com.example.spendify.domain.model.DefaultCategories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        UserProfileEntity::class,
        AccountEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spendify_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                // Populate default category templates only
                val categoryEntities = DefaultCategories.allDefaultCategories.map {
                    CategoryEntity.fromDomain(it)
                }
                database.categoryDao().insertCategories(categoryEntities)
            }
        }
    }
}
