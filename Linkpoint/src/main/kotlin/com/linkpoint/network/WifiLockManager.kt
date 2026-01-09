package com.linkpoint.network

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log

/**
 * Manages WiFi lock acquisition and release to prevent WiFi from going to sleep
 * during active Second Life connections.
 * 
 * This fixes WiFi connectivity issues by:
 * 1. Keeping WiFi active during connection
 * 2. Properly releasing lock when disconnected
 * 3. Using appropriate lock mode for different Android versions
 */
class WifiLockManager(private val context: Context) {
    
    companion object {
        private const val TAG = "WifiLockManager"
        private const val LOCK_TAG = "Linkpoint:SLConnection"
    }
    
    private var wifiLock: WifiManager.WifiLock? = null
    private val wifiManager: WifiManager? by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    }
    
    /**
     * Acquire WiFi lock to keep WiFi active during connection
     */
    @Synchronized
    fun acquireLock(): Boolean {
        if (wifiLock?.isHeld == true) {
            Log.d(TAG, "WiFi lock already held")
            return true
        }
        
        return try {
            val manager = wifiManager
            if (manager == null) {
                Log.w(TAG, "WifiManager not available")
                return false
            }
            
            // Use HIGH_PERF mode for better performance, fall back to FULL for older devices
            val lockMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // HIGH_PERF is deprecated in Android 10+, but still works
                WifiManager.WIFI_MODE_FULL_HIGH_PERF
            } else {
                WifiManager.WIFI_MODE_FULL_HIGH_PERF
            }
            
            wifiLock = manager.createWifiLock(lockMode, LOCK_TAG).apply {
                setReferenceCounted(false)
                acquire()
            }
            
            Log.i(TAG, "WiFi lock acquired")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire WiFi lock", e)
            false
        }
    }
    
    /**
     * Release WiFi lock when disconnected
     */
    @Synchronized
    fun releaseLock() {
        try {
            wifiLock?.let { lock ->
                if (lock.isHeld) {
                    lock.release()
                    Log.i(TAG, "WiFi lock released")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing WiFi lock", e)
        } finally {
            wifiLock = null
        }
    }
    
    /**
     * Check if WiFi lock is currently held
     */
    fun isLockHeld(): Boolean = wifiLock?.isHeld == true
}