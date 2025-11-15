package com.lumiyaviewer.lumiya.ui.compose.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Login Screen
 * 
 * Handles:
 * - Form validation
 * - Authentication logic
 * - State management
 * - Error handling
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val slAuth: SLAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginResult = MutableStateFlow<LoginResult?>(null)
    val loginResult: StateFlow<LoginResult?> = _loginResult.asStateFlow()

    /**
     * Update first name and validate
     */
    fun updateFirstName(firstName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                firstName = firstName,
                firstNameError = validateFirstName(firstName)
            )
        }
    }

    /**
     * Update last name and validate
     */
    fun updateLastName(lastName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                lastName = lastName,
                lastNameError = validateLastName(lastName)
            )
        }
    }

    /**
     * Update password and validate
     */
    fun updatePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = validatePassword(password)
            )
        }
    }

    /**
     * Update selected grid
     */
    fun updateSelectedGrid(grid: String) {
        _uiState.update { it.copy(selectedGrid = grid) }
    }

    /**
     * Perform login
     */
    fun login() {
        val currentState = _uiState.value
        
        // Validate all fields
        val firstNameError = validateFirstName(currentState.firstName)
        val lastNameError = validateLastName(currentState.lastName)
        val passwordError = validatePassword(currentState.password)

        if (firstNameError != null || lastNameError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    firstNameError = firstNameError,
                    lastNameError = lastNameError,
                    passwordError = passwordError,
                    errorMessage = "Please fix the errors above"
                )
            }
            return
        }

        // Start login process
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                statusMessage = "Connecting to Second Life..."
            )
        }

        viewModelScope.launch {
            try {
                // Create auth params
                val authParams = SLAuthParams(
                    firstName = currentState.firstName.trim(),
                    lastName = currentState.lastName.trim(),
                    password = currentState.password,
                    startLocation = "last",
                    channel = "Linkpoint",
                    version = "1.0.0",
                    platform = "Android",
                    mac = "00:00:00:00:00:00",
                    id0 = "00000000-0000-0000-0000-000000000000",
                    agreeToTos = true,
                    readCritical = true
                )

                _uiState.update {
                    it.copy(statusMessage = "Authenticating...")
                }

                // Perform authentication
                val authReply = slAuth.authenticate(authParams)

                if (authReply.success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            statusMessage = "Login successful! Loading world..."
                        )
                    }

                    // Save user session
                    saveUserSession(authReply)

                    // Emit success result
                    _loginResult.value = LoginResult.Success(authReply)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = authReply.message ?: "Login failed. Please check your credentials.",
                            statusMessage = null
                        )
                    }
                    _loginResult.value = LoginResult.Error(authReply.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Connection error: ${e.message}",
                        statusMessage = null
                    )
                }
                _loginResult.value = LoginResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Save user session to database
     */
    private suspend fun saveUserSession(authReply: SLAuthReply) {
        try {
            // Save user data to repository
            userRepository.saveCurrentUser(
                agentId = authReply.agentId ?: "",
                sessionId = authReply.sessionId ?: "",
                firstName = _uiState.value.firstName,
                lastName = _uiState.value.lastName
            )
        } catch (e: Exception) {
            // Log error but don't fail login
            android.util.Log.e("LoginViewModel", "Failed to save user session", e)
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Clear login result
     */
    fun clearLoginResult() {
        _loginResult.value = null
    }

    // ========== VALIDATION FUNCTIONS ==========

    private fun validateFirstName(firstName: String): String? {
        return when {
            firstName.isBlank() -> "First name is required"
            firstName.length < 2 -> "First name must be at least 2 characters"
            !firstName.matches(Regex("^[a-zA-Z]+$")) -> "First name can only contain letters"
            else -> null
        }
    }

    private fun validateLastName(lastName: String): String? {
        return when {
            lastName.isBlank() -> "Last name is required"
            lastName.length < 2 -> "Last name must be at least 2 characters"
            !lastName.matches(Regex("^[a-zA-Z]+$")) -> "Last name can only contain letters"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}

/**
 * Login result sealed class
 */
sealed class LoginResult {
    data class Success(val authReply: SLAuthReply) : LoginResult()
    data class Error(val message: String) : LoginResult()
}