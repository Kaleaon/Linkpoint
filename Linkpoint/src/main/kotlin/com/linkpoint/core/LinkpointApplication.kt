package com.linkpoint.core

import android.app.Application
import android.util.Log
import com.linkpoint.auth.LinkpointAuthManager
import com.linkpoint.graphics.LinkpointRenderPipeline
import com.linkpoint.voice.LinkpointVoiceManager

/**
 * Linkpoint Application
 * Modern Second Life viewer application for Android
 */
class LinkpointApplication : Application() {
    
    companion object {
        private const val TAG = "Linkpoint"
        
        @Volatile
        private var instance: LinkpointApplication? = null
        
        fun getInstance(): LinkpointApplication = instance!!
    }
    
    // Core managers (lazy initialization)
    val authManager: LinkpointAuthManager by lazy { LinkpointAuthManager() }
    lateinit var voiceManager: LinkpointVoiceManager
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Log.i(TAG, "Linkpoint Application starting...")
        Log.i(TAG, "Modern Second Life viewer for Android")
        Log.i(TAG, "Version: 1.0.0 (Build 2025)")
        
        // Initialize voice manager with context
        voiceManager = LinkpointVoiceManager(
            context = applicationContext,
            callback = createVoiceCallback()
        )
        
        Log.i(TAG, "Linkpoint Application initialized successfully")
    }
    
    private fun createVoiceCallback() = object : LinkpointVoiceManager.VoiceCallback {
        override fun onVoiceConnected(channelUri: String) {
            Log.i(TAG, "Voice connected: $channelUri")
        }
        
        override fun onVoiceDisconnected(channelUri: String, reason: String) {
            Log.i(TAG, "Voice disconnected: $channelUri - $reason")
        }
        
        override fun onUserJoined(channelUri: String, userId: String, displayName: String) {
            Log.i(TAG, "User joined voice: $displayName")
        }
        
        override fun onUserLeft(channelUri: String, userId: String) {
            Log.i(TAG, "User left voice: $userId")
        }
        
        override fun onUserSpeaking(userId: String, speaking: Boolean) {
            Log.d(TAG, "User $userId ${if (speaking) "started" else "stopped"} speaking")
        }
        
        override fun onVoiceError(error: String) {
            Log.e(TAG, "Voice error: $error")
        }
    }
    
    override fun onTerminate() {
        super.onTerminate()
        Log.i(TAG, "Linkpoint Application terminating...")
        
        // Cleanup managers
        authManager.cleanup()
        voiceManager.cleanup()
    }
}