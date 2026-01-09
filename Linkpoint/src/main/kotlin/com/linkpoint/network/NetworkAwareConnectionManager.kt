package com.linkpoint.network

import android.content.Context
import android.util.Log
import com.linkpoint.eventbus.EventBus
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.events.SLConnectionStateChangedEvent
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.slproto.events.SLLoginResultEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/**
 * Network-aware connection manager that handles:
 * 1. Automatic reconnection when network becomes available
 * 2. Graceful disconnection when network is lost
 * 3. Network type switching (WiFi <-> Mobile)
 * 4. Connection retry with exponential backoff
 * 
 * This fixes issues with internet connection over mobile and WiFi networks by:
 * - Properly detecting network availability before attempting connections
 * - Handling network transitions without dropping connections unexpectedly
 * - Providing user-friendly error messages for network issues
 * - Implementing smart retry logic based on network conditions
 */
class NetworkAwareConnectionManager(
    private val context: Context,
    private val gridConnection: SLGridConnection
) {
    
    companion object {
        private const val TAG = "NetworkAwareConnMgr"
        private const val MAX_RETRY_ATTEMPTS = 5
        private const val INITIAL_RETRY_DELAY_MS = 2000L
        private const val MAX_RETRY_DELAY_MS = 30000L
        private const val NETWORK_SWITCH_GRACE_PERIOD_MS = 5000L
    }
    
    private val networkMonitor = NetworkStateMonitor.getInstance(context)
    private val eventBus = EventBus.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var currentAuthParams: SLAuthParams? = null
    private var isConnecting = false
    private var retryAttempts = 0
    private var lastNetworkType: NetworkStateMonitor.NetworkType = NetworkStateMonitor.NetworkType.NONE
    private var networkSwitchJob: Job? = null
    private var autoReconnectEnabled = true
    
    /**
     * Initialize the network-aware connection manager
     */
    fun initialize() {
        Log.i(TAG, "Initializing network-aware connection manager")
        networkMonitor.startMonitoring()
        
        // Monitor network state changes
        scope.launch {
            networkMonitor.networkState.collectLatest { state ->
                handleNetworkStateChange(state)
            }
        }
    }
    
    /**
     * Shutdown the connection manager
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down network-aware connection manager")
        networkMonitor.stopMonitoring()
        scope.cancel()
    }
    
    /**
     * Enable or disable automatic reconnection
     */
    fun setAutoReconnect(enabled: Boolean) {
        autoReconnectEnabled = enabled
        Log.i(TAG, "Auto-reconnect ${if (enabled) "enabled" else "disabled"}")
    }
    
    /**
     * Attempt to connect with network awareness
     */
    suspend fun connect(authParams: SLAuthParams): ConnectionResult {
        currentAuthParams = authParams
        retryAttempts = 0
        
        val networkState = networkMonitor.getCurrentState()
        
        // Check network availability first
        if (!networkState.isConnected) {
            Log.w(TAG, "Cannot connect: No network available")
            return ConnectionResult.NoNetwork(
                "No internet connection available. Please check your WiFi or mobile data settings."
            )
        }
        
        if (!networkState.hasInternet) {
            Log.w(TAG, "Cannot connect: Network has no internet access")
            return ConnectionResult.NoInternet(
                "Connected to ${networkState.networkType} but no internet access. " +
                "Please check your network settings."
            )
        }
        
        // Perform connectivity test
        val testResult = networkMonitor.testSecondLifeConnectivity()
        if (!testResult.success && !testResult.secondLifeReachable) {
            Log.w(TAG, "Second Life servers not reachable: ${testResult.message}")
            // Still try to connect - the test might have temporary issues
        }
        
        return attemptConnection(authParams)
    }
    
    private suspend fun attemptConnection(authParams: SLAuthParams): ConnectionResult {
        if (isConnecting) {
            Log.d(TAG, "Connection already in progress")
            return ConnectionResult.AlreadyConnecting()
        }
        
        isConnecting = true
        
        return try {
            Log.i(TAG, "Attempting connection to ${authParams.gridName}")
            
            // Emit connecting state
            eventBus.publish(SLConnectionStateChangedEvent(SLGridConnection.ConnectionState.Connecting))
            
            // Use the grid connection to connect
            withContext(Dispatchers.IO) {
                gridConnection.Connect(authParams)
            }
            
            // Wait for connection result
            // In real implementation, this would be handled by event handlers
            delay(1000) // Brief delay to allow connection to establish
            
            when (gridConnection.connectionState) {
                SLGridConnection.ConnectionState.Connected -> {
                    Log.i(TAG, "Successfully connected")
                    retryAttempts = 0
                    lastNetworkType = networkMonitor.getCurrentState().networkType
                    ConnectionResult.Success()
                }
                SLGridConnection.ConnectionState.Connecting -> {
                    Log.d(TAG, "Connection still in progress")
                    ConnectionResult.Success() // Will be updated by events
                }
                else -> {
                    Log.w(TAG, "Connection failed")
                    ConnectionResult.Failed("Connection failed. Please try again.")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection error", e)
            ConnectionResult.Failed("Connection error: ${e.message}")
        } finally {
            isConnecting = false
        }
    }
    
    private suspend fun handleNetworkStateChange(state: NetworkStateMonitor.NetworkState) {
        Log.d(TAG, "Network state changed: $state")
        
        val wasConnected = gridConnection.connectionState == SLGridConnection.ConnectionState.Connected
        
        if (!state.isConnected) {
            // Network lost
            Log.w(TAG, "Network lost")
            
            if (wasConnected) {
                // Give a grace period for network switching (e.g., WiFi to Mobile)
                handleNetworkLoss()
            }
            return
        }
        
        // Network type changed (e.g., WiFi to Mobile transition)
        if (state.networkType != lastNetworkType && lastNetworkType != NetworkStateMonitor.NetworkType.NONE) {
            Log.i(TAG, "Network type changed from $lastNetworkType to ${state.networkType}")
            handleNetworkTypeChange(state)
        }
        
        lastNetworkType = state.networkType
        
        // Network available and we have pending auth params - try to reconnect
        if (state.isUsable && !wasConnected && currentAuthParams != null && autoReconnectEnabled) {
            Log.i(TAG, "Network available, attempting reconnection")
            attemptReconnection()
        }
    }
    
    private fun handleNetworkLoss() {
        // Cancel any pending network switch job
        networkSwitchJob?.cancel()
        
        // Start grace period for network to come back
        networkSwitchJob = scope.launch {
            Log.d(TAG, "Starting network loss grace period")
            delay(NETWORK_SWITCH_GRACE_PERIOD_MS)
            
            // If still no network after grace period, disconnect
            val currentState = networkMonitor.getCurrentState()
            if (!currentState.isConnected) {
                Log.w(TAG, "Network not recovered after grace period, disconnecting")
                gridConnection.forceDisconnect(false)
                eventBus.publish(SLDisconnectEvent(
                    false,
                    "Network connection lost. Will reconnect when network is available."
                ))
            }
        }
    }
    
    private suspend fun handleNetworkTypeChange(newState: NetworkStateMonitor.NetworkState) {
        // Cancel pending grace period job
        networkSwitchJob?.cancel()
        
        val wasConnected = gridConnection.connectionState == SLGridConnection.ConnectionState.Connected
        
        if (wasConnected && newState.isUsable) {
            Log.i(TAG, "Network type switched while connected, maintaining connection")
            // Connection should persist across network type changes
            // The underlying sockets might reconnect automatically
        }
    }
    
    private suspend fun attemptReconnection() {
        if (retryAttempts >= MAX_RETRY_ATTEMPTS) {
            Log.w(TAG, "Max reconnection attempts reached")
            eventBus.publish(SLDisconnectEvent(
                false,
                "Unable to reconnect after $MAX_RETRY_ATTEMPTS attempts. Please try manually."
            ))
            return
        }
        
        val authParams = currentAuthParams ?: return
        
        retryAttempts++
        val delay = calculateRetryDelay()
        
        Log.i(TAG, "Reconnection attempt $retryAttempts/$MAX_RETRY_ATTEMPTS after ${delay}ms")
        delay(delay)
        
        // Check network is still available
        if (!networkMonitor.getCurrentState().isUsable) {
            Log.d(TAG, "Network not available for reconnection")
            return
        }
        
        val result = attemptConnection(authParams)
        if (result is ConnectionResult.Failed) {
            // Schedule another retry
            if (autoReconnectEnabled) {
                attemptReconnection()
            }
        }
    }
    
    private fun calculateRetryDelay(): Long {
        // Exponential backoff with jitter
        val exponentialDelay = INITIAL_RETRY_DELAY_MS * (1 shl (retryAttempts - 1))
        val cappedDelay = minOf(exponentialDelay, MAX_RETRY_DELAY_MS)
        val jitter = (cappedDelay * 0.1 * Math.random()).toLong()
        return cappedDelay + jitter
    }
    
    /**
     * Get current network state for UI display
     */
    fun getNetworkState(): NetworkStateMonitor.NetworkState = networkMonitor.getCurrentState()
    
    /**
     * Get human-readable network status message
     */
    fun getNetworkStatusMessage(): String {
        val state = networkMonitor.getCurrentState()
        return when {
            !state.isConnected -> "No network connection"
            !state.hasInternet -> "No internet access"
            state.isWifi -> "Connected via WiFi"
            state.isMobile -> "Connected via Mobile Data"
            else -> "Connected"
        }
    }
    
    sealed class ConnectionResult {
        class Success : ConnectionResult()
        class AlreadyConnecting : ConnectionResult()
        data class NoNetwork(val message: String) : ConnectionResult()
        data class NoInternet(val message: String) : ConnectionResult()
        data class Failed(val message: String) : ConnectionResult()
    }
}