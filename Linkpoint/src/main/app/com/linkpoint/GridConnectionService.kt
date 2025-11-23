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
import com.linkpoint.eventbus.EventBus
import com.linkpoint.eventbus.EventHandler
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.events.SLConnectionStateChangedEvent
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.voice.LinkpointVoiceManager

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
class GridConnectionService : Service(), LinkpointVoiceManager.VoiceCallback {

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
        const val EXTRA_CHANNEL_URI = "channel_uri"
        const val EXTRA_AUTH_TOKEN = "auth_token"
        
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
    private var reconnectJob: Job? = null
    
    // Network monitoring
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    // Notification manager
    private var notificationManager: NotificationManager? = null
    
    // Voice Manager
    private lateinit var voiceManager: LinkpointVoiceManager

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "GridConnectionService created")
        
        setServiceInstance(this)
        isRunning.set(true)
        
        // Initialize managers
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Initialize Voice Manager
        voiceManager = LinkpointVoiceManager(this, this)
        serviceScope.launch {
            voiceManager.initialize()
        }
        
        // Create notification channel
        createNotificationChannel()
        
        // Register network callback
        registerNetworkCallback()

        // Subscribe to EventBus
        EventBus.getInstance().subscribe(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: action=${intent?.action}")
        
        when (intent?.action) {
            LOGIN_ACTION -> handleLogin(intent)
            LOGOUT_ACTION -> handleLogout()
            RECONNECT_ACTION -> handleReconnect()
            ACTION_VOICE_ACCEPT -> handleVoiceAccept(intent)
            ACTION_VOICE_REJECT -> handleVoiceReject(intent)
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
        reconnectJob?.cancel()
        serviceScope.cancel()
        
        // Unregister network callback
        unregisterNetworkCallback()
        
        // Unsubscribe from EventBus
        EventBus.getInstance().unsubscribe(this)
        
        // Cleanup voice
        voiceManager.cleanup()

        // Clear connection
        setGridConnection(null)
        setServiceInstance(null)
        
        // Stop foreground
        stopForeground(true)
    }
    
    // Event Handlers
    @EventHandler
    fun onLoginResult(event: SLLoginResultEvent) {
        if (event.success) {
            Log.i(TAG, "Login successful via EventBus")
            updateConnectionState(ConnectionState.CONNECTED)
            reconnectAttempts = 0
        } else {
            Log.e(TAG, "Login failed via EventBus: ${event.message}")
            updateConnectionState(ConnectionState.ERROR)
            // If we were reconnecting, maybe retry? But SLLoginResultEvent usually means final result.
            stopSelf()
        }
    }

    @EventHandler
    fun onDisconnect(event: SLDisconnectEvent) {
        Log.i(TAG, "Disconnected: ${event.message}, requested=${event.requested}")
        updateConnectionState(ConnectionState.DISCONNECTED)
        if (!event.requested) {
            // Unexpected disconnect
            handleReconnect()
        } else {
            stopSelf()
        }
    }

    @EventHandler
    fun onConnectionStateChanged(event: SLConnectionStateChangedEvent) {
        Log.d(TAG, "SL Grid Connection state changed: ${event.state}")
        // We could sync service state here if needed, but LoginResult/Disconnect cover most cases.
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
                Log.i(TAG, "Creating SLGridConnection...")
                val connection = SLGridConnection()
                
                // Use a persistent ID if available or generate new one. 
                // Ideally this should come from AuthenticationManager or similar.
                val clientID = java.util.UUID.randomUUID()

                val params = SLAuthParams(
                    username, 
                    password, 
                    clientID,
                    startLocation ?: "last",
                    gridUrl, // loginURL
                    "Grid" // gridName
                )

                setGridConnection(connection)
                connection.connect(params)
                
                Log.i(TAG, "Connect request sent")
                
            } catch (e: Exception) {
                Log.e(TAG, "Login initiation failed", e)
                updateConnectionState(ConnectionState.ERROR)
                stopSelf()
            }
        }
    }
    
    private fun handleLogout() {
        Log.i(TAG, "Initiating logout")
        
        // Disconnect
        serviceScope.launch {
            try {
                getGridConnection()?.disconnect()
                // connectionRef will be cleared in onDestroy or after disconnect event
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
            } finally {
                // We wait for disconnect event or force stop?
                // Usually disconnect() is async.
                // If we stopSelf immediately, we might kill the disconnect process.
                // But disconnect event should come back and call stopSelf.
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
                    val connection = getGridConnection()
                    if (connection != null) {
                        if (!connection.Reconnect()) {
                            Log.w(TAG, "SLGridConnection.Reconnect() returned false, possibly fatal error or not set up")
                            // Try full relogin?
                            // For now, just fail
                            updateConnectionState(ConnectionState.ERROR)
                            stopSelf()
                        }
                    } else {
                        Log.e(TAG, "Cannot reconnect, no connection object")
                        stopSelf()
                    }
                    
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
    
    private fun handleVoiceAccept(intent: Intent) {
        Log.d(TAG, "Voice call accepted")
        val channelUri = intent.getStringExtra(EXTRA_CHANNEL_URI)
        val authToken = intent.getStringExtra(EXTRA_AUTH_TOKEN) ?: ""
        
        if (!channelUri.isNullOrEmpty()) {
            serviceScope.launch {
                voiceManager.connectToVoiceChannel(channelUri, authToken)
            }
        } else {
            Log.e(TAG, "Accept voice call failed: missing channel URI")
        }
    }
    
    private fun handleVoiceReject(intent: Intent) {
        Log.d(TAG, "Voice call rejected")
        // For rejection, we might just need to ensure we aren't connected
        // or maybe send a message if supported. For now, just log.
        val channelUri = intent.getStringExtra(EXTRA_CHANNEL_URI)
        if (!channelUri.isNullOrEmpty()) {
            serviceScope.launch {
                voiceManager.leaveVoiceChannel(channelUri)
            }
        }
    }
    
    // VoiceCallback implementation
    override fun onVoiceConnected(channelUri: String) {
        Log.i(TAG, "Voice connected: $channelUri")
        // Optionally notify UI or show notification
    }

    override fun onVoiceDisconnected(channelUri: String, reason: String) {
        Log.i(TAG, "Voice disconnected: $channelUri, reason: $reason")
    }

    override fun onUserJoined(channelUri: String, userId: String, displayName: String) {
        Log.d(TAG, "Voice user joined: $displayName")
    }

    override fun onUserLeft(channelUri: String, userId: String) {
        Log.d(TAG, "Voice user left: $userId")
    }

    override fun onUserSpeaking(userId: String, speaking: Boolean) {
        // Handle speaking indicators
    }

    override fun onVoiceError(error: String) {
        Log.e(TAG, "Voice error: $error")
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
                    // Force disconnect logic in SLGridConnection might handle this,
                    // but we should signal it.
                    getGridConnection()?.ForceDisconnect(false)
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
