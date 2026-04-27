package com.linkpoint.service

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

/**
 * Idle Handler - Manages user idle state and auto-away/logout.
 * 
 * Based on the reference viewer's SLIdleHandler.java
 * 
 * Features:
 * - Tracks user activity (touches, key presses)
 * - Auto-away after configurable idle time
 * - Optional auto-logout after extended idle
 * - Activity-based tracking across app
 */
class IdleHandler(
    private val connectionKeepAlive: ConnectionKeepAliveManager
) {
    companion object {
        private const val TAG = "IdleHandler"
        
        // Default timeouts (milliseconds)
        const val DEFAULT_AWAY_TIMEOUT_MS = 5 * 60 * 1000L      // 5 minutes
        const val DEFAULT_LOGOUT_TIMEOUT_MS = 30 * 60 * 1000L   // 30 minutes
        const val IDLE_CHECK_INTERVAL_MS = 10_000L              // 10 seconds
    }
    
    /**
     * Coroutine scope for background idle checking operations.
     * 
     * Uses Default dispatcher for periodic checks as they're not I/O bound,
     * and SupervisorJob to ensure failures don't cancel the entire scope.
     */
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    /**
     * Main thread scope for UI updates and listener notifications.
     * 
     * Replaces the legacy Handler approach with coroutine-based UI updates.
     * All UI updates and listener notifications should use mainScope.
     */
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Configuration
    var awayTimeoutMs = DEFAULT_AWAY_TIMEOUT_MS
    var logoutTimeoutMs = DEFAULT_LOGOUT_TIMEOUT_MS
    var autoAwayEnabled = true
    var autoLogoutEnabled = false
    
    // State
    private var lastActivityTime = System.currentTimeMillis()
    
    private val _isIdle = MutableStateFlow(false)
    val isIdle: StateFlow<Boolean> = _isIdle
    
    private val _isAway = MutableStateFlow(false)
    val isAway: StateFlow<Boolean> = _isAway
    
    // Tracked activities
    private val trackedActivities = mutableListOf<WeakReference<Activity>>()
    
    // Listeners
    private val listeners = mutableListOf<IdleListener>()
    
    // Check job
    private var checkJob: Job? = null
    
    init {
        startIdleCheck()
    }
    
    /**
     * Record user activity.
     */
    fun recordActivity() {
        lastActivityTime = System.currentTimeMillis()
        connectionKeepAlive.recordUserActivity()
        
        // Clear away/idle state
        if (_isAway.value || _isIdle.value) {
            _isIdle.value = false
            _isAway.value = false
            notifyStateChange(IdleState.ACTIVE)
            Log.d(TAG, "User returned from idle/away")
        }
    }
    
    /**
     * Attach touch listener to an activity.
     */
    fun trackActivity(activity: Activity) {
        // Remove dead references
        trackedActivities.removeAll { it.get() == null }
        
        // Add new reference
        trackedActivities.add(WeakReference(activity))
        
        // Set touch listener on root view
        val rootView = activity.window.decorView.rootView
        rootView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN || 
                event.action == MotionEvent.ACTION_MOVE) {
                recordActivity()
            }
            false
        }
        
        Log.d(TAG, "Tracking activity: ${activity.javaClass.simpleName}")
    }
    
    /**
     * Stop tracking an activity.
     */
    fun untrackActivity(activity: Activity) {
        trackedActivities.removeAll { it.get() == activity || it.get() == null }
        
        // Remove touch listener
        val rootView = activity.window.decorView.rootView
        rootView.setOnTouchListener(null)
    }
    
    private fun startIdleCheck() {
        checkJob?.cancel()
        checkJob = scope.launch {
            while (isActive) {
                delay(IDLE_CHECK_INTERVAL_MS)
                checkIdleState()
            }
        }
    }
    
    private fun checkIdleState() {
        val idleTime = System.currentTimeMillis() - lastActivityTime
        
        // Check for logout timeout
        if (autoLogoutEnabled && idleTime >= logoutTimeoutMs) {
            if (!_isIdle.value || !_isAway.value) {
                Log.i(TAG, "Auto-logout triggered after ${idleTime / 1000}s idle")
                notifyStateChange(IdleState.LOGOUT)
            }
            return
        }
        
        // Check for away timeout
        if (autoAwayEnabled && idleTime >= awayTimeoutMs) {
            if (!_isAway.value) {
                _isAway.value = true
                _isIdle.value = true
                Log.d(TAG, "User is now away (${idleTime / 1000}s idle)")
                notifyStateChange(IdleState.AWAY)
            }
            return
        }
        
        // Check for idle (shorter than away)
        val idleThreshold = awayTimeoutMs / 2
        if (idleTime >= idleThreshold && !_isIdle.value) {
            _isIdle.value = true
            Log.d(TAG, "User is now idle (${idleTime / 1000}s)")
            notifyStateChange(IdleState.IDLE)
        }
    }
    
    /**
     * Manually set away status.
     */
    fun setAway(away: Boolean) {
        if (away && !_isAway.value) {
            _isAway.value = true
            _isIdle.value = true
            notifyStateChange(IdleState.AWAY)
        } else if (!away && _isAway.value) {
            recordActivity()
        }
    }
    
    /**
     * Get idle time in milliseconds.
     */
    fun getIdleTimeMs(): Long = System.currentTimeMillis() - lastActivityTime
    
    /**
     * Add idle state listener.
     */
    fun addListener(listener: IdleListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: IdleListener) {
        listeners.remove(listener)
    }
    
    /**
     * Notify all registered listeners of an idle state change.
     * 
     * Uses mainScope to ensure notifications run on the main thread,
     * replacing the legacy Handler.post() approach with coroutine-based
     * execution for better integration with structured concurrency.
     */
    private fun notifyStateChange(state: IdleState) {
        mainScope.launch {
            listeners.forEach { it.onIdleStateChanged(state) }
        }
    }
    
    /**
     * Shutdown the idle handler and cancel all operations.
     * 
     * Cancels both background and main thread scopes to prevent memory leaks
     * and ensure proper cleanup of resources.
     */
    fun shutdown() {
        checkJob?.cancel()
        scope.cancel()
        mainScope.cancel()
        trackedActivities.clear()
        listeners.clear()
    }
}

/**
 * Idle states.
 */
enum class IdleState {
    ACTIVE,     // User is active
    IDLE,       // User hasn't interacted recently
    AWAY,       // User is away (longer idle)
    LOGOUT      // Auto-logout triggered
}

/**
 * Idle state change listener.
 */
fun interface IdleListener {
    fun onIdleStateChanged(state: IdleState)
}
