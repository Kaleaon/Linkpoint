package com.linkpoint.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.core.ConnectionState
import com.linkpoint.ui.world.WorldViewActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/**
 * Background service to maintain grid connection
 * Based on the reference viewer's GridConnectionService
 */
class GridConnectionService : Service() {
    
    companion object {
        private const val TAG = "GridConnectionService"
        private const val NOTIFICATION_ID = 1002  // Unique ID (LinkpointConnectionService uses 1001)
        private const val CHANNEL_ID = "linkpoint_connection"
        
        fun start(context: Context) {
            val intent = Intent(context, GridConnectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            context.stopService(Intent(context, GridConnectionService::class.java))
        }
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var keepAliveJob: Job? = null
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "GridConnectionService created")
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Connecting..."))
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "GridConnectionService started")
        
        startKeepAlive()
        observeConnectionState()
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "GridConnectionService destroyed")
        
        keepAliveJob?.cancel()
        serviceScope.cancel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Grid Connection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Maintains connection to Second Life grid"
                setShowBadge(false)
            }
            
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(status: String): Notification {
        val intent = Intent(this, WorldViewActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Linkpoint")
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification(status: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification(status))
    }
    
    private fun startKeepAlive() {
        keepAliveJob?.cancel()
        keepAliveJob = serviceScope.launch {
            while (isActive) {
                // Send keep-alive packet
                sendKeepAlive()
                
                // Wait 30 seconds
                delay(30_000)
            }
        }
    }
    
    private suspend fun sendKeepAlive() {
        if (app.sessionManager.isConnected()) {
            // Send UDP keep-alive to simulator
            try {
                // Send AgentUpdate message as keep-alive
                app.udpConnection.sendAgentUpdate()
                Log.d(TAG, "Sending keep-alive")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send keep-alive", e)
            }
        }
    }
    
    private fun observeConnectionState() {
        serviceScope.launch {
            app.sessionManager.connectionState.collectLatest { state ->
                val status = when (state) {
                    ConnectionState.CONNECTED -> {
                        val region = app.sessionManager.currentRegion.value?.name ?: "Unknown"
                        "Connected to $region"
                    }
                    ConnectionState.CONNECTING -> "Connecting..."
                    ConnectionState.RECONNECTING -> "Reconnecting..."
                    ConnectionState.DISCONNECTED -> {
                        stopSelf()
                        "Disconnected"
                    }
                    ConnectionState.ERROR -> "Connection error"
                }
                updateNotification(status)
            }
        }
    }
}
