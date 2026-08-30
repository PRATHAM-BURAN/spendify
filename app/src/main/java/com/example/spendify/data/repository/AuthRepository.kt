package com.example.spendify.data.repository

import com.example.spendify.data.local.dao.AccountDao
import com.example.spendify.data.local.dao.UserProfileDao
import com.example.spendify.data.local.entity.AccountEntity
import com.example.spendify.data.local.entity.UserProfileEntity
import com.example.spendify.data.remote.AuthResult
import com.example.spendify.data.remote.FirebaseAuthService
import com.example.spendify.data.remote.FirestoreService
import com.example.spendify.domain.model.ThemeMode
import com.example.spendify.domain.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthRepository(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService,
    private val userProfileDao: UserProfileDao,
    private val accountDao: AccountDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    fun getUserProfileFlow(): Flow<UserProfile?> {
        return userProfileDao.getUserProfileFlow().map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun getUserProfile(): UserProfile {
        return userProfileDao.getUserProfile()?.toDomain() ?: UserProfile(
            userId = "user_default",
            email = "",
            displayName = "User",
            currencyCode = "USD",
            currencySymbol = "$",
            themeMode = ThemeMode.DARK
        )
    }

    suspend fun isUserLoggedIn(): Boolean {
        return userProfileDao.getUserProfile() != null
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        val cleanPassword = password.trim()
        val inputPasswordHash = hashPassword(cleanPassword)

        // 1. Check local registered accounts
        val existingAccount = accountDao.getAccountByEmail(cleanEmail)
        if (existingAccount != null) {
            if (existingAccount.passwordHash != inputPasswordHash) {
                return AuthResult(
                    isSuccess = false,
                    errorMessage = "Incorrect password. Please check and try again."
                )
            }

            // Valid local credentials -> set user session
            val profile = UserProfile(
                userId = existingAccount.userId,
                email = existingAccount.email,
                displayName = existingAccount.fullName,
                currencyCode = "USD",
                currencySymbol = "$",
                themeMode = ThemeMode.DARK
            )
            userProfileDao.insertOrUpdate(UserProfileEntity.fromDomain(profile))

            // Sync with Firebase in background
            scope.launch {
                authService.signInWithEmail(cleanEmail, cleanPassword)
                firestoreService.saveProfile(profile)
            }

            return AuthResult(
                isSuccess = true,
                userId = profile.userId,
                email = profile.email,
                displayName = profile.displayName
            )
        }

        // 2. Try Cloud Firebase Sign In (if account was created on another device)
        val cloudResult = authService.signInWithEmail(cleanEmail, cleanPassword)
        if (cloudResult != null && cloudResult.isSuccess) {
            val userId = cloudResult.userId ?: "usr_${System.currentTimeMillis()}"
            val displayName = cloudResult.displayName ?: cleanEmail.substringBefore("@")

            accountDao.insertAccount(
                AccountEntity(
                    email = cleanEmail,
                    userId = userId,
                    fullName = displayName,
                    passwordHash = inputPasswordHash
                )
            )

            val profile = UserProfile(
                userId = userId,
                email = cleanEmail,
                displayName = displayName,
                currencyCode = "USD",
                currencySymbol = "$",
                themeMode = ThemeMode.DARK
            )
            userProfileDao.insertOrUpdate(UserProfileEntity.fromDomain(profile))
            scope.launch { firestoreService.saveProfile(profile) }

            return AuthResult(
                isSuccess = true,
                userId = userId,
                email = cleanEmail,
                displayName = displayName
            )
        }

        return AuthResult(
            isSuccess = false,
            errorMessage = "No account found with '$cleanEmail'. Please switch to Sign Up to create your account."
        )
    }

    suspend fun signUp(email: String, password: String, fullName: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        val cleanPassword = password.trim()
        val cleanName = fullName.trim().ifBlank { cleanEmail.substringBefore("@") }

        // 1. Check if email is already registered locally
        val existingAccount = accountDao.getAccountByEmail(cleanEmail)
        if (existingAccount != null) {
            return AuthResult(
                isSuccess = false,
                errorMessage = "An account with '$cleanEmail' already exists. Please switch to Sign In."
            )
        }

        val userId = "usr_${System.currentTimeMillis()}"
        val passwordHash = hashPassword(cleanPassword)

        // 2. Save account in local database (guarantees offline support)
        accountDao.insertAccount(
            AccountEntity(
                email = cleanEmail,
                userId = userId,
                fullName = cleanName,
                passwordHash = passwordHash
            )
        )

        // 3. Save active user profile
        val profile = UserProfile(
            userId = userId,
            email = cleanEmail,
            displayName = cleanName,
            currencyCode = "USD",
            currencySymbol = "$",
            themeMode = ThemeMode.DARK
        )
        userProfileDao.insertOrUpdate(UserProfileEntity.fromDomain(profile))

        // 4. Sync to Cloud Firebase in background
        scope.launch {
            authService.signUpWithEmail(cleanEmail, cleanPassword)
            firestoreService.saveProfile(profile)
        }

        return AuthResult(
            isSuccess = true,
            userId = profile.userId,
            email = profile.email,
            displayName = profile.displayName
        )
    }

    suspend fun updateProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(UserProfileEntity.fromDomain(profile))
        scope.launch {
            firestoreService.saveProfile(profile)
        }
    }

    suspend fun signOut() {
        authService.signOut()
        userProfileDao.clear()
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
