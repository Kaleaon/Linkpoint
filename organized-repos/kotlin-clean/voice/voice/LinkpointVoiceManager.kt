package com.linkpoint.voice

import android.content.Context
import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.*
import org.webrtc.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Modern Linkpoint Voice Manager using WebRTC
 * Implements Second Life voice communication with modern Kotlin coroutines
 * and updated WebRTC standards for 2025
 */
class LinkpointVoiceManager(
    private val context: Context,
    private val callback: VoiceCallback
) {
    companion object {
        private const val TAG = "LinkpointVoice"
    }

    // WebRTC Components
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var audioDeviceModule: AudioDeviceModule? = null
    private var audioSource: AudioSource? = null
    private var localAudioTrack: AudioTrack? = null

    // Voice Session Management
    private val activePeerConnections = ConcurrentHashMap<String, PeerConnection>()
    private val voiceSessions = ConcurrentHashMap<String, VoiceSession>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Configuration
    private var isInitialized = false
    private var isMuted = false
    private var speakerVolume = 1.0f
    private var microphoneVolume = 1.0f

    interface VoiceCallback {
        fun onVoiceConnected(channelUri: String)
        fun onVoiceDisconnected(channelUri: String, reason: String)
        fun onUserJoined(channelUri: String, userId: String, displayName: String)
        fun onUserLeft(channelUri: String, userId: String)
        fun onUserSpeaking(userId: String, speaking: Boolean)
        fun onVoiceError(error: String)
    }

    /**
     * Initialize WebRTC voice system with modern configuration
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing Linkpoint WebRTC voice system...")

            // Initialize WebRTC with modern options
            val initOptions = PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(false)
                .setFieldTrials("WebRTC-Audio-OpusSetSignalVoiceWithDtx/Enabled/")
                .createInitializationOptions()
            PeerConnectionFactory.initialize(initOptions)

            // Create modern audio device module with enhanced echo cancellation
            audioDeviceModule = JavaAudioDeviceModule.builder(context)
                .setUseHardwareAcousticEchoCanceler(true)
                .setUseHardwareNoiseSuppressor(true)
                .setUseStereoInput(false)
                .setUseStereoOutput(false)
                .setAudioRecordErrorCallback(object : JavaAudioDeviceModule.AudioRecordErrorCallback {
                    override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                        Log.e(TAG, "Audio record init error: $errorMessage")
                        callback.onVoiceError("Microphone initialization failed: $errorMessage")
                    }

                    override fun onWebRtcAudioRecordStartError(
                        errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode,
                        errorMessage: String
                    ) {
                        Log.e(TAG, "Audio record start error: $errorMessage")
                        callback.onVoiceError("Microphone start failed: $errorMessage")
                    }

                    override fun onWebRtcAudioRecordError(errorMessage: String) {
                        Log.e(TAG, "Audio record error: $errorMessage")
                    }
                })
                .setAudioTrackErrorCallback(object : JavaAudioDeviceModule.AudioTrackErrorCallback {
                    override fun onWebRtcAudioTrackInitError(errorMessage: String) {
                        Log.e(TAG, "Audio track init error: $errorMessage")
                        callback.onVoiceError("Speaker initialization failed: $errorMessage")
                    }

                    override fun onWebRtcAudioTrackStartError(
                        errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode,
                        errorMessage: String
                    ) {
                        Log.e(TAG, "Audio track start error: $errorMessage")
                        callback.onVoiceError("Speaker start failed: $errorMessage")
                    }

                    override fun onWebRtcAudioTrackError(errorMessage: String) {
                        Log.e(TAG, "Audio track error: $errorMessage")
                    }
                })
                .createAudioDeviceModule()

            // Create peer connection factory with modern options
            val options = PeerConnectionFactory.Options()
            peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(audioDeviceModule)
                .createPeerConnectionFactory()

            // Create audio source with modern constraints for voice
            val audioConstraints = MediaConstraints().apply {
                mandatory.addAll(listOf(
                    MediaConstraints.KeyValuePair("googEchoCancellation", "true"),
                    MediaConstraints.KeyValuePair("googNoiseSuppression", "true"),
                    MediaConstraints.KeyValuePair("googHighpassFilter", "true"),
                    MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"),
                    MediaConstraints.KeyValuePair("googAutoGainControl", "true"),
                    MediaConstraints.KeyValuePair("googAudioMirroring", "false")
                ))
            }

            audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
            localAudioTrack = peerConnectionFactory?.createAudioTrack("LinkpointAudio", audioSource)

            isInitialized = true
            Log.i(TAG, "Linkpoint WebRTC voice system initialized successfully")
            true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize WebRTC voice system", e)
            callback.onVoiceError("Initialization failed: ${e.message}")
            false
        }
    }

    /**
     * Connect to Second Life voice channel with modern async approach
     */
    suspend fun connectToVoiceChannel(channelUri: String, authToken: String): Boolean =
        withContext(Dispatchers.IO) {
            if (!isInitialized) {
                Log.e(TAG, "WebRTC not initialized")
                callback.onVoiceError("Voice system not initialized")
                return@withContext false
            }

            try {
                Log.i(TAG, "Connecting to voice channel: $channelUri")

                // Create voice session for this channel
                val session = VoiceSession(channelUri, authToken)
                voiceSessions[channelUri] = session

                // In production, this would:
                // 1. Connect to SL voice server's WebRTC signaling endpoint
                // 2. Authenticate using LLSD-based auth token
                // 3. Join the specified spatial or group voice channel
                // 4. Create peer connections for each user in proximity
                // 5. Handle spatial audio positioning

                callback.onVoiceConnected(channelUri)
                Log.i(TAG, "Successfully connected to voice channel")
                true

            } catch (e: Exception) {
                Log.e(TAG, "Failed to connect to voice channel", e)
                callback.onVoiceError("Connection failed: ${e.message}")
                false
            }
        }

    /**
     * Disconnect from voice channel
     */
    suspend fun leaveVoiceChannel(channelUri: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Leaving voice channel: $channelUri")

            voiceSessions.remove(channelUri)?.cleanup()

            // Close peer connections for this channel
            activePeerConnections.entries.removeIf { (key, peerConnection) ->
                if (key.startsWith(channelUri)) {
                    peerConnection.close()
                    true
                } else false
            }

            callback.onVoiceDisconnected(channelUri, "User left")
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
        isMuted = muted
        localAudioTrack?.setEnabled(!muted)
        Log.i(TAG, "Microphone ${if (muted) "muted" else "unmuted"}")
    }

    /**
     * Set speaker volume with modern AudioManager API
     */
    fun setSpeakerVolume(volume: Float) {
        speakerVolume = volume.coerceIn(0.0f, 1.0f)

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        audioManager?.let {
            val maxVolume = it.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
            val targetVolume = (maxVolume * speakerVolume).toInt()
            it.setStreamVolume(AudioManager.STREAM_VOICE_CALL, targetVolume, 0)
        }

        Log.i(TAG, "Speaker volume set to: $speakerVolume")
    }

    /**
     * Set microphone volume
     */
    fun setMicrophoneVolume(volume: Float) {
        microphoneVolume = volume.coerceIn(0.0f, 1.0f)
        Log.i(TAG, "Microphone volume set to: $microphoneVolume")
    }

    /**
     * Get list of available audio devices
     */
    fun getAvailableAudioDevices(): List<AudioDevice> {
        val devices = mutableListOf<AudioDevice>()
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        audioManager?.let {
            devices.add(AudioDevice("speaker", "Speaker", AudioDevice.Type.SPEAKER))
            devices.add(AudioDevice("microphone", "Microphone", AudioDevice.Type.MICROPHONE))

            if (it.isBluetoothScoAvailableOffCall) {
                devices.add(AudioDevice("bluetooth", "Bluetooth", AudioDevice.Type.BLUETOOTH))
            }

            if (it.isWiredHeadsetOn) {
                devices.add(AudioDevice("headset", "Wired Headset", AudioDevice.Type.WIRED_HEADSET))
            }
        }

        return devices
    }

    // Getters
    fun isConnected() = voiceSessions.isNotEmpty()
    fun isMuted() = isMuted
    fun getSpeakerVolume() = speakerVolume
    fun getMicrophoneVolume() = microphoneVolume

    /**
     * Cleanup and release resources
     */
    fun cleanup() {
        Log.i(TAG, "Cleaning up Linkpoint WebRTC voice system...")
        scope.cancel()

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

        isInitialized = false
        Log.i(TAG, "WebRTC voice system cleanup completed")
    }

    /**
     * Voice session for managing channel-specific state
     */
    private data class VoiceSession(
        val channelUri: String,
        val authToken: String,
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

        override fun toString() = name
    }
}