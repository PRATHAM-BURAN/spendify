package com.example.spendify.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class AuthResult(
    val isSuccess: Boolean,
    val userId: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val errorMessage: String? = null
)

class FirebaseAuthService {

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }
    }

    val isFirebaseAvailable: Boolean
        get() = auth != null

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    val currentUserId: String
        get() = auth?.currentUser?.uid ?: ""

    val isUserLoggedIn: Boolean
        get() = auth?.currentUser != null

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth?.addAuthStateListener(listener)
        awaitClose {
            auth?.removeAuthStateListener(listener)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult? {
        val firebaseAuth = auth ?: return null
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            AuthResult(
                isSuccess = true,
                userId = user?.uid,
                email = user?.email,
                displayName = user?.displayName
            )
        } catch (e: Exception) {
            AuthResult(
                isSuccess = false,
                errorMessage = e.localizedMessage ?: "Invalid email or password"
            )
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): AuthResult? {
        val firebaseAuth = auth ?: return null
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            AuthResult(
                isSuccess = true,
                userId = user?.uid,
                email = user?.email,
                displayName = user?.displayName
            )
        } catch (e: Exception) {
            AuthResult(
                isSuccess = false,
                errorMessage = e.localizedMessage ?: "Registration failed"
            )
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (_: Exception) {}
    }
}
