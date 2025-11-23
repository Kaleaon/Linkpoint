package com.linkpoint.auth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.linkpoint.GridConnectionService
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.slproto.events.SLLoginResultEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Authentication Manager for Linkpoint
 * 
 * Manages user authentication including:
 * - XMLRPC login via GridConnectionService
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
    
    // Deferred result for login
    private var loginDeferred: CompletableDeferred<LoginResult>? = null
    
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
        val userId: String, // Changed to String for UUID
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
        // Subscribe to EventBus
        EventBus.getInstance().subscribe(this)
        
        // Try to restore session on initialization
        restoreSession()
    }
    
    @EventHandler
    fun onLoginResult(event: SLLoginResultEvent) {
        if (event.success) {
            val agentId = event.agentId?.toString() ?: "unknown"
            Log.i(TAG, "Login success event received for agent: $agentId")
            
            // Ideally we should have the username and gridUrl from the login request context
            // For now, we might have to rely on what we stored during login()
            // or what is available in the event or GridConnectionService
            
            // If we have a pending login, complete it
            val pendingCredentials = currentLoginCredentials
            if (pendingCredentials != null) {
                val session = UserSession(
                    userId = agentId,
                    username = pendingCredentials.username,
                    sessionToken = generateSessionToken(), // Local session token
                    gridUrl = pendingCredentials.gridUrl
                )
                
                saveSession(session)
                currentSession = session
                _authState.value = AuthState.Authenticated(session)
                
                loginDeferred?.complete(LoginResult.Success(session))
            } else {
                // Login happened outside of this manager (e.g. auto-reconnect?)
                // Or restored session?
            }
        } else {
            Log.e(TAG, "Login failed event received: ${event.message}")
            val error = LoginResult.Failure(event.message ?: "Unknown login error")
            _authState.value = AuthState.Error(error.message)
            loginDeferred?.complete(error)
        }
        
        loginInProgress.set(false)
        loginDeferred = null
        currentLoginCredentials = null
    }

    @EventHandler
    fun onDisconnect(event: SLDisconnectEvent) {
        Log.i(TAG, "Disconnect event received: ${event.message}")
        if (_authState.value is AuthState.Authenticated) {
            _authState.value = AuthState.Unauthenticated
            // Should we clear session?
            // If it's a temporary disconnect (reconnecting), maybe not.
            // But SLDisconnectEvent usually means fully disconnected.
            currentSession = null
            clearSession()
        }
    }
    
    private var currentLoginCredentials: LoginCredentials? = null

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
            currentLoginCredentials = credentials
            
            loginDeferred = CompletableDeferred()
            
            // Start GridConnectionService
            val intent = Intent(context, GridConnectionService::class.java).apply {
                action = GridConnectionService.LOGIN_ACTION
                putExtra(GridConnectionService.EXTRA_USERNAME, credentials.username)
                putExtra(GridConnectionService.EXTRA_PASSWORD, credentials.password)
                putExtra(GridConnectionService.EXTRA_GRID_URL, credentials.gridUrl)
                putExtra(GridConnectionService.EXTRA_START_LOCATION, credentials.startLocation)
            }
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
            
            // Wait for result with timeout
            withTimeout(60000) { // 60 seconds timeout
                loginDeferred?.await() ?: LoginResult.Failure("Login cancelled or error")
            }
            
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Login timed out", e)
            loginInProgress.set(false)
            _authState.value = AuthState.Error("Login timed out")
            LoginResult.Failure("Login timed out")
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            loginInProgress.set(false)
            _authState.value = AuthState.Error("Login failed: ${e.message}", e)
            LoginResult.Failure("Login failed: ${e.message}", e)
        }
    }
    
    /**
     * Logout
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Logging out user: ${currentSession?.username}")
            
            // Stop GridConnectionService
            val intent = Intent(context, GridConnectionService::class.java).apply {
                action = GridConnectionService.LOGOUT_ACTION
            }
            context.startService(intent)
            
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
    fun getCurrentUserId(): String? {
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
        // For SL, token refresh is effectively a re-login or handled by keepalives.
        // We assume session is valid if connected.
        // But if we are disconnected, we might need to login again.
        
        // Check GridConnectionService state?
        // For now, simple check.
        isAuthenticated()
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
            putString(KEY_USER_ID, session.userId)
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
            val userId = prefs.getString(KEY_USER_ID, null)
            val username = prefs.getString(KEY_USERNAME, null)
            val sessionToken = prefs.getString(KEY_SESSION_TOKEN, null)
            val gridUrl = prefs.getString(KEY_GRID_URL, null)
            val loginTime = prefs.getLong(KEY_LAST_LOGIN, 0L)
            
            if (userId != null && username != null && sessionToken != null && gridUrl != null) {
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
        EventBus.getInstance().unsubscribe(this)
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
