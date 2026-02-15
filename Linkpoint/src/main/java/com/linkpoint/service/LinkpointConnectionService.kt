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
import com.linkpoint.push.PushEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LinkpointConnectionService : Service() {

    companion object {
        private const val TAG = "ConnectionService"

        const val NOTIFICATION_CHANNEL_ID = "linkpoint_connection"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.linkpoint.service.START"
        const val ACTION_STOP = "com.linkpoint.service.STOP"
        const val ACTION_PING = "com.linkpoint.service.PING"
        const val ACTION_PUSH_WAKE = "com.linkpoint.service.PUSH_WAKE"

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning

        fun start(context: Context) {
            val intent = Intent(context, LinkpointConnectionService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(intent)
            else context.startService(intent)
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
    private var pushWakeJob: Job? = null

    private var isConnected = false
    private var lastPingTime = 0L
    private var lastActivityTime = 0L

    private var connectionCallback: ConnectionCallback? = null

    inner class LocalBinder : Binder() {
        fun getService(): LinkpointConnectionService = this@LinkpointConnectionService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Connection service created")
        createNotificationChannel()
        BackgroundResumeScheduler.schedule(this)
        observePushWakeups()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundService()
            ACTION_STOP -> stopForegroundService()
            ACTION_PING -> performPing()
            ACTION_PUSH_WAKE -> {
                recordActivity()
                startForegroundService()
            }
        }
        return START_STICKY
    }

    private fun observePushWakeups() {
        pushWakeJob?.cancel()
        pushWakeJob = serviceScope.launch {
            PushEventBus.events.collect {
                if (BackgroundRuntimeConfig.isPushWakeupsEnabled(this@LinkpointConnectionService)) {
                    recordActivity()
                    startForegroundService()
                }
            }
        }
    }

    private fun startForegroundService() {
        val profile = BackgroundRuntimeConfig.getIntensityProfile(this)
        val notification = createNotification("Connected to Second Life")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        acquireWakeLock(profile.wakeLockTimeoutMs)
        startKeepAlive(profile)
        startConnectionCheck(profile)

        _isRunning.value = true
        lastActivityTime = System.currentTimeMillis()
    }

    private fun stopForegroundService() {
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
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createNotification(status: String): Notification {
        val openIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingOpenIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, LinkpointConnectionService::class.java).apply {
            action = ACTION_STOP
        }
        val pendingDisconnectIntent = PendingIntent.getService(
            this,
            1,
            disconnectIntent,
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
        getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, createNotification(status))
    }

    private fun acquireWakeLock(timeoutMs: Long) {
        if (wakeLock == null) {
            wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "Linkpoint::ConnectionWakeLock"
            ).apply { setReferenceCounted(false) }
        }
        wakeLock?.acquire(timeoutMs)
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
    }

    private fun startKeepAlive(profile: BackgroundRuntimeConfig.IntensityProfile) {
        keepAliveJob?.cancel()
        keepAliveJob = serviceScope.launch {
            while (isActive) {
                try {
                    performKeepAlive()
                    delay(if (isIdle()) profile.idleIntervalMs else profile.keepAliveIntervalMs)
                } catch (e: Exception) {
                    Log.e(TAG, "Keep-alive error", e)
                    delay(profile.keepAliveIntervalMs)
                }
            }
        }
    }

    private fun startConnectionCheck(profile: BackgroundRuntimeConfig.IntensityProfile) {
        connectionCheckJob?.cancel()
        connectionCheckJob = serviceScope.launch {
            while (isActive) {
                try {
                    checkConnection(profile)
                    delay(10_000L)
                } catch (e: Exception) {
                    Log.e(TAG, "Connection check error", e)
                }
            }
        }
    }

    private suspend fun performKeepAlive() {
        lastPingTime = System.currentTimeMillis()
        connectionCallback?.onSendPing()
    }

    private fun performPing() {
        serviceScope.launch { performKeepAlive() }
    }

    private fun checkConnection(profile: BackgroundRuntimeConfig.IntensityProfile) {
        val timeSinceLastPing = System.currentTimeMillis() - lastPingTime
        if (isConnected && timeSinceLastPing > profile.keepAliveIntervalMs * 3) {
            connectionCallback?.onConnectionStale()
            BackgroundResumeScheduler.schedule(this, immediate = true)
        }
    }

    private fun isIdle(): Boolean = System.currentTimeMillis() - lastActivityTime > 5 * 60 * 1000L

    fun setConnected(connected: Boolean) {
        isConnected = connected
        updateNotification(if (connected) "Connected to Second Life" else "Disconnected")
    }

    fun recordActivity() {
        lastActivityTime = System.currentTimeMillis()
    }

    fun setConnectionCallback(callback: ConnectionCallback?) {
        connectionCallback = callback
    }

    override fun onDestroy() {
        keepAliveJob?.cancel()
        connectionCheckJob?.cancel()
        pushWakeJob?.cancel()
        serviceScope.cancel()
        releaseWakeLock()
        _isRunning.value = false
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (isConnected) BackgroundResumeScheduler.schedule(this, immediate = true)
        else stopForegroundService()
        super.onTaskRemoved(rootIntent)
    }
}

interface ConnectionCallback {
    fun onSendPing()
    fun onConnectionStale()
}
