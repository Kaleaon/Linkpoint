package com.linkpoint

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.linkpoint.slproto.SLGridConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Grid Connection Service - Manages persistent connection to Second Life grid
 * 
 * This service handles:
 * - Connection lifecycle management (login, maintain, logout)
 * - Network state monitoring and reconnection
 * - Foreground service for persistent connection
 * - Voice chat integration
 * - Connection state broadcasting
 * 
 * The service runs as a foreground service when connected to ensure
 * the connection is maintained even when the app is in the background.
 */
class GridConnectionService : Service() {

    companion object {
        private const val TAG = "GridConnectionService"
        
        // Action constants
        const val LOGIN_ACTION = "com.linkpoint.action.LOGIN"
        const val LOGOUT_ACTION = "com.linkpoint.action.LOGOUT"
        const val RECONNECT_ACTION = "com.linkpoint.action.RECONNECT"
        const val ACTION_VOICE_ACCEPT = "com.linkpoint.voice.ACCEPT"
        const val ACTION_VOICE_REJECT = "com.linkpoint.voice.REJECT"
        
        // Extra keys
        const val EXTRA_USERNAME = "username"
        const val EXTRA_PASSWORD = "password"
        const val EXTRA_GRID_URL = "grid_url"
        const val EXTRA_START_LOCATION = "start_location"
        
        // Notification constants
        private const val NOTIFICATION_CHANNEL_ID = "grid_connection_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Grid Connection"
        private const val NOTIFICATION_ID = 1001
        
        // Reconnection constants
        private const val RECONNECT_DELAY_MS = 5000L
        private const val MAX_RECONNECT_ATTEMPTS = 5
        
        // Static references
        private val connectionRef = AtomicReference<SLGridConnection?>(null)
        private val serviceInstance = AtomicReference<GridConnectionService?>(null)
        private val visibleActivityCount = AtomicInteger(0)
        private val voiceLoginInfo = AtomicReference<Any?>(null)

        fun getGridConnection(): SLGridConnection? = connectionRef.get()

        fun setGridConnection(connection: SLGridConnection?) {
            connectionRef.set(connection)
            Log.d(TAG, "Grid connection ${if (connection != null) "set" else "cleared"}")
        }

        fun getServiceInstance(): GridConnectionService? = serviceInstance.get()

        fun setServiceInstance(service: GridConnectionService?) {
            serviceInstance.set(service)
        }

        fun hasVisibleActivities(): Boolean = visibleActivityCount.get() > 0
        
        fun incrementVisibleActivities() {
            visibleActivityCount.incrementAndGet()
        }
        
        fun decrementVisibleActivities() {
            visibleActivityCount.decrementAndGet()
        }

        fun setVoiceLoginInfo(info: Any?) {
            voiceLoginInfo.set(info)
        }
        
        fun getVoiceLoginInfo(): Any? = voiceLoginInfo.get()

        enum class LEDAction { NONE, PULSE, SOLID }
        
        enum class ConnectionState {
            DISCONNECTED,
            CONNECTING,
            CONNECTED,
            RECONNECTING,
            ERROR
        }
    }
    
    // Service state
    private val isRunning = AtomicBoolean(false)
    private val connectionState = AtomicReference(ConnectionState.DISCONNECTED)
    private var reconnectAttempts = 0
    
    // Coroutine scope for background tasks
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var heartbeatJob: Job? = null
    private var reconnectJob: Job? = null
    
    // Network monitoring
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    // Notification manager
    private var notificationManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "GridConnectionService created")
        
        setServiceInstance(this)
        isRunning.set(true)
        
        // Initialize managers
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel
        createNotificationChannel()
        
        // Register network callback
        registerNetworkCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: action=${intent?.action}")
        
        when (intent?.action) {
            LOGIN_ACTION -> handleLogin(intent)
            LOGOUT_ACTION -> handleLogout()
            RECONNECT_ACTION -> handleReconnect()
            ACTION_VOICE_ACCEPT -> handleVoiceAccept()
            ACTION_VOICE_REJECT -> handleVoiceReject()
            else -> Log.w(TAG, "Unknown action: ${intent?.action}")
        }
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "GridConnectionService destroyed")
        
        isRunning.set(false)
        
        // Cancel all jobs
        heartbeatJob?.cancel()
        reconnectJob?.cancel()
        serviceScope.cancel()
        
        // Unregister network callback
        unregisterNetworkCallback()
        
        // Clear connection
        setGridConnection(null)
        setServiceInstance(null)
        
        // Stop foreground
        stopForeground(true)
    }
    
    // Connection lifecycle methods
    
    private fun handleLogin(intent: Intent) {
        val username = intent.getStringExtra(EXTRA_USERNAME)
        val password = intent.getStringExtra(EXTRA_PASSWORD)
        val gridUrl = intent.getStringExtra(EXTRA_GRID_URL)
        val startLocation = intent.getStringExtra(EXTRA_START_LOCATION)
        
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            Log.e(TAG, "Login failed: missing credentials")
            updateConnectionState(ConnectionState.ERROR)
            return
        }
        
        Log.i(TAG, "Initiating login for user: $username")
        updateConnectionState(ConnectionState.CONNECTING)
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createNotification("Connecting to grid..."))
        
        serviceScope.launch {
            try {
                // TODO: Implement actual login logic with SLGridConnection
                // For now, simulate connection
                delay(2000)
                
                // Create connection (placeholder)
                // val connection = SLGridConnection.create(username, password, gridUrl, startLocation)
                // setGridConnection(connection)
                
                updateConnectionState(ConnectionState.CONNECTED)
                reconnectAttempts = 0
                
                // Start heartbeat
                startHeartbeat()
                
                Log.i(TAG, "Login successful")
                
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                updateConnectionState(ConnectionState.ERROR)
                stopSelf()
            }
        }
    }
    
    private fun handleLogout() {
        Log.i(TAG, "Initiating logout")
        
        updateConnectionState(ConnectionState.DISCONNECTED)
        
        // Stop heartbeat
        heartbeatJob?.cancel()
        heartbeatJob = null
        
        // Disconnect
        serviceScope.launch {
            try {
                getGridConnection()?.let { connection ->
                    // TODO: Implement proper disconnect logic
                    Log.d(TAG, "Disconnecting from grid")
                }
                
                setGridConnection(null)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
            } finally {
                stopSelf()
            }
        }
    }
    
    private fun handleReconnect() {
        if (connectionState.get() == ConnectionState.RECONNECTING) {
            Log.d(TAG, "Already reconnecting, ignoring request")
            return
        }
        
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Max reconnect attempts reached, giving up")
            updateConnectionState(ConnectionState.ERROR)
            stopSelf()
            return
        }
        
        Log.i(TAG, "Attempting reconnection (attempt ${reconnectAttempts + 1}/$MAX_RECONNECT_ATTEMPTS)")
        updateConnectionState(ConnectionState.RECONNECTING)
        
        reconnectJob?.cancel()
        reconnectJob = serviceScope.launch {
            delay(RECONNECT_DELAY_MS)
            
            if (isActive) {
                reconnectAttempts++
                
                try {
                    // TODO: Implement reconnection logic
                    // For now, simulate reconnection
                    delay(2000)
                    
                    updateConnectionState(ConnectionState.CONNECTED)
                    reconnectAttempts = 0
                    
                    Log.i(TAG, "Reconnection successful")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Reconnection failed", e)
                    
                    if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                        handleReconnect()
                    } else {
                        updateConnectionState(ConnectionState.ERROR)
                        stopSelf()
                    }
                }
            }
        }
    }
    
    private fun handleVoiceAccept() {
        Log.d(TAG, "Voice call accepted")
        // TODO: Implement voice call acceptance logic
    }
    
    private fun handleVoiceReject() {
        Log.d(TAG, "Voice call rejected")
        // TODO: Implement voice call rejection logic
    }
    
    // Heartbeat and monitoring
    
    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = serviceScope.launch {
            while (isActive && connectionState.get() == ConnectionState.CONNECTED) {
                try {
                    // Send heartbeat/keepalive
                    getGridConnection()?.let { connection ->
                        // TODO: Implement actual heartbeat logic
                        Log.v(TAG, "Heartbeat sent")
                    }
                    
                    delay(30000) // 30 seconds
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Heartbeat failed", e)
                    handleReconnect()
                    break
                }
            }
        }
    }
    
    // Network monitoring
    
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network available")
                
                if (connectionState.get() == ConnectionState.RECONNECTING) {
                    handleReconnect()
                }
            }
            
            override fun onLost(network: Network) {
                Log.w(TAG, "Network lost")
                
                if (connectionState.get() == ConnectionState.CONNECTED) {
                    updateConnectionState(ConnectionState.RECONNECTING)
                    handleReconnect()
                }
            }
        }
        
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback!!)
    }
    
    private fun unregisterNetworkCallback() {
        networkCallback?.let { callback ->
            try {
                connectivityManager?.unregisterNetworkCallback(callback)
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering network callback", e)
            }
        }
        networkCallback = null
    }
    
    // Notification management
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Maintains connection to Second Life grid"
                setShowBadge(false)
            }
            
            notificationManager?.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(message: String): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Linkpoint")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateConnectionState(newState: ConnectionState) {
        val oldState = connectionState.getAndSet(newState)
        
        if (oldState != newState) {
            Log.i(TAG, "Connection state changed: $oldState -> $newState")
            
            // Update notification
            val message = when (newState) {
                ConnectionState.DISCONNECTED -> "Disconnected"
                ConnectionState.CONNECTING -> "Connecting to grid..."
                ConnectionState.CONNECTED -> "Connected to grid"
                ConnectionState.RECONNECTING -> "Reconnecting..."
                ConnectionState.ERROR -> "Connection error"
            }
            
            if (newState != ConnectionState.DISCONNECTED) {
                notificationManager?.notify(NOTIFICATION_ID, createNotification(message))
            }
            
            // Broadcast state change
            broadcastConnectionState(newState)
        }
    }
    
    private fun broadcastConnectionState(state: ConnectionState) {
        val intent = Intent("com.linkpoint.CONNECTION_STATE_CHANGED").apply {
            putExtra("state", state.name)
        }
        sendBroadcast(intent)
    }
}
