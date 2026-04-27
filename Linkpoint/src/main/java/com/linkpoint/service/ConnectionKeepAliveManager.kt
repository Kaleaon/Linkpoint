package com.linkpoint.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Connection Keep-Alive Manager - Prevents disconnection during background/idle.
 * 
 * Handles:
 * - Regular agent update pings to keep UDP connection alive
 * - Network change detection and reconnection
 * - Doze mode handling
 * - Screen off handling
 * - Graceful degradation when battery low
 */
class ConnectionKeepAliveManager(
    private val context: Context,
    private val udpConnection: UDPConnectionFixed
) {
    companion object {
        private const val TAG = "KeepAliveManager"
        
        // Ping intervals
        const val ACTIVE_PING_INTERVAL_MS = 5_000L      // 5 seconds when active
        const val BACKGROUND_PING_INTERVAL_MS = 15_000L // 15 seconds in background
        const val IDLE_PING_INTERVAL_MS = 25_000L       // 25 seconds when idle (must be < 30s SL timeout)
        
        // Timeout thresholds
        const val CONNECTION_TIMEOUT_MS = 60_000L  // Consider disconnected after 60s no response
        const val RECONNECT_DELAY_MS = 5_000L      // Wait before reconnect attempt
    }
    
    /**
     * Coroutine scope for background operations.
     * 
     * Uses IO dispatcher for network operations and SupervisorJob to ensure
     * failures don't cancel the entire scope. This is managed by the caller
     * (typically the Activity or Fragment) which should cancel this scope
     * when appropriate to prevent memory leaks.
     */
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Main thread scope for UI updates.
     * 
     * Replaces the legacy Handler approach with coroutine-based UI updates.
     * All UI updates should use withContext(Dispatchers.Main) instead of
     * posting to a Handler. This provides better integration with structured
     * concurrency and automatic cancellation.
     */
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // State
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _isInBackground = MutableStateFlow(false)
    val isInBackground: StateFlow<Boolean> = _isInBackground
    
    // Timing
    private var lastPingSentTime = 0L
    private var lastPongReceivedTime = 0L
    private var lastUserActivityTime = 0L
    
    // Jobs
    private var pingJob: Job? = null
    private var timeoutCheckJob: Job? = null
    
    // Network callback
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    // Agent info for pings
    private var agentId: UUID? = null
    private var sessionId: UUID? = null
    
    // Listeners
    private val stateListeners = mutableListOf<ConnectionStateListener>()
    
    /**
     * Initialize with agent credentials.
     */
    fun initialize(agentId: UUID, sessionId: UUID) {
        this.agentId = agentId
        this.sessionId = sessionId
        
        registerNetworkCallback()
        startPingLoop()
        startTimeoutCheck()
        
        _connectionState.value = ConnectionState.CONNECTED
        Log.i(TAG, "Keep-alive manager initialized")
    }
    
    /**
     * Record that app is in foreground.
     */
    fun onForeground() {
        _isInBackground.value = false
        lastUserActivityTime = System.currentTimeMillis()
        
        // Send immediate ping when returning to foreground
        scope.launch {
            sendPing()
        }
        
        Log.d(TAG, "App in foreground")
    }
    
    /**
     * Record that app is in background.
     */
    fun onBackground() {
        _isInBackground.value = true
        Log.d(TAG, "App in background")
    }
    
    /**
     * Record user activity (touch, input, etc).
     */
    fun recordUserActivity() {
        lastUserActivityTime = System.currentTimeMillis()
    }
    
    /**
     * Handle pong/ack received from server.
     */
    fun onPongReceived() {
        lastPongReceivedTime = System.currentTimeMillis()
        
        if (_connectionState.value == ConnectionState.RECONNECTING) {
            _connectionState.value = ConnectionState.CONNECTED
            notifyStateChange(ConnectionState.CONNECTED)
            Log.i(TAG, "Connection restored")
        }
    }
    
    /**
     * Notify that a critical connection issue was detected.
     * This is called when the UDP socket becomes invalid (e.g., "Operation not permitted" errors).
     */
    fun notifyConnectionIssue() {
        Log.w(TAG, "⚠️ Critical connection issue detected - socket may be invalid")
        _connectionState.value = ConnectionState.ERROR
        notifyStateChange(ConnectionState.ERROR)
        
        // Attempt reconnection through the keep-alive mechanism
        scope.launch {
            delay(RECONNECT_DELAY_MS)
            attemptReconnect()
        }
    }
    
    private fun startPingLoop() {
        pingJob?.cancel()
        pingJob = scope.launch {
            while (isActive) {
                try {
                    val interval = calculatePingInterval()
                    delay(interval)
                    sendPing()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Ping error", e)
                    delay(BACKGROUND_PING_INTERVAL_MS)
                }
            }
        }
    }
    
    private fun startTimeoutCheck() {
        timeoutCheckJob?.cancel()
        timeoutCheckJob = scope.launch {
            while (isActive) {
                delay(10_000L) // Check every 10 seconds
                checkConnectionTimeout()
            }
        }
    }
    
    private fun calculatePingInterval(): Long {
        val timeSinceActivity = System.currentTimeMillis() - lastUserActivityTime
        
        return when {
            !_isInBackground.value && timeSinceActivity < 60_000L -> ACTIVE_PING_INTERVAL_MS
            !_isInBackground.value -> BACKGROUND_PING_INTERVAL_MS
            timeSinceActivity < 5 * 60_000L -> BACKGROUND_PING_INTERVAL_MS
            else -> IDLE_PING_INTERVAL_MS
        }
    }
    
    private suspend fun sendPing() {
        try {
            // Send standard AgentUpdate message for keep-alive
            udpConnection.sendAgentUpdate()
            
            lastPingSentTime = System.currentTimeMillis()
            Log.v(TAG, "Ping sent")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send ping", e)
        }
    }
    
    private fun checkConnectionTimeout() {
        if (_connectionState.value == ConnectionState.DISCONNECTED) return
        
        val timeSinceLastPong = System.currentTimeMillis() - lastPongReceivedTime
        
        if (timeSinceLastPong > CONNECTION_TIMEOUT_MS) {
            if (_connectionState.value != ConnectionState.RECONNECTING) {
                Log.w(TAG, "Connection timeout detected (${timeSinceLastPong}ms since last response)")
                _connectionState.value = ConnectionState.RECONNECTING
                notifyStateChange(ConnectionState.RECONNECTING)
                
                // Attempt reconnection
                scope.launch {
                    attemptReconnect()
                }
            }
        }
    }
    
    private suspend fun attemptReconnect() {
        Log.i(TAG, "Attempting reconnection...")
        
        // Try sending multiple pings
        repeat(3) { attempt ->
            delay(RECONNECT_DELAY_MS)
            sendPing()
            
            // Wait for response
            delay(5_000L)
            
            if (System.currentTimeMillis() - lastPongReceivedTime < 10_000L) {
                Log.i(TAG, "Reconnection successful on attempt ${attempt + 1}")
                return
            }
        }
        
        // All attempts failed
        Log.e(TAG, "Reconnection failed after 3 attempts")
        _connectionState.value = ConnectionState.DISCONNECTED
        notifyStateChange(ConnectionState.DISCONNECTED)
    }
    
    private fun registerNetworkCallback() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available")
                // Network restored - send ping immediately
                scope.launch {
                    delay(1000L) // Brief delay for network to stabilize
                    sendPing()
                }
            }
            
            override fun onLost(network: Network) {
                Log.w(TAG, "Network lost")
                _connectionState.value = ConnectionState.RECONNECTING
                notifyStateChange(ConnectionState.RECONNECTING)
            }
            
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                Log.d(TAG, "Network capabilities changed: hasInternet=$hasInternet")
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        // Validate callback was created successfully
        val callback = networkCallback 
            ?: throw IllegalStateException("Network callback was not initialized")
        connectivityManager.registerNetworkCallback(request, callback)
    }
    
    private fun unregisterNetworkCallback() {
        networkCallback?.let { callback ->
            try {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering network callback", e)
            }
        }
        networkCallback = null
    }
    
    fun addStateListener(listener: ConnectionStateListener) {
        stateListeners.add(listener)
    }
    
    fun removeStateListener(listener: ConnectionStateListener) {
        stateListeners.remove(listener)
    }
    
    /**
     * Notify all registered listeners of a connection state change.
     * 
     * Uses mainScope to ensure notifications run on the main thread,
     * replacing the legacy Handler.post() approach with coroutine-based
     * execution for better integration with structured concurrency.
     */
    private fun notifyStateChange(state: ConnectionState) {
        mainScope.launch {
            stateListeners.forEach { it.onConnectionStateChanged(state) }
        }
    }
    
    /**
     * Shutdown the keep-alive manager and cancel all operations.
     * 
     * Cancels both background and main thread scopes to prevent memory leaks
     * and ensure proper cleanup of resources.
     */
    fun shutdown() {
        pingJob?.cancel()
        timeoutCheckJob?.cancel()
        unregisterNetworkCallback()
        scope.cancel()
        mainScope.cancel()
        
        _connectionState.value = ConnectionState.DISCONNECTED
        Log.i(TAG, "Keep-alive manager shutdown")
    }
}

// ConnectionState is now unified in com.linkpoint.network.events.ConnectionState
// typealias provided for backwards compatibility with existing imports.
typealias ConnectionState = com.linkpoint.network.events.ConnectionState

/**
 * Connection state change listener.
 */
fun interface ConnectionStateListener {
    fun onConnectionStateChanged(state: ConnectionState)
}
