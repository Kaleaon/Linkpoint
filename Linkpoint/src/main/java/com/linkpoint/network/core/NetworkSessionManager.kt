package com.linkpoint.network.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.PowerManager
import android.util.Log
import com.linkpoint.protocol.caps.CapEventQueue
import com.linkpoint.service.ConnectionKeepAliveManager
import com.linkpoint.service.LinkpointConnectionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * NetworkSessionManager - Coordinates background execution, Doze mode socket keepalives,
 * EventQueueGet long-poll loops, and rapid connection recovery across network interface changes.
 */
class NetworkSessionManager(
    private val context: Context,
    private val keepAliveManager: ConnectionKeepAliveManager? = null,
    private val eventQueue: CapEventQueue? = null,
    private val networkStateManager: NetworkStateManager? = null
) {
    companion object {
        private const val TAG = "NetworkSessionManager"
        const val BACKGROUND_POLL_INTERVAL_MS = 15_000L
        const val DOZE_POLL_INTERVAL_MS = 25_000L
        const val RECOVERY_TIMEOUT_MS = 5_000L
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isInBackground = MutableStateFlow(false)
    val isInBackground: StateFlow<Boolean> = _isInBackground.asStateFlow()

    private val _isDozeMode = MutableStateFlow(false)
    val isDozeMode: StateFlow<Boolean> = _isDozeMode.asStateFlow()

    private val _isForegroundServiceActive = MutableStateFlow(false)
    val isForegroundServiceActive: StateFlow<Boolean> = _isForegroundServiceActive.asStateFlow()

    private var dozeReceiver: BroadcastReceiver? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private val isInitialized = AtomicBoolean(false)

    fun initialize() {
        if (!isInitialized.compareAndSet(false, true)) return
        registerDozeReceiver()
        registerNetworkCallback()
        Log.i(TAG, "NetworkSessionManager initialized")
    }

    /**
     * Called when app transitions to background execution.
     * Starts Foreground Service and adjusts socket keepalive / EventQueue polling.
     */
    fun onBackground() {
        _isInBackground.value = true
        keepAliveManager?.onBackground()

        // Requirement 1: Start Foreground Service upon background transition
        try {
            LinkpointConnectionService.start(context)
            _isForegroundServiceActive.value = true
            Log.i(TAG, "Started ForegroundService (DATA_SYNC) on background transition")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start ForegroundService on background transition", e)
        }

        // Requirement 2: Adjust EventQueue polling & heartbeats
        eventQueue?.setAdaptiveBackgroundMode(true, BACKGROUND_POLL_INTERVAL_MS)
    }

    /**
     * Called when app returns to foreground.
     */
    fun onForeground() {
        _isInBackground.value = false
        keepAliveManager?.onForeground()
        eventQueue?.setAdaptiveBackgroundMode(false)
        Log.i(TAG, "App returned to foreground")
    }

    /**
     * Handle Doze mode state change.
     */
    fun onDozeModeChanged(isDoze: Boolean) {
        _isDozeMode.value = isDoze
        Log.i(TAG, "Doze mode state changed: isDoze=$isDoze")
        if (isDoze) {
            eventQueue?.setAdaptiveBackgroundMode(true, DOZE_POLL_INTERVAL_MS)
        } else if (_isInBackground.value) {
            eventQueue?.setAdaptiveBackgroundMode(true, BACKGROUND_POLL_INTERVAL_MS)
        } else {
            eventQueue?.setAdaptiveBackgroundMode(false)
        }
    }

    /**
     * Handle network interface switches / reconnection (< 5s recovery target).
     */
    fun triggerConnectionRecovery() {
        Log.i(TAG, "Triggering fast connection recovery (< 5s target)")
        networkStateManager?.requestConnectionReset()
        scope.launch {
            try {
                // Instantly force EventQueueGet long-poll re-establishment
                eventQueue?.forceReconnect()
                delay(1000L)
                // Trigger UDP keepalive ping
                LinkpointConnectionService.setProcessConnected(true)
            } catch (e: Exception) {
                Log.e(TAG, "Error during connection recovery", e)
            }
        }
    }

    private fun registerDozeReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    if (PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED == intent?.action) {
                        val pm = ctx?.getSystemService(Context.POWER_SERVICE) as? PowerManager
                        val isIdle = pm?.isDeviceIdleMode == true
                        onDozeModeChanged(isIdle)
                    }
                }
            }
            val filter = IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)
            try {
                context.registerReceiver(receiver, filter)
                dozeReceiver = receiver
            } catch (e: Exception) {
                Log.w(TAG, "Failed to register Doze receiver", e)
            }
        }
    }

    private fun registerNetworkCallback() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Network interface available - recovering socket session")
                triggerConnectionRecovery()
            }

            override fun onLost(network: Network) {
                Log.w(TAG, "Network interface lost")
                networkStateManager?.setStatus(NetworkStateManager.ConnectionStatus.RECONNECTING)
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        try {
            cm.registerNetworkCallback(request, callback)
            networkCallback = callback
        } catch (e: Exception) {
            Log.w(TAG, "Failed to register network callback", e)
        }
    }

    fun shutdown() {
        dozeReceiver?.let {
            try { context.unregisterReceiver(it) } catch (_: Exception) {}
            dozeReceiver = null
        }
        networkCallback?.let {
            try {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                cm?.unregisterNetworkCallback(it)
            } catch (_: Exception) {}
            networkCallback = null
        }
        isInitialized.set(false)
        Log.i(TAG, "NetworkSessionManager shut down")
    }
}
