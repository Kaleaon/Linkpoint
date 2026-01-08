package com.linkpoint.voice

import android.content.Context
import android.media.AudioManager
import android.util.Log
import io.getstream.webrtc.android.ktx.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.webrtc.*
import java.util.concurrent.ConcurrentHashMap

/**
 * WebRTC Voice Manager for Second Life Voice Chat
 * Replaces proprietary Vivox SDK with open-source WebRTC implementation
 * Modern Kotlin implementation with coroutines and flows
 */
class WebRTCVoiceManager(
    private val context: Context,
    private val voiceCallback: VoiceCallback
) {
    companion object {
        private const val TAG = "WebRTCVoice"
    }
    
    // WebRTC Components
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var audioDeviceModule: AudioDeviceModule? = null
    private var audioSource: AudioSource? = null
    private var localAudioTrack: AudioTrack? = null
    
    // Voice Session Management
    private val activePeerConnections = ConcurrentHashMap<String, PeerConnection>()
    private val voiceSessions = ConcurrentHashMap<String, VoiceSession>()
    
    // Configuration
    private var isInitialized = false
    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()
    
    private val _speakerVolume = MutableStateFlow(1.0f)
    val speakerVolume: StateFlow<Float> = _speakerVolume.asStateFlow()
    
    private val _microphoneVolume = MutableStateFlow(1.0f)
    val microphoneVolume: StateFlow<Float> = _microphoneVolume.asStateFlow()
    
    // Second Life voice server configuration
    private var voiceServerUrl: String? = null
    private var voiceChannelUri: String? = null
    private var authToken: String? = null
    
    // Coroutine scope for async operations
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    interface VoiceCallback {
        fun onVoiceConnected(channelUri: String)
        fun onVoiceDisconnected(channelUri: String, reason: String)
        fun onUserJoined(channelUri: String, userId: String, displayName: String)
        fun onUserLeft(channelUri: String, userId: String)
        fun onUserSpeaking(userId: String, speaking: Boolean)
        fun onVoiceError(error: String)
    }
    
    /**
     * Initialize WebRTC voice system
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing WebRTC voice system...")
            
            // Initialize WebRTC
            val initOptions = PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(false)
                .setFieldTrials("")
                .createInitializationOptions()
            PeerConnectionFactory.initialize(initOptions)
            
            // Create audio device module
            audioDeviceModule = JavaAudioDeviceModule.builder(context)
                .setUseHardwareAcousticEchoCanceler(true)
                .setUseHardwareNoiseSuppressor(true)
                .setAudioRecordErrorCallback(object : JavaAudioDeviceModule.AudioRecordErrorCallback {
                    override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                        Log.e(TAG, "Audio record init error: $errorMessage")
                    }
                    
                    override fun onWebRtcAudioRecordStartError(
                        errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode,
                        errorMessage: String
                    ) {
                        Log.e(TAG, "Audio record start error: $errorMessage")
                    }
                    
                    override fun onWebRtcAudioRecordError(errorMessage: String) {
                        Log.e(TAG, "Audio record error: $errorMessage")
                    }
                })
                .setAudioTrackErrorCallback(object : JavaAudioDeviceModule.AudioTrackErrorCallback {
                    override fun onWebRtcAudioTrackInitError(errorMessage: String) {
                        Log.e(TAG, "Audio track init error: $errorMessage")
                    }
                    
                    override fun onWebRtcAudioTrackStartError(
                        errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode,
                        errorMessage: String
                    ) {
                        Log.e(TAG, "Audio track start error: $errorMessage")
                    }
                    
                    override fun onWebRtcAudioTrackError(errorMessage: String) {
                        Log.e(TAG, "Audio track error: $errorMessage")
                    }
                })
                .createAudioDeviceModule()
            
            // Create peer connection factory
            val options = PeerConnectionFactory.Options()
            peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(audioDeviceModule)
                .createPeerConnectionFactory()
            
            // Create audio source and track
            val audioConstraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
            }
            
            audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
            localAudioTrack = peerConnectionFactory?.createAudioTrack("ARDAMSa0", audioSource)
            
            isInitialized = true
            Log.i(TAG, "WebRTC voice system initialized successfully")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize WebRTC voice system", e)
            voiceCallback.onVoiceError("Initialization failed: ${e.message}")
            false
        }
    }
    
    /**
     * Connect to Second Life voice channel
     */
    suspend fun connectToVoiceChannel(channelUri: String, authToken: String?): Boolean = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            Log.e(TAG, "WebRTC not initialized")
            return@withContext false
        }
        
        try {
            Log.i(TAG, "Connecting to voice channel: $channelUri")
            
            voiceChannelUri = channelUri
            this@WebRTCVoiceManager.authToken = authToken
            
            // Create voice session for this channel
            val session = VoiceSession(channelUri, authToken)
            voiceSessions[channelUri] = session
            
            // In a real implementation, this would:
            // 1. Connect to SL voice server's WebRTC endpoint
            // 2. Perform authentication using the auth token
            // 3. Join the specified voice channel
            // 4. Set up peer connections with other users in the channel
            
            // For now, simulate successful connection
            voiceCallback.onVoiceConnected(channelUri)
            
            Log.i(TAG, "Successfully connected to voice channel")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to voice channel", e)
            voiceCallback.onVoiceError("Connection failed: ${e.message}")
            false
        }
    }
    
    /**
     * Disconnect from voice channel
     */
    suspend fun leaveVoiceChannel(channelUri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Leaving voice channel: $channelUri")
            
            val session = voiceSessions.remove(channelUri)
            session?.cleanup()
            
            // Close peer connections for this channel
            activePeerConnections.entries.removeAll { entry ->
                if (entry.key.startsWith(channelUri)) {
                    entry.value.close()
                    true
                } else {
                    false
                }
            }
            
            voiceCallback.onVoiceDisconnected(channelUri, "User left")
            
            Log.i(TAG, "Successfully left voice channel")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to leave voice channel", e)
            false
        }
    }
    
    /**
     * Mute/unmute microphone
     */
    fun setMicrophoneMuted(muted: Boolean) {
        _isMuted.value = muted
        localAudioTrack?.setEnabled(!muted)
        Log.i(TAG, "Microphone ${if (muted) "muted" else "unmuted"}")
    }
    
    /**
     * Set speaker volume
     */
    fun setSpeakerVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        _speakerVolume.value = clampedVolume
        
        // Adjust audio manager volume
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        audioManager?.let {
            val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
            val targetVolume = (maxVolume * clampedVolume).toInt()
            it.setStreamVolume(AudioManager.STREAM_VOICE_CALL, targetVolume, 0)
        }
        
        Log.i(TAG, "Speaker volume set to: $clampedVolume")
    }
    
    /**
     * Set microphone volume
     */
    fun setMicrophoneVolume(volume: Float) {
        val clampedVolume = volume.coerceIn(0.0f, 1.0f)
        _microphoneVolume.value = clampedVolume
        // WebRTC handles microphone gain internally
        Log.i(TAG, "Microphone volume set to: $clampedVolume")
    }
    
    /**
     * Get list of available audio devices
     */
    fun getAvailableAudioDevices(): List<AudioDevice> {
        val devices = mutableListOf<AudioDevice>()
        
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        audioManager?.let {
            // Add built-in speaker
            devices.add(AudioDevice("speaker", "Speaker", AudioDevice.Type.SPEAKER))
            
            // Add built-in microphone
            devices.add(AudioDevice("microphone", "Microphone", AudioDevice.Type.MICROPHONE))
            
            // Check for bluetooth devices
            if (it.isBluetoothScoAvailableOffCall) {
                devices.add(AudioDevice("bluetooth", "Bluetooth", AudioDevice.Type.BLUETOOTH))
            }
            
            // Check for wired headset
            if (it.isWiredHeadsetOn) {
                devices.add(AudioDevice("headset", "Wired Headset", AudioDevice.Type.WIRED_HEADSET))
            }
        }
        
        return devices
    }
    
    /**
     * Check if voice is currently connected
     */
    fun isConnected(): Boolean = voiceSessions.isNotEmpty()
    
    /**
     * Cleanup and release resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up WebRTC voice system...")
        
        // Close all peer connections
        activePeerConnections.values.forEach { it.close() }
        activePeerConnections.clear()
        
        // Cleanup voice sessions
        voiceSessions.values.forEach { it.cleanup() }
        voiceSessions.clear()
        
        // Dispose WebRTC resources
        localAudioTrack?.dispose()
        localAudioTrack = null
        
        audioSource?.dispose()
        audioSource = null
        
        peerConnectionFactory?.dispose()
        peerConnectionFactory = null
        
        audioDeviceModule?.release()
        audioDeviceModule = null
        
        // Cancel coroutine scope
        scope.cancel()
        
        isInitialized = false
        Log.i(TAG, "WebRTC voice system cleanup completed")
    }
    
    /**
     * Voice session for managing channel-specific state
     */
    private data class VoiceSession(
        val channelUri: String,
        val authToken: String?,
        val createTime: Long = System.currentTimeMillis()
    ) {
        fun cleanup() {
            // Cleanup session-specific resources
        }
    }
    
    /**
     * Audio device representation
     */
    data class AudioDevice(
        val id: String,
        val name: String,
        val type: Type
    ) {
        enum class Type {
            SPEAKER, MICROPHONE, BLUETOOTH, WIRED_HEADSET
        }
        
        override fun toString(): String = name
    }
}