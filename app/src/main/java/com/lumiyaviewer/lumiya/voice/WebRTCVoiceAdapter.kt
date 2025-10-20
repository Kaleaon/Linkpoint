package com.lumiyaviewer.lumiya.voice

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*

/**
 * WebRTC Voice Adapter for Linkpoint
 * Bridges the existing Vivox-based voice system with WebRTC implementation
 * Provides drop-in replacement for Vivox functionality
 * Modern Kotlin implementation with coroutines
 */
class WebRTCVoiceAdapter private constructor(context: Context) {
    
    companion object {
        private const val TAG = "WebRTCVoiceAdapter"
        
        @Volatile
        private var instance: WebRTCVoiceAdapter? = null
        
        /**
         * Get singleton instance
         */
        @JvmStatic
        fun getInstance(context: Context): WebRTCVoiceAdapter {
            return instance ?: synchronized(this) {
                instance ?: WebRTCVoiceAdapter(context).also { instance = it }
            }
        }
    }
    
    private val context = context.applicationContext
    private var webRTCManager: WebRTCVoiceManager? = null
    private var isInitialized = false
    
    // Voice state
    private var currentVoiceAccountName: String? = null
    private var currentVoicePassword: String? = null
    private var currentChannelUri: String? = null
    private var isConnectedToVoice = false
    private var isMicrophoneMuted = false
    private var speakerVolume = 1.0f
    private var microphoneVolume = 1.0f
    
    // Callback interface for voice events
    interface VoiceAdapterCallback {
        fun onVoiceInitialized(success: Boolean)
        fun onVoiceChannelConnected(channelUri: String)
        fun onVoiceChannelDisconnected(channelUri: String)
        fun onVoiceUserJoined(userId: String, displayName: String)
        fun onVoiceUserLeft(userId: String)
        fun onVoiceError(error: String)
    }
    
    private var adapterCallback: VoiceAdapterCallback? = null
    
    /**
     * Set callback for voice events
     */
    fun setCallback(callback: VoiceAdapterCallback) {
        this.adapterCallback = callback
    }
    
    /**
     * Initialize WebRTC voice system
     * Replaces Vivox vx_initialize()
     */
    suspend fun initialize(): Boolean {
        if (isInitialized) {
            Log.i(TAG, "WebRTC voice adapter already initialized")
            return true
        }
        
        Log.i(TAG, "Initializing WebRTC voice adapter...")
        
        webRTCManager = WebRTCVoiceManager(context, object : WebRTCVoiceManager.VoiceCallback {
            override fun onVoiceConnected(channelUri: String) {
                isConnectedToVoice = true
                currentChannelUri = channelUri
                adapterCallback?.onVoiceChannelConnected(channelUri)
            }
            
            override fun onVoiceDisconnected(channelUri: String, reason: String) {
                isConnectedToVoice = false
                currentChannelUri = null
                adapterCallback?.onVoiceChannelDisconnected(channelUri)
            }
            
            override fun onUserJoined(channelUri: String, userId: String, displayName: String) {
                adapterCallback?.onVoiceUserJoined(userId, displayName)
            }
            
            override fun onUserLeft(channelUri: String, userId: String) {
                adapterCallback?.onVoiceUserLeft(userId)
            }
            
            override fun onUserSpeaking(userId: String, speaking: Boolean) {
                // Can be used for UI indicators
            }
            
            override fun onVoiceError(error: String) {
                adapterCallback?.onVoiceError(error)
            }
        })
        
        val success = webRTCManager?.initialize() ?: false
        isInitialized = success
        adapterCallback?.onVoiceInitialized(success)
        Log.i(TAG, "WebRTC voice adapter initialization ${if (success) "successful" else "failed"}")
        return success
    }
    
    /**
     * Shutdown voice system
     * Replaces Vivox vx_uninitialize()
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down WebRTC voice adapter...")
        
        webRTCManager?.cleanup()
        webRTCManager = null
        
        isInitialized = false
        isConnectedToVoice = false
        currentChannelUri = null
        currentVoiceAccountName = null
        currentVoicePassword = null
        
        Log.i(TAG, "WebRTC voice adapter shutdown completed")
    }
    
    /**
     * Account login for voice
     * Replaces Vivox vx_req_account_login
     */
    suspend fun accountLogin(accountName: String, password: String, serverUri: String): Boolean {
        Log.i(TAG, "Voice account login: $accountName to $serverUri")
        
        if (!isInitialized) {
            Log.e(TAG, "Voice adapter not initialized")
            return false
        }
        
        currentVoiceAccountName = accountName
        currentVoicePassword = password
        
        // In a real implementation, this would authenticate with the voice server
        // For now, simulate successful login
        return true
    }
    
    /**
     * Account logout
     * Replaces Vivox vx_req_account_logout
     */
    suspend fun accountLogout(): Boolean {
        Log.i(TAG, "Voice account logout")
        
        // Disconnect from any active channels first
        currentChannelUri?.let {
            if (isConnectedToVoice) {
                sessionTerminate(it)
            }
        }
        
        currentVoiceAccountName = null
        currentVoicePassword = null
        
        return true
    }
    
    /**
     * Connect to voice channel
     * Replaces Vivox vx_req_session_create + vx_req_session_media_connect
     */
    suspend fun sessionConnect(channelUri: String, authToken: String?): Boolean {
        Log.i(TAG, "Connecting to voice session: $channelUri")
        
        if (!isInitialized) {
            Log.e(TAG, "Voice adapter not initialized")
            return false
        }
        
        val manager = webRTCManager ?: run {
            Log.e(TAG, "WebRTC manager not available")
            return false
        }
        
        return manager.connectToVoiceChannel(channelUri, authToken)
    }
    
    /**
     * Disconnect from voice channel
     * Replaces Vivox vx_req_session_terminate
     */
    suspend fun sessionTerminate(channelUri: String): Boolean {
        Log.i(TAG, "Terminating voice session: $channelUri")
        
        return webRTCManager?.leaveVoiceChannel(channelUri) ?: true
    }
    
    /**
     * Set local speaker volume
     * Replaces Vivox vx_req_connector_set_local_speaker_volume
     */
    fun setSpeakerVolume(volume: Float) {
        speakerVolume = volume.coerceIn(0.0f, 1.0f)
        webRTCManager?.setSpeakerVolume(speakerVolume)
        Log.i(TAG, "Speaker volume set to: $speakerVolume")
    }
    
    /**
     * Set local microphone volume
     * Replaces Vivox vx_req_connector_set_local_mic_volume
     */
    fun setMicrophoneVolume(volume: Float) {
        microphoneVolume = volume.coerceIn(0.0f, 1.0f)
        webRTCManager?.setMicrophoneVolume(microphoneVolume)
        Log.i(TAG, "Microphone volume set to: $microphoneVolume")
    }
    
    /**
     * Mute/unmute local microphone
     * Replaces Vivox vx_req_connector_mute_local_mic
     */
    fun setMicrophoneMuted(muted: Boolean) {
        isMicrophoneMuted = muted
        webRTCManager?.setMicrophoneMuted(muted)
        Log.i(TAG, "Microphone ${if (muted) "muted" else "unmuted"}")
    }
    
    /**
     * Check if voice system is initialized
     * Replaces Vivox vx_is_initialized
     */
    fun isVoiceInitialized(): Boolean = isInitialized
    
    /**
     * Check if connected to voice channel
     */
    fun isConnectedToChannel(): Boolean = isConnectedToVoice
    
    /**
     * Get current channel URI
     */
    fun getCurrentChannelUri(): String? = currentChannelUri
    
    /**
     * Check if microphone is muted
     */
    fun isMicrophoneMuted(): Boolean = isMicrophoneMuted
    
    /**
     * Get current speaker volume
     */
    fun getSpeakerVolume(): Float = speakerVolume
    
    /**
     * Get current microphone volume
     */
    fun getMicrophoneVolume(): Float = microphoneVolume
    
    /**
     * Process voice credentials from Second Life
     * This handles SL-specific voice authentication and channel setup
     */
    suspend fun processSecondLifeVoiceCredentials(
        slVoiceUser: String,
        slVoicePassword: String,
        slVoiceServer: String,
        channelUri: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Processing Second Life voice credentials...")
            Log.i(TAG, "Voice user: $slVoiceUser")
            Log.i(TAG, "Voice server: $slVoiceServer")
            Log.i(TAG, "Channel URI: $channelUri")
            
            // Step 1: Account login with SL voice credentials
            val accountLoginSuccess = accountLogin(slVoiceUser, slVoicePassword, slVoiceServer)
            if (!accountLoginSuccess) {
                Log.e(TAG, "Voice account login failed")
                return@withContext false
            }
            
            // Step 2: Connect to the voice channel
            val channelConnectSuccess = sessionConnect(channelUri, null)
            if (!channelConnectSuccess) {
                Log.e(TAG, "Voice channel connection failed")
                return@withContext false
            }
            
            Log.i(TAG, "Second Life voice credentials processed successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to process SL voice credentials", e)
            false
        }
    }
    
    /**
     * Get build version info
     * Replaces Vivox BUILD_VERSION_get, BUILD_DATE_get, etc.
     */
    fun getBuildVersion(): String = "WebRTC-1.0"
    
    fun getBuildDate(): String = "2024-09-13"
    
    fun getBuildHost(): String = "Linkpoint-WebRTC"
}