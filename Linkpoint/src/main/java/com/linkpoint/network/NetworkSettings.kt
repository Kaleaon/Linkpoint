package com.linkpoint.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * Network settings for configurable network behavior.
 * 
 * Includes adjustable settings for:
 * - XML-RPC response buffer size (3MB - 500MB)
 * - Used for login responses which can be large due to inventory skeletons
 */
class NetworkSettings(context: Context) {
    
    companion object {
        private const val TAG = "NetworkSettings"
        private const val PREFS_NAME = "network_settings"
        
        // Keys for preferences
        const val KEY_XML_BUFFER_SIZE_MB = "xml_buffer_size_mb"
        
        // Buffer size limits (3MB - 500MB as requested)
        const val MIN_BUFFER_SIZE_MB = 3
        const val MAX_BUFFER_SIZE_MB = 500
        const val DEFAULT_BUFFER_SIZE_MB = 100  // 100MB default - good for most users
        
        @Volatile
        private var instance: NetworkSettings? = null
        
        /**
         * Get or create singleton instance
         */
        fun getInstance(context: Context): NetworkSettings {
            return instance ?: synchronized(this) {
                instance ?: NetworkSettings(context.applicationContext).also {
                    instance = it
                }
            }
        }
        
        /**
         * Get current buffer size in bytes (for use without context)
         */
        fun getBufferSizeBytes(): Long {
            return instance?.getXmlBufferSizeMB()?.toLong()?.times(1024 * 1024) 
                ?: (DEFAULT_BUFFER_SIZE_MB.toLong() * 1024 * 1024)
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Get configured XML response buffer size in MB
     */
    fun getXmlBufferSizeMB(): Int {
        return prefs.getInt(KEY_XML_BUFFER_SIZE_MB, DEFAULT_BUFFER_SIZE_MB)
    }
    
    /**
     * Set XML response buffer size in MB
     */
    fun setXmlBufferSizeMB(sizeMB: Int) {
        val clampedSize = sizeMB.coerceIn(MIN_BUFFER_SIZE_MB, MAX_BUFFER_SIZE_MB)
        prefs.edit().putInt(KEY_XML_BUFFER_SIZE_MB, clampedSize).apply()
        Log.i(TAG, "XML buffer size set to ${clampedSize}MB")
    }
    
    /**
     * Get XML response buffer size in bytes
     */
    fun getXmlBufferSizeBytes(): Long {
        return getXmlBufferSizeMB().toLong() * 1024 * 1024
    }
}
