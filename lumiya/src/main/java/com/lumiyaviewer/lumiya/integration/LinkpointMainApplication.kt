package com.lumiyaviewer.lumiya.integration

import android.app.Application
import android.util.Log
import com.lumiyaviewer.lumiya.animesh.AnimeshManager
import com.lumiyaviewer.lumiya.auth.LinkpointAuthManager
import com.lumiyaviewer.lumiya.bom.BakesOnMeshManager
// import com.lumiyaviewer.lumiya.core.LinkpointApplication
import com.lumiyaviewer.lumiya.eep.EEPManager
import com.lumiyaviewer.lumiya.pbr.PBRMaterialManager
import com.lumiyaviewer.lumiya.protocol.LinkpointProtocolManager
import com.lumiyaviewer.lumiya.voice.LinkpointVoiceManager

/**
 * Main Linkpoint Application (Extended)
 * Properly integrated version with all systems connected
 */
class LinkpointMainApplication : Application() {
    
    companion object {
        private const val TAG = "LinkpointApp"
        
        @Volatile
        private var instance: LinkpointMainApplication? = null
        
        fun getInstance(): LinkpointMainApplication = instance!!
    }
    
    // Core managers
    lateinit var authManager: LinkpointAuthManager
        private set
    
    lateinit var voiceManager: LinkpointVoiceManager
        private set
    
    lateinit var protocolManager: LinkpointProtocolManager
        private set
    
    // Feature managers
    lateinit var animeshManager: AnimeshManager
        private set
    
    lateinit var bomManager: BakesOnMeshManager
        private set
    
    lateinit var eepManager: EEPManager
        private set
    
    lateinit var pbrManager: PBRMaterialManager
        private set
    
    // Integrated protocol handler
    lateinit var protocolHandler: IntegratedProtocolHandler
        private set
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Log.i(TAG, "========================================")
        Log.i(TAG, "Linkpoint - Modern SL Viewer")
        Log.i(TAG, "Version 1.0.0 with Full Feature Parity")
        Log.i(TAG, "========================================")
        
        initializeManagers()
        
        Log.i(TAG, "Linkpoint initialized successfully")
    }
    
    private fun initializeManagers() {
        // Core managers
        authManager = LinkpointAuthManager()
        protocolManager = LinkpointProtocolManager()
        
        // Voice manager with callback
        voiceManager = LinkpointVoiceManager(
            context = applicationContext,
            callback = createVoiceCallback()
        )
        
        // Feature managers
        animeshManager = AnimeshManager(this)
        bomManager = BakesOnMeshManager()
        eepManager = EEPManager()
        pbrManager = PBRMaterialManager()
        
        // Create integrated protocol handler
        protocolHandler = IntegratedProtocolHandler(
            protocolManager = protocolManager,
            animeshManager = animeshManager,
            bomManager = bomManager
        )
        
        Log.i(TAG, "All managers initialized")
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
        Log.i(TAG, "Linkpoint terminating...")
        
        // Cleanup all managers
        protocolHandler.cleanup()
        authManager.cleanup()
        voiceManager.cleanup()
        animeshManager.cleanup()
        bomManager.cleanup()
        eepManager.cleanup()
        pbrManager.cleanup()
        
        Log.i(TAG, "Linkpoint terminated")
    }
}