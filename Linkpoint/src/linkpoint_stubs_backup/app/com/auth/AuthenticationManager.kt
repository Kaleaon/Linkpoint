package com.lumiyaviewer.lumiya.auth

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Authentication Manager for Linkpoint
 * 
 * Manages user authentication including:
 * - XMLRPC login
 * - Token management
 * - Session persistence
 * - Logout functionality
 * - Authentication state
 */
class AuthenticationManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AuthenticationManager"
        private const val PREFS_NAME = "auth_preferences"
        private const val KEY_USERNAME = "username"
        private const val KEY_SESSION_TOKEN = "session_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_GRID_URL = "grid_url"
        private const val KEY_LAST_LOGIN = "last_login"
        
        @Volatile
        private var instance: AuthenticationManager? = null
        
        fun getInstance(context: Context): AuthenticationManager {
            return instance ?: synchronized(this) {
                instance ?: AuthenticationManager(context.applicationContext).also { 
                    instance = it 
                }
            }
        }
    }
    
    // Shared preferences for persistence
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Authentication state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // Current session
    private var currentSession: UserSession? = null
    
    // Coroutine scope
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Login in progress flag
    private val loginInProgress = AtomicBoolean(false)
    
    /**
     * Authentication State
     */
    sealed class AuthState {
        object Unauthenticated : AuthState()
        object Authenticating : AuthState()
        data class Authenticated(val session: UserSession) : AuthState()
        data class Error(val message: String, val exception: Exception? = null) : AuthState()
    }
    
    /**
     * User Session
     */
    data class UserSession(
        val userId: Long,
        val username: String,
        val sessionToken: String,
        val gridUrl: String,
        val loginTime: Long = System.currentTimeMillis()
    )
    
    /**
     * Login Credentials
     */
    data class LoginCredentials(
        val username: String,
        val password: String,
        val gridUrl: String,
        val startLocation: String = "last"
    )
    
    /**
     * Login Result
     */
    sealed class LoginResult {
        data class Success(val session: UserSession) : LoginResult()
        data class Failure(val message: String, val exception: Exception? = null) : LoginResult()
    }
    
    init {
        // Try to restore session on initialization
        restoreSession()
    }
    
    /**
     * Login with credentials
     */
    suspend fun login(credentials: LoginCredentials): LoginResult = withContext(Dispatchers.IO) {
        if (!loginInProgress.compareAndSet(false, true)) {
            return@withContext LoginResult.Failure("Login already in progress")
        }
        
        try {
            Log.i(TAG, "Attempting login for user: ${credentials.username}")
            _authState.value = AuthState.Authenticating
            
            // TODO: Implement actual XMLRPC login
            // For now, simulate login
            delay(2000)
            
            // Create session (placeholder)
            val session = UserSession(
                userId = 1L, // TODO: Get from server
                username = credentials.username,
                sessionToken = generateSessionToken(),
                gridUrl = credentials.gridUrl
            )
            
            // Save session
            saveSession(session)
            currentSession = session
            
            _authState.value = AuthState.Authenticated(session)
            
            Log.i(TAG, "Login successful for user: ${credentials.username}")
            LoginResult.Success(session)
            
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            val errorMessage = e.message?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
                ?: "An unexpected error occurred. Please try again."
            _authState.value = AuthState.Error("Login failed: $errorMessage", e)
            LoginResult.Failure("Login failed: $errorMessage", e)
            
        } finally {
            loginInProgress.set(false)
        }
    }
    
    /**
     * Logout
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Logging out user: ${currentSession?.username}")
            
            // TODO: Notify server of logout
            
            // Clear session
            clearSession()
            currentSession = null
            
            _authState.value = AuthState.Unauthenticated
            
            Log.i(TAG, "Logout successful")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
        }
    }
    
    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return currentSession != null && _authState.value is AuthState.Authenticated
    }
    
    /**
     * Get current session
     */
    fun getCurrentSession(): UserSession? {
        return currentSession
    }
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): Long? {
        return currentSession?.userId
    }
    
    /**
     * Get current username
     */
    fun getCurrentUsername(): String? {
        return currentSession?.username
    }
    
    /**
     * Refresh session token
     */
    suspend fun refreshToken(): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = currentSession ?: return@withContext false
            
            Log.i(TAG, "Refreshing session token")
            
            // TODO: Implement actual token refresh
            delay(1000)
            
            // Update session with new token
            val newSession = session.copy(
                sessionToken = generateSessionToken(),
                loginTime = System.currentTimeMillis()
            )
            
            saveSession(newSession)
            currentSession = newSession
            
            _authState.value = AuthState.Authenticated(newSession)
            
            Log.i(TAG, "Token refreshed successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing token", e)
            false
        }
    }
    
    /**
     * Validate session
     */
    suspend fun validateSession(): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = currentSession ?: return@withContext false
            
            // Check if session is expired (24 hours)
            val sessionAge = System.currentTimeMillis() - session.loginTime
            val maxAge = 24 * 60 * 60 * 1000L // 24 hours
            
            if (sessionAge > maxAge) {
                Log.w(TAG, "Session expired")
                logout()
                return@withContext false
            }
            
            // TODO: Validate with server
            
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error validating session", e)
            false
        }
    }
    
    /**
     * Save session to persistent storage
     */
    private fun saveSession(session: UserSession) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, session.userId)
            putString(KEY_USERNAME, session.username)
            putString(KEY_SESSION_TOKEN, session.sessionToken)
            putString(KEY_GRID_URL, session.gridUrl)
            putLong(KEY_LAST_LOGIN, session.loginTime)
            apply()
        }
        
        Log.d(TAG, "Session saved to persistent storage")
    }
    
    /**
     * Restore session from persistent storage
     */
    private fun restoreSession() {
        try {
            val userId = prefs.getLong(KEY_USER_ID, -1L)
            val username = prefs.getString(KEY_USERNAME, null)
            val sessionToken = prefs.getString(KEY_SESSION_TOKEN, null)
            val gridUrl = prefs.getString(KEY_GRID_URL, null)
            val loginTime = prefs.getLong(KEY_LAST_LOGIN, 0L)
            
            if (userId != -1L && username != null && sessionToken != null && gridUrl != null) {
                val session = UserSession(
                    userId = userId,
                    username = username,
                    sessionToken = sessionToken,
                    gridUrl = gridUrl,
                    loginTime = loginTime
                )
                
                // Check if session is still valid
                val sessionAge = System.currentTimeMillis() - loginTime
                val maxAge = 24 * 60 * 60 * 1000L // 24 hours
                
                if (sessionAge < maxAge) {
                    currentSession = session
                    _authState.value = AuthState.Authenticated(session)
                    Log.i(TAG, "Session restored for user: $username")
                } else {
                    Log.w(TAG, "Restored session is expired")
                    clearSession()
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring session", e)
            clearSession()
        }
    }
    
    /**
     * Clear session from persistent storage
     */
    private fun clearSession() {
        prefs.edit().clear().apply()
        Log.d(TAG, "Session cleared from persistent storage")
    }
    
    /**
     * Generate session token
     */
    private fun generateSessionToken(): String {
        return java.util.UUID.randomUUID().toString()
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        scope.cancel()
        Log.i(TAG, "AuthenticationManager cleaned up")
    }
}

/**
 * Extension functions
 */

/**
 * Check if session is expired
 */
fun AuthenticationManager.UserSession.isExpired(maxAgeHours: Int = 24): Boolean {
    val sessionAge = System.currentTimeMillis() - loginTime
    val maxAge = maxAgeHours * 60 * 60 * 1000L
    return sessionAge > maxAge
}

/**
 * Get session age in hours
 */
fun AuthenticationManager.UserSession.getAgeHours(): Long {
    val sessionAge = System.currentTimeMillis() - loginTime
    return sessionAge / (60 * 60 * 1000L)
}