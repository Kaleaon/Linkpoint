package com.linkpoint.protocol.messages

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Robust UDP Connection with automatic reconnection and error handling.
 * 
 * Features:
 * - Automatic reconnection with exponential backoff
 * - Connection state tracking
 * - Graceful error handling
 * - Thread-safe operations
 * - Heartbeat/ping mechanism
 * 
 * Thread-safe: All operations are thread-safe
 * 
 * @property simIP Simulator IP address
 * @property simPort Simulator port
 */
class RobustUDPConnection(
    private val simIP: String,
    private val simPort: Int
) {
    companion object {
        private const val TAG = "RobustUDPConnection"
        
        // Reconnection settings
        private const val MAX_RECONNECT_ATTEMPTS = 5
        private const val INITIAL_RECONNECT_DELAY_MS = 2000L
        private const val MAX_RECONNECT_DELAY_MS = 30000L
        
        // Heartbeat settings
        private const val HEARTBEAT_INTERVAL_MS = 30000L
    }
    
    // Connection state
    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        RECONNECTING,
        FAILED
    }
    
    // State tracking
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError
    
    // Network components
    private var datagramChannel: DatagramChannel? = null
    private var receiveJob: Job? = null
    private var heartbeatJob: Job? = null
    
    // Reconnection tracking
    private val reconnectAttempts = AtomicInteger(0)
    private val isShuttingDown = AtomicBoolean(false)
    
    // Coroutine scope
    private val scope = CoroutineScope(
        Dispatchers.IO + 
        SupervisorJob() + 
        CoroutineName("RobustUDPConnection")
    )
    
    // Packet handling
    private val packetChannel = Channel<PacketData>(capacity = Channel.UNLIMITED)
    
    data class PacketData(
        val data: ByteArray,
        val timestamp: Long
    )
    
    /**
     * Connect to the simulator
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        if (isShuttingDown.get()) {
            return@withContext false
        }
        
        _connectionState.value = ConnectionState.CONNECTING
        _connectionError.value = null
        
        try {
            Log.d(TAG, "Connecting to $simIP:$simPort...")
            
            val address = InetSocketAddress(simIP, simPort)
            datagramChannel = DatagramChannel.open()
            datagramChannel?.configureBlocking(true)
            datagramChannel?.connect(address)
            
            // Start receive loop
            startReceiveLoop()
            
            // Start heartbeat
            startHeartbeat()
            
            _connectionState.value = ConnectionState.CONNECTED
            reconnectAttempts.set(0)
            
            Log.i(TAG, "✓ Connected to $simIP:$simPort")
            true
            
        } catch (e: Exception) {
            val error = "Connection failed: ${e.message}"
            Log.e(TAG, error, e)
            
            _connectionState.value = ConnectionState.FAILED
            _connectionError.value = error
            disconnectInternal()
            
            // Attempt reconnection
            scheduleReconnect()
            
            false
        }
    }
    
    /**
     * Disconnect from the simulator
     */
    fun disconnect() {
        if (isShuttingDown.compareAndSet(false, true)) {
            Log.d(TAG, "Disconnecting...")
            
            // Cancel jobs
            receiveJob?.cancel()
            heartbeatJob?.cancel()
            
            // Close channel
            disconnectInternal()
            
            // Cancel scope
            scope.cancel()
            
            _connectionState.value = ConnectionState.DISCONNECTED
            _connectionError.value = null
            
            Log.i(TAG, "✓ Disconnected")
        }
    }
    
    /**
     * Internal disconnect logic
     */
    private fun disconnectInternal() {
        try {
            datagramChannel?.close()
            datagramChannel = null
        } catch (e: Exception) {
            Log.e(TAG, "Error closing channel", e)
        }
    }
    
    /**
     * Send data
     */
    suspend fun sendData(data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        if (_connectionState.value != ConnectionState.CONNECTED) {
            Log.w(TAG, "Cannot send data - not connected")
            return@withContext false
        }
        
        try {
            val buffer = ByteBuffer.wrap(data)
            datagramChannel?.write(buffer)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send data", e)
            handleConnectionError("Send error: ${e.message}")
            false
        }
    }
    
    /**
     * Start the receive loop
     */
    private fun startReceiveLoop() {
        receiveJob = scope.launch {
            val buffer = ByteBuffer.allocate(4096)
            
            while (isActive && _connectionState.value == ConnectionState.CONNECTED) {
                try {
                    buffer.clear()
                    val bytesRead = datagramChannel?.read(buffer) ?: -1
                    
                    if (bytesRead > 0) {
                        buffer.flip()
                        val data = ByteArray(bytesRead)
                        buffer.get(data)
                        
                        // Send to packet channel
                        packetChannel.trySend(PacketData(data, System.currentTimeMillis()))
                            .isSuccess
                    }
                    
                } catch (e: Exception) {
                    if (!isShuttingDown.get()) {
                        Log.e(TAG, "Error receiving data", e)
                        handleConnectionError("Receive error: ${e.message}")
                    }
                    break
                }
            }
        }
    }
    
    /**
     * Start heartbeat mechanism
     */
    private fun startHeartbeat() {
        heartbeatJob = scope.launch {
            while (isActive && _connectionState.value == ConnectionState.CONNECTED) {
                delay(HEARTBEAT_INTERVAL_MS)
                
                if (_connectionState.value == ConnectionState.CONNECTED) {
                    sendHeartbeat()
                }
            }
        }
    }
    
    /**
     * Send heartbeat packet
     */
    private suspend fun sendHeartbeat() {
        // Send a simple heartbeat packet
        val heartbeatPacket = createHeartbeatPacket()
        val success = sendData(heartbeatPacket)
        
        if (!success && !isShuttingDown.get()) {
            handleConnectionError("Heartbeat failed")
        }
    }
    
    /**
     * Create heartbeat packet (implement based on protocol)
     */
    private fun createHeartbeatPacket(): ByteArray {
        // This should be implemented based on the SL protocol
        // For now, return empty array
        return ByteArray(0)
    }
    
    /**
     * Handle connection error
     */
    private fun handleConnectionError(error: String) {
        Log.e(TAG, "Connection error: $error")
        
        _connectionState.value = ConnectionState.FAILED
        _connectionError.value = error
        
        disconnectInternal()
        
        // Schedule reconnection
        if (!isShuttingDown.get()) {
            scheduleReconnect()
        }
    }
    
    /**
     * Schedule reconnection attempt
     */
    private fun scheduleReconnect() {
        val currentAttempts = reconnectAttempts.incrementAndGet()
        
        if (currentAttempts > MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Max reconnection attempts reached")
            _connectionState.value = ConnectionState.FAILED
            _connectionError.value = "Max reconnection attempts reached"
            return
        }
        
        _connectionState.value = ConnectionState.RECONNECTING
        
        // Calculate exponential backoff delay
        val reconnectDelayMs = minOf(
            INITIAL_RECONNECT_DELAY_MS * (1 shl (currentAttempts - 1)),
            MAX_RECONNECT_DELAY_MS
        )
        
        Log.i(TAG, "Reconnecting in ${reconnectDelayMs}ms (attempt $currentAttempts/$MAX_RECONNECT_ATTEMPTS)")
        
        scope.launch {
            delay(reconnectDelayMs)
            
            if (!isShuttingDown.get()) {
                Log.i(TAG, "Attempting reconnection...")
                connect()
            }
        }
    }
    
    /**
     * Get packet channel for receiving data
     */
    fun getPacketChannel(): Channel<PacketData> {
        return packetChannel
    }
    
    /**
     * Check if connected
     */
    fun isConnected(): Boolean {
        return _connectionState.value == ConnectionState.CONNECTED
    }
    
    /**
     * Get current connection state
     */
    fun getConnectionState(): ConnectionState {
        return _connectionState.value
    }
    
    /**
     * Get reconnection attempt count
     */
    fun getReconnectAttempts(): Int {
        return reconnectAttempts.get()
    }
}