package com.linkpoint.modern.connection

import android.content.Context
import android.util.Log
import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.pow

/**
 * Modern connection manager that handles Second Life grid connections
 * with improved reliability, retry logic, and fallback mechanisms
 */
class ModernConnectionManager(context: Context) {
    
    private val TAG = "ModernConnectionManager"
    
    // Updated login endpoints (fix for DNS issues)
    private val MAIN_GRID_LOGIN = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
    private val BETA_GRID_LOGIN = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
    
    // Retry configuration
    private val MAX_RETRY_ATTEMPTS = 3
    private val INITIAL_RETRY_DELAY_MS = 1000L
    private val RETRY_BACKOFF_MULTIPLIER = 2.0
    
    private val context: Context = context.applicationContext
    private val diagnostics = ConnectionDiagnostics(context)
    private val executor: ExecutorService
    private val activeConnections = AtomicInteger(0)
    
    // Connection state
    @Volatile private var state = ConnectionState.DISCONNECTED
    @Volatile private var lastError: String? = null
    
    /**
     * Connection states
     */
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING, 
        AUTHENTICATING,
        CONNECTED,
        RECONNECTING,
        ERROR
    }

    init {
        executor = Executors.newCachedThreadPool { r ->
            Thread(r, "SL-Connection-${r.hashCode()}").apply {
                isDaemon = true
            }
        }
    }

    /**
     * Establish connection to Second Life grid with modern reliability features
     */
    fun connectAsync(authParams: SLAuthParams): CompletableFuture<SLAuthReply> {
        Log.i(TAG, "Starting modern connection attempt to: ${authParams.gridName}")
        
        setState(ConnectionState.CONNECTING)
        
        return CompletableFuture
            .supplyAsync({ performPreConnectionDiagnostics() }, executor)
            .thenCompose { diagnosticResult ->
                when (diagnosticResult.getOverallHealth()) {
                    ConnectionDiagnostics.DiagnosticResult.HealthLevel.NO_CONNECTIVITY -> {
                        setState(ConnectionState.ERROR)
                        throw ConnectionException("No network connectivity available")
                    }
                    ConnectionDiagnostics.DiagnosticResult.HealthLevel.CRITICAL -> {
                        Log.w(TAG, "Poor connectivity detected, will attempt connection with fallbacks")
                    }
                    else -> {
                        // Good or acceptable connectivity
                    }
                }
                
                attemptConnectionWithRetry(authParams, 0)
            }
            .whenComplete { result, throwable ->
                if (throwable != null) {
                    setState(ConnectionState.ERROR)
                    lastError = throwable.message
                    Log.e(TAG, "Connection failed: ${throwable.message}", throwable)
                } else {
                    setState(ConnectionState.CONNECTED)
                    activeConnections.incrementAndGet()
                    Log.i(TAG, "Connection established successfully")
                }
            }
    }

    /**
     * Perform pre-connection diagnostics
     */
    private fun performPreConnectionDiagnostics(): ConnectionDiagnostics.DiagnosticResult {
        Log.d(TAG, "Performing pre-connection diagnostics")
        
        return try {
            diagnostics.diagnoseAsync().get(15, TimeUnit.SECONDS)
        } catch (e: Exception) {
            Log.w(TAG, "Diagnostic check failed, proceeding with connection attempt", e)
            // Return a minimal result indicating we should try anyway
            ConnectionDiagnostics.DiagnosticResult().apply {
                networkAvailable = true // Assume network is available
            }
        }
    }

    /**
     * Attempt connection with retry logic
     */
    private fun attemptConnectionWithRetry(
        authParams: SLAuthParams,
        attemptNumber: Int
    ): CompletableFuture<SLAuthReply> {
        if (attemptNumber >= MAX_RETRY_ATTEMPTS) {
            throw ConnectionException("Maximum retry attempts exceeded ($MAX_RETRY_ATTEMPTS)")
        }
        
        Log.d(TAG, "Connection attempt ${attemptNumber + 1}/$MAX_RETRY_ATTEMPTS")
        
        return CompletableFuture
            .supplyAsync({ performActualConnection(authParams) }, executor)
            .handle { result, throwable ->
                if (throwable != null) {
                    Log.w(TAG, "Connection attempt ${attemptNumber + 1} failed: ${throwable.message}")
                    
                    if (attemptNumber < MAX_RETRY_ATTEMPTS - 1) {
                        // Calculate exponential backoff delay
                        val delay = (INITIAL_RETRY_DELAY_MS * RETRY_BACKOFF_MULTIPLIER.pow(attemptNumber)).toLong()
                        Log.i(TAG, "Retrying connection in ${delay}ms")
                        
                        try {
                            Thread.sleep(delay)
                        } catch (e: InterruptedException) {
                            Thread.currentThread().interrupt()
                            throw ConnectionException("Connection retry interrupted")
                        }
                        
                        setState(ConnectionState.RECONNECTING)
                        attemptConnectionWithRetry(authParams, attemptNumber + 1).join()
                    } else {
                        throw ConnectionException("All connection attempts failed", throwable)
                    }
                } else {
                    result
                }
            }
    }

    /**
     * Perform actual connection
     */
    private fun performActualConnection(authParams: SLAuthParams): SLAuthReply {
        setState(ConnectionState.AUTHENTICATING)
        
        // Use the corrected login URL if needed
        val correctedParams = ensureCorrectLoginURL(authParams)
        
        return try {
            // Use the existing authentication system but with modern error handling
            val auth = SLAuth()
            val result = auth.Login(correctedParams)
                ?: throw ConnectionException("Authentication returned null response")
            
            if (!result.success) {
                throw ConnectionException("Authentication failed: ${result.message}")
            }
            
            result
        } catch (e: Exception) {
            throw ConnectionException("Connection failed during authentication", e)
        }
    }

    /**
     * Ensure correct login URL
     */
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
            Log.i(TAG, "Creating new auth params with corrected login URL")
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

    /**
     * Set connection state
     */
    private fun setState(newState: ConnectionState) {
        if (state != newState) {
            val oldState = state
            state = newState
            Log.d(TAG, "Connection state changed: $oldState -> $newState")
        }
    }

    /**
     * Get current state
     */
    fun getState(): ConnectionState = state
    
    /**
     * Get last error message
     */
    fun getLastError(): String? = lastError
    
    /**
     * Get active connection count
     */
    fun getActiveConnectionCount(): Int = activeConnections.get()

    /**
     * Shutdown the connection manager
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down connection manager")
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    /**
     * Custom exception for connection-related errors
     */
    class ConnectionException : RuntimeException {
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}
