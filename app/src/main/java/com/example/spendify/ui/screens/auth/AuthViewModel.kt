package com.example.spendify.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendify.data.remote.AuthResult
import com.example.spendify.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isSignUp: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun onFullNameChanged(name: String) {
        _uiState.update { it.copy(fullName = name, fullNameError = null, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, confirmPasswordError = null, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isSignUp = !it.isSignUp,
                fullName = "",
                email = "",
                password = "",
                confirmPassword = "",
                fullNameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                errorMessage = null
            )
        }
    }

    fun submitAuth() {
        val state = _uiState.value
        val name = state.fullName.trim()
        val email = state.email.trim()
        val password = state.password.trim()
        val confirmPassword = state.confirmPassword.trim()

        var hasError = false
        var nameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        // Validate Full Name (for Sign Up)
        if (state.isSignUp) {
            if (name.isBlank()) {
                nameError = "Full name is required"
                hasError = true
            } else if (name.length < 2) {
                nameError = "Name must be at least 2 characters"
                hasError = true
            }
        }

        // Validate Email
        if (email.isBlank()) {
            emailError = "Email is required"
            hasError = true
        } else if (!emailRegex.matches(email)) {
            emailError = "Please enter a valid email address (e.g. name@domain.com)"
            hasError = true
        }

        // Validate Password
        if (password.isBlank()) {
            passwordError = "Password is required"
            hasError = true
        } else if (state.isSignUp && password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            hasError = true
        } else if (state.isSignUp && (!password.any { it.isDigit() } || !password.any { it.isLetter() })) {
            passwordError = "Password must contain both letters and numbers"
            hasError = true
        }

        // Validate Confirm Password (for Sign Up)
        if (state.isSignUp) {
            if (confirmPassword.isBlank()) {
                confirmPasswordError = "Please confirm your password"
                hasError = true
            } else if (confirmPassword != password) {
                confirmPasswordError = "Passwords do not match"
                hasError = true
            }
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    errorMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = if (state.isSignUp) {
                authRepository.signUp(email, password, name)
            } else {
                authRepository.signIn(email, password)
            }

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.errorMessage ?: "Authentication failed. Please check your credentials."
                    )
                }
            }
        }
    }

    fun resetAuthState() {
        _uiState.value = AuthUiState()
    }
}
