package com.linkpoint.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.linkpoint.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Foreground Service for maintaining Second Life connection in background.
 * 
 * This service ensures Linkpoint stays connected even when:
 * - App is in background
 * - Screen is off
 * - User switches to other apps
 * - Device enters doze mode
 * 
 * Features:
 * - Foreground notification (required for Android 8+)
 * - Wake lock to prevent CPU sleep during critical operations
 * - Connection keep-alive pings
 * - Automatic reconnection on network changes
 * - Battery-optimized idle mode
 */
class LinkpointConnectionService : Service() {
    
    companion object {
        private const val TAG = "ConnectionService"
        
        const val NOTIFICATION_CHANNEL_ID = "linkpoint_connection"
        const val NOTIFICATION_ID = 1001
        
        const val ACTION_START = "com.linkpoint.service.START"
        const val ACTION_STOP = "com.linkpoint.service.STOP"
        const val ACTION_PING = "com.linkpoint.service.PING"
        
        // Keep-alive interval (30 seconds - within SL timeout)
        const val KEEPALIVE_INTERVAL_MS = 30_000L
        
        // Ping interval when idle (60 seconds)
        const val IDLE_PING_INTERVAL_MS = 60_000L
        
        // Connection check interval
        const val CONNECTION_CHECK_INTERVAL_MS = 10_000L
        
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning
        
        fun start(context: Context) {
            val intent = Intent(context, LinkpointConnectionService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, LinkpointConnectionService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
    
    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    private var wakeLock: PowerManager.WakeLock? = null
    private var keepAliveJob: Job? = null
    private var connectionCheckJob: Job? = null
    
    // Connection state
    private var isConnected = false
    private var lastPingTime = 0L
    private var lastActivityTime = 0L
    
    // Callbacks
    private var connectionCallback: ConnectionCallback? = null
    
    inner class LocalBinder : Binder() {
        fun getService(): LinkpointConnectionService = this@LinkpointConnectionService
    }
    
    override fun onBind(intent: Intent?): IBinder = binder
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Connection service created")
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundService()
            ACTION_STOP -> stopForegroundService()
            ACTION_PING -> performPing()
        }
        
        // Restart if killed
        return START_STICKY
    }
    
    private fun startForegroundService() {
        Log.i(TAG, "Starting foreground connection service")
        
        val notification = createNotification("Connected to Second Life")
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        
        acquireWakeLock()
        startKeepAlive()
        startConnectionCheck()
        
        _isRunning.value = true
        lastActivityTime = System.currentTimeMillis()
    }
    
    private fun stopForegroundService() {
        Log.i(TAG, "Stopping foreground connection service")
        
        keepAliveJob?.cancel()
        connectionCheckJob?.cancel()
        releaseWakeLock()
        
        _isRunning.value = false
        
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Linkpoint Connection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Maintains connection to Second Life"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(status: String): Notification {
        // Intent to open the app
        val openIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingOpenIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Intent to disconnect
        val disconnectIntent = Intent(this, LinkpointConnectionService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingDisconnectIntent = PendingIntent.getService(
            this, 1, disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Linkpoint")
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(pendingOpenIntent)
            .addAction(0, "Disconnect", pendingDisconnectIntent)
            .build()
    }
    
    private fun updateNotification(status: String) {
        val notification = createNotification(status)
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Linkpoint::ConnectionWakeLock"
            ).apply {
                setReferenceCounted(false)
            }
        }
        
        // Acquire with timeout to prevent battery drain
        wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes max
        Log.d(TAG, "Wake lock acquired")
    }
    
    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
                Log.d(TAG, "Wake lock released")
            }
        }
    }
    
    private fun startKeepAlive() {
        keepAliveJob?.cancel()
        keepAliveJob = serviceScope.launch {
            while (isActive) {
                try {
                    performKeepAlive()
                    
                    // Use longer interval when idle
                    val interval = if (isIdle()) IDLE_PING_INTERVAL_MS else KEEPALIVE_INTERVAL_MS
                    delay(interval)
                } catch (e: Exception) {
                    Log.e(TAG, "Keep-alive error", e)
                    delay(KEEPALIVE_INTERVAL_MS)
                }
            }
        }
    }
    
    private fun startConnectionCheck() {
        connectionCheckJob?.cancel()
        connectionCheckJob = serviceScope.launch {
            while (isActive) {
                try {
                    checkConnection()
                    delay(CONNECTION_CHECK_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Connection check error", e)
                }
            }
        }
    }
    
    private suspend fun performKeepAlive() {
        // Refresh wake lock periodically
        if (wakeLock?.isHeld != true) {
            acquireWakeLock()
        }
        
        lastPingTime = System.currentTimeMillis()
        
        // Notify callback to send ping
        connectionCallback?.onSendPing()
        
        Log.v(TAG, "Keep-alive ping sent")
    }
    
    private fun performPing() {
        serviceScope.launch {
            performKeepAlive()
        }
    }
    
    private fun checkConnection() {
        val timeSinceLastPing = System.currentTimeMillis() - lastPingTime
        
        // If no ping for too long, connection might be dead
        if (isConnected && timeSinceLastPing > KEEPALIVE_INTERVAL_MS * 3) {
            Log.w(TAG, "Connection may be stale, requesting reconnect")
            connectionCallback?.onConnectionStale()
        }
    }
    
    private fun isIdle(): Boolean {
        val idleTime = System.currentTimeMillis() - lastActivityTime
        return idleTime > 5 * 60 * 1000L // 5 minutes
    }
    
    /**
     * Update connection state.
     */
    fun setConnected(connected: Boolean) {
        isConnected = connected
        updateNotification(if (connected) "Connected to Second Life" else "Disconnected")
    }
    
    /**
     * Record user activity (resets idle timer).
     */
    fun recordActivity() {
        lastActivityTime = System.currentTimeMillis()
    }
    
    /**
     * Set connection callback.
     */
    fun setConnectionCallback(callback: ConnectionCallback?) {
        connectionCallback = callback
    }
    
    override fun onDestroy() {
        Log.i(TAG, "Connection service destroyed")
        
        keepAliveJob?.cancel()
        connectionCheckJob?.cancel()
        serviceScope.cancel()
        releaseWakeLock()
        
        _isRunning.value = false
        
        super.onDestroy()
    }
    
    override fun onTaskRemoved(rootIntent: Intent?) {
        // App was swiped away - keep service running if connected
        if (isConnected) {
            Log.i(TAG, "App removed but staying connected")
        } else {
            stopForegroundService()
        }
        super.onTaskRemoved(rootIntent)
    }
}

/**
 * Callback for connection events.
 */
interface ConnectionCallback {
    fun onSendPing()
    fun onConnectionStale()
}
