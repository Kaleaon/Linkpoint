package com.linkpoint.network.core

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Manages network connection state flags.
 * Based on patterns from the official Second Life app's CoreNetworkingService.
 * 
 * Tracks:
 * - IsReconnecting
 * - ConnectionReset
 * - ConnectionIsFaulted
 * - ForceReconnect
 * - AlwaysReconnect
 */
class NetworkStateManager {
    
    companion object {
        private const val TAG = "NetworkStateManager"
        
        // Reset guard timeout to prevent rapid reset attempts
        private const val RESET_GUARD_TIMEOUT_MS = 5000L  // 5 seconds
    }
    
    /**
     * Connection state enum
     */
    enum class ConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        RECONNECTING,
        ERROR,
        FAULTED
    }
    
    // State flows for reactive updates
    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()
    
    private val _isReconnecting = MutableStateFlow(false)
    val isReconnecting: StateFlow<Boolean> = _isReconnecting.asStateFlow()
    
    private val _lastStatusChange = MutableStateFlow(0L)
    val lastStatusChange: StateFlow<Long> = _lastStatusChange.asStateFlow()
    
    // Atomic flags for thread-safe access
    private val connectionIsFaulted = AtomicBoolean(false)
    private val connectionReset = AtomicBoolean(false)
    private val forceReconnect = AtomicBoolean(false)
    private val alwaysReconnect = AtomicBoolean(true)  // Default to true
    private val logoutInProgress = AtomicBoolean(false)
    private val switchingAppsForceReconnect = AtomicBoolean(false)
    
    // Reconnection tracking
    private val reconnectCount = AtomicLong(0)
    private val lastReconnectAttempt = AtomicLong(0)
    private val lastResetRequest = AtomicLong(0)
    
    // Connection instance tracking
    private var connectionInstanceId: String = ""
    private var currentConnectionStartTime: Long = 0L
    
    /**
     * Set the connection status
     */
    @Synchronized
    fun setStatus(status: ConnectionStatus) {
        val previousStatus = _connectionStatus.value
        if (previousStatus != status) {
            Log.d(TAG, "Connection status: $previousStatus -> $status")
            _connectionStatus.value = status
            _lastStatusChange.value = System.currentTimeMillis()
            
            when (status) {
                ConnectionStatus.RECONNECTING -> {
                    _isReconnecting.value = true
                    reconnectCount.incrementAndGet()
                    lastReconnectAttempt.set(System.currentTimeMillis())
                }
                ConnectionStatus.CONNECTED -> {
                    _isReconnecting.value = false
                    connectionIsFaulted.set(false)
                    connectionReset.set(false)
                    forceReconnect.set(false)
                }
                ConnectionStatus.ERROR, ConnectionStatus.FAULTED -> {
                    connectionIsFaulted.set(true)
                }
                ConnectionStatus.DISCONNECTED -> {
                    _isReconnecting.value = false
                    currentConnectionStartTime = 0L
                }
                else -> {}
            }
        }
    }
    
    /**
     * Request a connection reset.
     * Returns false if a reset was recently requested (guard timeout).
     */
    fun requestConnectionReset(): Boolean {
        val now = System.currentTimeMillis()
        val lastReset = lastResetRequest.get()
        
        if (now - lastReset < RESET_GUARD_TIMEOUT_MS) {
            Log.w(TAG, "Reset guard active - reset requested ${now - lastReset}ms ago")
            return false
        }
        
        lastResetRequest.set(now)
        connectionReset.set(true)
        Log.d(TAG, "Connection reset requested")
        return true
    }
    
    /**
     * Clear the connection reset flag
     */
    fun clearConnectionReset() {
        connectionReset.set(false)
    }
    
    /**
     * Check if connection reset is requested
     */
    fun isConnectionResetRequested(): Boolean = connectionReset.get()
    
    /**
     * Set force reconnect flag
     */
    fun setForceReconnect(force: Boolean) {
        forceReconnect.set(force)
        if (force) {
            Log.d(TAG, "Force reconnect enabled")
        }
    }
    
    /**
     * Check if force reconnect is enabled
     */
    fun isForceReconnectEnabled(): Boolean = forceReconnect.get()
    
    /**
     * Set always reconnect flag
     */
    fun setAlwaysReconnect(always: Boolean) {
        alwaysReconnect.set(always)
    }
    
    /**
     * Check if always reconnect is enabled
     */
    fun isAlwaysReconnectEnabled(): Boolean = alwaysReconnect.get()
    
    /**
     * Set logout in progress flag
     */
    fun setLogoutInProgress(inProgress: Boolean) {
        logoutInProgress.set(inProgress)
        if (inProgress) {
            Log.d(TAG, "Logout in progress")
        }
    }
    
    /**
     * Check if logout is in progress
     */
    fun isLogoutInProgress(): Boolean = logoutInProgress.get()
    
    /**
     * Check if connection is faulted
     */
    fun isConnectionFaulted(): Boolean = connectionIsFaulted.get()
    
    /**
     * Set switching apps force reconnect flag
     */
    fun setSwitchingAppsForceReconnect(force: Boolean) {
        switchingAppsForceReconnect.set(force)
    }
    
    /**
     * Get the current connection instance ID
     */
    fun getConnectionInstanceId(): String = connectionInstanceId
    
    /**
     * Set a new connection instance ID
     */
    fun setConnectionInstanceId(id: String) {
        connectionInstanceId = id
        currentConnectionStartTime = System.currentTimeMillis()
        Log.d(TAG, "Connection instance ID: $id")
    }
    
    /**
     * Get reconnection count
     */
    fun getReconnectCount(): Long = reconnectCount.get()
    
    /**
     * Get time since last status change
     */
    fun getTimeSinceLastStatusChange(): Long {
        val lastChange = _lastStatusChange.value
        return if (lastChange > 0) System.currentTimeMillis() - lastChange else 0
    }
    
    /**
     * Get connection details for diagnostics
     */
    fun getConnectionDetails(): ConnectionDetails {
        return ConnectionDetails(
            status = _connectionStatus.value,
            isReconnecting = _isReconnecting.value,
            isFaulted = connectionIsFaulted.get(),
            isResetRequested = connectionReset.get(),
            forceReconnect = forceReconnect.get(),
            alwaysReconnect = alwaysReconnect.get(),
            logoutInProgress = logoutInProgress.get(),
            reconnectCount = reconnectCount.get(),
            connectionInstanceId = connectionInstanceId,
            connectionDurationMs = if (currentConnectionStartTime > 0) 
                System.currentTimeMillis() - currentConnectionStartTime else 0,
            lastStatusChangeMs = getTimeSinceLastStatusChange()
        )
    }
    
    /**
     * Log connection details for diagnostics
     */
    fun logConnectionDetails() {
        val details = getConnectionDetails()
        Log.d(TAG, "=== CONNECTION DETAILS ===")
        Log.d(TAG, "  Current Status: ${details.status}")
        Log.d(TAG, "  Is Reconnecting: ${details.isReconnecting}")
        Log.d(TAG, "  Connection Is Faulted: ${details.isFaulted}")
        Log.d(TAG, "  Connection Reset: ${details.isResetRequested}")
        Log.d(TAG, "  Force Reconnect: ${details.forceReconnect}")
        Log.d(TAG, "  Always Reconnect: ${details.alwaysReconnect}")
        Log.d(TAG, "  Logout In Progress: ${details.logoutInProgress}")
        Log.d(TAG, "  Reconnect Count: ${details.reconnectCount}")
        Log.d(TAG, "  Connection Instance ID: ${details.connectionInstanceId}")
        Log.d(TAG, "  Connection Duration: ${details.connectionDurationMs}ms")
        Log.d(TAG, "  Last Status Change: ${details.lastStatusChangeMs}ms ago")
    }
    
    /**
     * Reset all state
     */
    fun reset() {
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
        _isReconnecting.value = false
        _lastStatusChange.value = 0L
        connectionIsFaulted.set(false)
        connectionReset.set(false)
        forceReconnect.set(false)
        logoutInProgress.set(false)
        switchingAppsForceReconnect.set(false)
        reconnectCount.set(0)
        lastReconnectAttempt.set(0)
        lastResetRequest.set(0)
        connectionInstanceId = ""
        currentConnectionStartTime = 0L
        Log.d(TAG, "Network state reset")
    }
    
    data class ConnectionDetails(
        val status: ConnectionStatus,
        val isReconnecting: Boolean,
        val isFaulted: Boolean,
        val isResetRequested: Boolean,
        val forceReconnect: Boolean,
        val alwaysReconnect: Boolean,
        val logoutInProgress: Boolean,
        val reconnectCount: Long,
        val connectionInstanceId: String,
        val connectionDurationMs: Long,
        val lastStatusChangeMs: Long
    )
}
