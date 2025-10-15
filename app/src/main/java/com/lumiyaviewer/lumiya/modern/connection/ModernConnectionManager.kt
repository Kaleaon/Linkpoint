package com.lumiyaviewer.lumiya.modern.connection

import android.content.Context
import android.util.Log
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Modern connection manager that handles Second Life grid connections
 * with improved reliability, retry logic, and fallback mechanisms.
 */
class ModernConnectionManager {
    private String TAG = "ModernConnectionManager"
    
    // Updated login endpoints (fix for DNS issues)
    private String MAIN_GRID_LOGIN = "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
    private String BETA_GRID_LOGIN = "https://login.aditi.lindenlab.com/cgi-bin/login.cgi"
    
    // Retry configuration
    private int MAX_RETRY_ATTEMPTS = 3
    private long INITIAL_RETRY_DELAY_MS = 1000
    private double RETRY_BACKOFF_MULTIPLIER = 2.0
    
    private Context context
    private ConnectionDiagnostics diagnostics
    private ExecutorService executor
    private AtomicInteger activeConnections = AtomicInteger(0)
    
    // Connection state
    private volatile ConnectionState state = ConnectionState.DISCONNECTED
    private volatile String lastError = null
    
    enum class class ConnectionState {
        DISCONNECTED,
        CONNECTING, 
        AUTHENTICATING,
        CONNECTED,
        RECONNECTING,
        ERROR
    }

    ModernConnectionManager(Context context) {
        this.context = context.getApplicationContext()
        this.diagnostics = ConnectionDiagnostics(context)
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = Thread(r, "SL-Connection-" + r.hashCode())
            t.setDaemon(true)
            return t
        })
    }

    /**
     * Establish connection to Second Life grid with modern reliability features.
     */
    CompletableFuture<SLAuthReply> connectAsync(SLAuthParams authParams) {
        Log.i(TAG, "Starting modern connection attempt to: " + authParams.gridName)
        
        setState(ConnectionState.CONNECTING)
        
        return CompletableFuture
            .supplyAsync(() -> performPreConnectionDiagnostics(), executor)
            .thenCompose(diagnosticResult -> {
                if (diagnosticResult.getOverallHealth() == ConnectionDiagnostics.DiagnosticResult.HealthLevel.NO_CONNECTIVITY) {
                    setState(ConnectionState.ERROR)
                    throw ConnectionException("No network connectivity available")
                }
                
                if (diagnosticResult.getOverallHealth() == ConnectionDiagnostics.DiagnosticResult.HealthLevel.CRITICAL) {
                    Log.w(TAG, "Poor connectivity detected, will attempt connection with fallbacks")
                }
                
                return attemptConnectionWithRetry(authParams, 0)
            })
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    setState(ConnectionState.ERROR)
                    lastError = throwable.getMessage()
                    Log.e(TAG, "Connection failed: " + throwable.getMessage(), throwable)
                } else {
                    setState(ConnectionState.CONNECTED)
                    activeConnections.incrementAndGet()
                    Log.i(TAG, "Connection established successfully")
                }
            })
    }

    private ConnectionDiagnostics.DiagnosticResult performPreConnectionDiagnostics() {
        Log.d(TAG, "Performing pre-connection diagnostics")
        
        try {
            return diagnostics.diagnoseAsync().get(15, TimeUnit.SECONDS)
        } catch (Exception e) {
            Log.w(TAG, "Diagnostic check failed, proceeding with connection attempt", e)
            // Return a minimal result indicating we should try anyway
            ConnectionDiagnostics.DiagnosticResult result = new ConnectionDiagnostics.DiagnosticResult()
            result.networkAvailable = true; // Assume network is available
            return result
        }
    }

    private CompletableFuture<SLAuthReply> attemptConnectionWithRetry(SLAuthParams authParams, int attemptNumber) {
        if (attemptNumber >= MAX_RETRY_ATTEMPTS) {
            throw ConnectionException("Maximum retry attempts exceeded (" + MAX_RETRY_ATTEMPTS + ")")
        }
        
        Log.d(TAG, "Connection attempt " + (attemptNumber + 1) + "/" + MAX_RETRY_ATTEMPTS)
        
        return CompletableFuture
            .supplyAsync(() -> performActualConnection(authParams), executor)
            .handle((result, throwable) -> {
                if (throwable != null) {
                    Log.w(TAG, "Connection attempt " + (attemptNumber + 1) + " failed: " + throwable.getMessage())
                    
                    if (attemptNumber < MAX_RETRY_ATTEMPTS - 1) {
                        // Calculate exponential backoff delay
                        long delay = (long)(INITIAL_RETRY_DELAY_MS * Math.pow(RETRY_BACKOFF_MULTIPLIER, attemptNumber))
                        Log.i(TAG, "Retrying connection in " + delay + "ms")
                        
                        try {
                            Thread.sleep(delay)
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt()
                            throw ConnectionException("Connection retry interrupted")
                        }
                        
                        setState(ConnectionState.RECONNECTING)
                        return attemptConnectionWithRetry(authParams, attemptNumber + 1).join()
                    } else {
                        throw ConnectionException("All connection attempts failed", throwable)
                    }
                }
                
                return result
            })
    }

    private SLAuthReply performActualConnection(SLAuthParams authParams) {
        setState(ConnectionState.AUTHENTICATING)
        
        // Use the corrected login URL if needed
        SLAuthParams correctedParams = ensureCorrectLoginURL(authParams)
        
        try {
            // Use the existing authentication system but with modern error handling
            com.lumiyaviewer.lumiya.slproto.auth.SLAuth auth = new com.lumiyaviewer.lumiya.slproto.auth.SLAuth()
            SLAuthReply result = auth.Login(correctedParams)
            
            if (result == null) {
                throw ConnectionException("Authentication returned null response")
            }
            
            if (!result.success) {
                throw ConnectionException("Authentication failed: " + result.message)
            }
            
            return result
            
        } catch (Exception e) {
            throw ConnectionException("Connection failed during authentication", e)
        }
    }

    private SLAuthParams ensureCorrectLoginURL(SLAuthParams original) {
        String loginURL = original.loginURL
        
        // Fix common login URL issues
        if (loginURL == null || loginURL.isEmpty()) {
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
        if (!loginURL.equals(original.loginURL)) {
            Log.i(TAG, "Creating new auth params with corrected login URL")
            return SLAuthParams(
                original.loginName,
                original.passwordHash,
                original.clientID,
                original.startLocation,
                loginURL,
                original.gridName
            )
        }
        
        return original
    }

    private void setState(ConnectionState newState) {
        if (this.state != newState) {
            ConnectionState oldState = this.state
            this.state = newState
            Log.d(TAG, "Connection state changed: " + oldState + " -> " + newState)
            // TODO: Emit state change event for UI updates
        }
    }

    ConnectionState getState() {
        return state
    }
    
    String getLastError() {
        return lastError
    }
    
    int getActiveConnectionCount() {
        return activeConnections.get()
    }

    void shutdown() {
        Log.i(TAG, "Shutting down connection manager")
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (InterruptedException e) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    /**
     * Custom exception for connection-related errors
     */
    class ConnectionException extends RuntimeException {
        ConnectionException(String message) {
            super(message)
        }
        
        ConnectionException(String message, Throwable cause) {
            super(message, cause)
        }
    }
}
