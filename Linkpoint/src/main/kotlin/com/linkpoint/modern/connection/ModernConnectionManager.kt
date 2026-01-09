package com.linkpoint.modern.connection

import android.content.Context
import android.util.Log
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Modern connection manager that handles Second Life grid connections
 * with improved reliability, retry logic, and fallback mechanisms.
 * 
 * Fixed from original broken syntax to proper Kotlin.
 */
class ModernConnectionManager(context: Context) {
    
    companion object {
        private const val TAG = "ModernConnectionManager"
        
        // Updated login endpoints (fix for DNS issues)
        private const val MAIN_GRID_LOGIN = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        private const val BETA_GRID_LOGIN = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
        
        // Retry configuration
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val RETRY_BACKOFF_MULTIPLIER = 2.0
    }
    
    private val appContext: Context = context.applicationContext
    private val diagnostics: ConnectionDiagnostics = ConnectionDiagnostics(appContext)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activeConnections = AtomicInteger(0)
    
    // Connection state
    @Volatile
    private var state: ConnectionState = ConnectionState.DISCONNECTED
    
    @Volatile
    private var lastError: String? = null
    
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        AUTHENTICATING,
        CONNECTED,
        RECONNECTING,
        ERROR
    }
    
    /**
     * Establish connection to Second Life grid with modern reliability features.
     */
    suspend fun connectAsync(authParams: SLAuthParams): SLAuthReply = withContext(Dispatchers.IO) {
        Log.i(TAG, "Starting modern connection attempt to: ${authParams.gridName}")
        
        setState(ConnectionState.CONNECTING)
        
        try {
            // Perform pre-connection diagnostics
            val diagnosticResult = performPreConnectionDiagnostics()
            
            if (diagnosticResult.getOverallHealth() == ConnectionDiagnostics.DiagnosticResult.HealthLevel.NO_CONNECTIVITY) {
                setState(ConnectionState.ERROR)
                throw ConnectionException("No network connectivity available")
            }
            
            if (diagnosticResult.getOverallHealth() == ConnectionDiagnostics.DiagnosticResult.HealthLevel.CRITICAL) {
                Log.w(TAG, "Poor connectivity detected, will attempt connection with fallbacks")
            }
            
            // Attempt connection with retry
            val result = attemptConnectionWithRetry(authParams, 0)
            
            setState(ConnectionState.CONNECTED)
            activeConnections.incrementAndGet()
            Log.i(TAG, "Connection established successfully")
            
            result
        } catch (e: Exception) {
            setState(ConnectionState.ERROR)
            lastError = e.message
            Log.e(TAG, "Connection failed: ${e.message}", e)
            throw e
        }
    }
    
    private suspend fun performPreConnectionDiagnostics(): ConnectionDiagnostics.DiagnosticResult {
        Log.d(TAG, "Performing pre-connection diagnostics")
        
        return try {
            withTimeout(15000) {
                diagnostics.diagnoseAsync()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Diagnostic check failed, proceeding with connection attempt", e)
            // Return a minimal result indicating we should try anyway
            ConnectionDiagnostics.DiagnosticResult(networkAvailable = true)
        }
    }
    
    private suspend fun attemptConnectionWithRetry(
        authParams: SLAuthParams,
        attemptNumber: Int
    ): SLAuthReply {
        if (attemptNumber >= MAX_RETRY_ATTEMPTS) {
            throw ConnectionException("Maximum retry attempts exceeded ($MAX_RETRY_ATTEMPTS)")
        }
        
        Log.d(TAG, "Connection attempt ${attemptNumber + 1}/$MAX_RETRY_ATTEMPTS")
        
        return try {
            performActualConnection(authParams)
        } catch (e: Exception) {
            Log.w(TAG, "Connection attempt ${attemptNumber + 1} failed: ${e.message}")
            
            if (attemptNumber < MAX_RETRY_ATTEMPTS - 1) {
                // Calculate exponential backoff delay
                val delay = (INITIAL_RETRY_DELAY_MS * Math.pow(RETRY_BACKOFF_MULTIPLIER, attemptNumber.toDouble())).toLong()
                Log.i(TAG, "Retrying connection in ${delay}ms")
                
                delay(delay)
                setState(ConnectionState.RECONNECTING)
                attemptConnectionWithRetry(authParams, attemptNumber + 1)
            } else {
                throw ConnectionException("All connection attempts failed", e)
            }
        }
    }
    
    private suspend fun performActualConnection(authParams: SLAuthParams): SLAuthReply {
        setState(ConnectionState.AUTHENTICATING)
        
        // Use the corrected login URL if needed
        val correctedParams = ensureCorrectLoginURL(authParams)
        
        return withContext(Dispatchers.IO) {
            try {
                // Use the existing authentication system but with modern error handling
                val auth = com.linkpoint.slproto.auth.SLAuth()
                val result = auth.Login(correctedParams)
                    ?: throw ConnectionException("Authentication returned null response")
                
                if (!result.success) {
                    throw ConnectionException("Authentication failed: ${result.message}")
                }
                
                result
            } catch (e: ConnectionException) {
                throw e
            } catch (e: Exception) {
                throw ConnectionException("Connection failed during authentication", e)
            }
        }
    }
    
    private fun ensureCorrectLoginURL(original: SLAuthParams): SLAuthParams {
        var loginURL = original.loginURL
        
        // Fix common login URL issues
        if (loginURL.isNullOrEmpty()) {
            Log.i(TAG, "No login URL specified, using main grid default")
            loginURL = MAIN_GRID_LOGIN
        }
        
        // Handle legacy URLs or incorrect domains
        if (loginURL.contains("login.secondlife.com")) {
            Log.i(TAG, "Correcting legacy login URL to use login.agni.lindenlab.com")
            loginURL = MAIN_GRID_LOGIN
        }
        
        // Ensure HTTPS
        if (loginURL.startsWith("http://")) {
            Log.i(TAG, "Upgrading login URL from HTTP to HTTPS")
            loginURL = loginURL.replace("http://", "https://")
        }
        
        // Create corrected auth params if URL was changed
        return if (loginURL != original.loginURL) {
            Log.i(TAG, "Creating auth params with corrected login URL")
            SLAuthParams(
                original.loginName,
                original.passwordHash,
                original.clientID,
                original.startLocation,
                loginURL,
                original.gridName
            )
        } else {
            original
        }
    }
    
    private fun setState(newState: ConnectionState) {
        if (state != newState) {
            val oldState = state
            state = newState
            Log.d(TAG, "Connection state changed: $oldState -> $newState")
            // TODO: Emit state change event for UI updates
        }
    }
    
    fun getState(): ConnectionState = state
    
    fun getLastError(): String? = lastError
    
    fun getActiveConnectionCount(): Int = activeConnections.get()
    
    fun shutdown() {
        Log.i(TAG, "Shutting down connection manager")
        scope.cancel()
    }
    
    /**
     * Custom exception for connection-related errors
     */
    class ConnectionException : RuntimeException {
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}
