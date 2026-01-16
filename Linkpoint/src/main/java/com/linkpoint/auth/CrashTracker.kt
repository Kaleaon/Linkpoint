package com.linkpoint.auth

import android.content.Context
import android.util.Log

/**
 * Tracks application execution status for crash reporting.
 * 
 * The `last_exec_event` login parameter tells the grid about how the
 * previous session ended. This is used by Linden Lab for viewer stability
 * metrics and crash reporting.
 * 
 * All official viewers (Firestorm, Alchemy, LibreMetaverse) send this parameter.
 * 
 * Usage:
 * 1. Call recordAppStart() in Application.onCreate()
 * 2. Call recordCleanShutdown() when user logs out properly
 * 3. Call getLastExecStatus() when building login request
 * 
 * @see <a href="https://wiki.secondlife.com/wiki/Current_login_protocols">SL Login Protocol</a>
 */
class CrashTracker(context: Context) {
    
    companion object {
        private const val TAG = "CrashTracker"
        private const val PREFS_NAME = "linkpoint_crash_tracker"
        private const val KEY_LAST_EXEC = "last_exec"
        private const val KEY_SESSION_ACTIVE = "session_active"
        private const val KEY_LOGOUT_IN_PROGRESS = "logout_in_progress"
    }
    
    /**
     * Last execution status values.
     * These match the official Second Life protocol values.
     */
    enum class Status(val value: Int) {
        /** Application exited normally */
        NORMAL(0),
        /** Application froze (not responding) */
        FROZE(1),
        /** Application detected error and exited abnormally */
        FORCED_CRASH(2),
        /** Other/unknown crash */
        OTHER_CRASH(3),
        /** Application froze during logout */
        LOGOUT_FROZE(4),
        /** Application crashed during logout */
        LOGOUT_CRASH(5)
    }
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Get the last execution status value to send with login.
     * 
     * @return Integer status code matching Second Life protocol
     */
    fun getLastExecStatus(): Int {
        return prefs.getInt(KEY_LAST_EXEC, Status.NORMAL.value)
    }
    
    /**
     * Get the last execution status as enum.
     */
    fun getLastExecStatusEnum(): Status {
        val value = getLastExecStatus()
        return Status.values().find { it.value == value } ?: Status.NORMAL
    }
    
    /**
     * Record that the application has started.
     * 
     * This should be called in Application.onCreate().
     * If the previous session didn't end cleanly (session_active was true),
     * we record it as a crash.
     */
    fun recordAppStart() {
        val wasSessionActive = prefs.getBoolean(KEY_SESSION_ACTIVE, false)
        val wasLogoutInProgress = prefs.getBoolean(KEY_LOGOUT_IN_PROGRESS, false)
        
        if (wasSessionActive) {
            // Previous session didn't end cleanly - it was a crash
            val crashType = if (wasLogoutInProgress) {
                Log.w(TAG, "Detected crash during logout")
                Status.LOGOUT_CRASH
            } else {
                Log.w(TAG, "Detected crash during normal operation")
                Status.OTHER_CRASH
            }
            
            prefs.edit()
                .putInt(KEY_LAST_EXEC, crashType.value)
                .putBoolean(KEY_SESSION_ACTIVE, false)
                .putBoolean(KEY_LOGOUT_IN_PROGRESS, false)
                .apply()
        } else {
            // Previous session ended normally
            Log.d(TAG, "Previous session ended normally")
        }
    }
    
    /**
     * Record that a session has started (user logged in).
     * 
     * This should be called after successful login.
     */
    fun recordSessionStart() {
        prefs.edit()
            .putBoolean(KEY_SESSION_ACTIVE, true)
            .putBoolean(KEY_LOGOUT_IN_PROGRESS, false)
            .apply()
        Log.d(TAG, "Session started")
    }
    
    /**
     * Record that logout is in progress.
     * 
     * This should be called when the user initiates logout.
     * If the app crashes during logout, we'll know.
     */
    fun recordLogoutStarted() {
        prefs.edit()
            .putBoolean(KEY_LOGOUT_IN_PROGRESS, true)
            .apply()
        Log.d(TAG, "Logout started")
    }
    
    /**
     * Record a clean shutdown.
     * 
     * This should be called after successful logout or when the app
     * is closing normally.
     */
    fun recordCleanShutdown() {
        prefs.edit()
            .putInt(KEY_LAST_EXEC, Status.NORMAL.value)
            .putBoolean(KEY_SESSION_ACTIVE, false)
            .putBoolean(KEY_LOGOUT_IN_PROGRESS, false)
            .apply()
        Log.d(TAG, "Clean shutdown recorded")
    }
    
    /**
     * Record a forced crash (detected error).
     * 
     * Call this if the application detects an unrecoverable error
     * and is about to terminate.
     */
    fun recordForcedCrash() {
        prefs.edit()
            .putInt(KEY_LAST_EXEC, Status.FORCED_CRASH.value)
            .putBoolean(KEY_SESSION_ACTIVE, false)
            .apply()
        Log.e(TAG, "Forced crash recorded")
    }
    
    /**
     * Get a human-readable description of the last execution status.
     */
    fun getLastExecDescription(): String {
        return when (getLastExecStatusEnum()) {
            Status.NORMAL -> "Normal exit"
            Status.FROZE -> "Application froze"
            Status.FORCED_CRASH -> "Forced crash (error detected)"
            Status.OTHER_CRASH -> "Unexpected crash"
            Status.LOGOUT_FROZE -> "Froze during logout"
            Status.LOGOUT_CRASH -> "Crashed during logout"
        }
    }
}
