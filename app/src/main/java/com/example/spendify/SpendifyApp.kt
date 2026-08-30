package com.example.spendify

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.spendify.data.local.AppDatabase
import com.example.spendify.data.remote.FirebaseAuthService
import com.example.spendify.data.remote.FirestoreService
import com.example.spendify.data.repository.AuthRepository
import com.example.spendify.data.repository.BudgetRepository
import com.example.spendify.data.repository.CategoryRepository
import com.example.spendify.data.repository.ExportRepository
import com.example.spendify.data.repository.TransactionRepository
import com.example.spendify.util.NotificationHelper
import com.example.spendify.workers.BudgetAlertWorker
import com.example.spendify.workers.RecurringTransactionWorker
import java.util.concurrent.TimeUnit

class SpendifyApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var firestoreService: FirestoreService
        private set

    lateinit var authService: FirebaseAuthService
        private set

    lateinit var transactionRepository: TransactionRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var budgetRepository: BudgetRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var exportRepository: ExportRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize Local Database
        database = AppDatabase.getDatabase(this)

        // 2. Initialize Remote Services
        firestoreService = FirestoreService()
        authService = FirebaseAuthService()

        // 3. Initialize Repositories
        transactionRepository = TransactionRepository(
            transactionDao = database.transactionDao(),
            firestoreService = firestoreService
        )
        categoryRepository = CategoryRepository(
            categoryDao = database.categoryDao(),
            firestoreService = firestoreService
        )
        budgetRepository = BudgetRepository(
            budgetDao = database.budgetDao(),
            transactionDao = database.transactionDao(),
            firestoreService = firestoreService
        )
        authRepository = AuthRepository(
            authService = authService,
            firestoreService = firestoreService,
            userProfileDao = database.userProfileDao(),
            accountDao = database.accountDao()
        )
        exportRepository = ExportRepository(this)

        // 4. Create Notification Channels
        NotificationHelper.createNotificationChannels(this)

        // 5. Schedule Background Workers
        scheduleBackgroundWorkers()
    }

    private fun scheduleBackgroundWorkers() {
        val workManager = WorkManager.getInstance(this)

        // Recurring Transaction Worker (Runs periodically every 12 hours)
        val recurringWorkRequest = PeriodicWorkRequestBuilder<RecurringTransactionWorker>(
            12, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "SpendifyRecurringWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            recurringWorkRequest
        )

        // Budget Alert Worker (Runs periodically every 6 hours)
        val budgetWorkRequest = PeriodicWorkRequestBuilder<BudgetAlertWorker>(
            6, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "SpendifyBudgetAlertWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            budgetWorkRequest
        )
    }
}
