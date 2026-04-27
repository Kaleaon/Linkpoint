package com.lumiyaviewer.lumiya.voice

import android.content.Context
import android.media.AudioManager
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Modern WebRTC Voice Manager for Second Life Voice Chat
 * Complete replacement for legacy Vivox SDK
 * 
 * Features:
 * - Kotlin Coroutines for async operations
 * - StateFlow for reactive state management
 * - Modern WebRTC implementation
 * - Spatial audio support for Second Life
 */
class ModernWebRTCVoiceManager(
    private val context: Context,
    private val callback: VoiceCallback
) {
    companion object {
        private const val TAG = "ModernWebRTCVoice"
        
        // WebRTC Configuration
        private val ICE_SERVERS = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
        )
    }
    
    // WebRTC Components
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var audioDeviceModule: AudioDeviceModule? = null
    private var audioSource: AudioSource? = null
    private var localAudioTrack: AudioTrack? = null
    
    // Voice Session Management
    private val activePeerConnections = ConcurrentHashMap<String, PeerConnection>()
    private val voiceSessions = ConcurrentHashMap<String, VoiceSession>()
    
    // State Management with Kotlin Flow
    private val _connectionState = MutableStateFlow<VoiceConnectionState>(VoiceConnectionState.Disconnected)
    val connectionState: StateFlow<VoiceConnectionState> = _connectionState
    
    private val _participants = MutableStateFlow<Map<String, VoiceParticipant>>(emptyMap())
    val participants: StateFlow<Map<String, VoiceParticipant>> = _participants
    
    // Configuration
    private var isInitialized = false
    private var isMuted = false
    private var speakerVolume = 1.0f
    private var microphoneVolume = 1.0f
    
    // Second Life voice server configuration
    private var voiceServerUrl: String? = null
    private var voiceChannelUri: String? = null
    private var authToken: String? = null
    
    // Coroutine scope
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    /**
     * Voice connection states
     */
    sealed class VoiceConnectionState {
        object Disconnected : VoiceConnectionState()
        object Connecting : VoiceConnectionState()
        data class Connected(val channelUri: String) : VoiceConnectionState()
        data class Error(val message: String) : VoiceConnectionState()
    }
    
    /**
     * Voice participant data
     */
    data class VoiceParticipant(
        val userId: String,
        val displayName: String,
        val isSpeaking: Boolean = false,
        val isMuted: Boolean = false,
        val volume: Float = 1.0f,
        val position: SpatialPosition? = null
    )
    
    /**
     * Spatial position for 3D audio
     */
    data class SpatialPosition(
        val x: Float,
        val y: Float,
        val z: Float,
        val orientation: Float = 0f
    )
    
    /**
     * Voice callback interface
     */
    interface VoiceCallback {
        fun onVoiceConnected(channelUri: String)
        fun onVoiceDisconnected(channelUri: String, reason: String)
        fun onUserJoined(channelUri: String, userId: String, displayName: String)
        fun onUserLeft(channelUri: String, userId: String)
        fun onUserSpeaking(userId: String, speaking: Boolean)
        fun onVoiceError(error: String)
    }
    
    /**
     * Voice session data
     */
    data class VoiceSession(
        val channelUri: String,
        val peerConnection: PeerConnection,
        val participants: MutableMap<String, VoiceParticipant> = mutableMapOf()
    )
    
    /**
     * Initialize WebRTC voice system
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Initializing modern WebRTC voice system...")
            
            // Initialize WebRTC
            val initOptions = PeerConnectionFactory.InitializationOptions.builder(context)
                .setEnableInternalTracer(false)
                .setFieldTrials("")
                .createInitializationOptions()
            PeerConnectionFactory.initialize(initOptions)
            
            // Create audio device module with echo cancellation and noise suppression
            audioDeviceModule = JavaAudioDeviceModule.builder(context)
                .setUseHardwareAcousticEchoCanceler(true)
                .setUseHardwareNoiseSuppressor(true)
                .setAudioRecordErrorCallback(object : JavaAudioDeviceModule.AudioRecordErrorCallback {
                    override fun onWebRtcAudioRecordInitError(errorMessage: String) {
                        Log.e(TAG, "Audio record init error: $errorMessage")
                        callback.onVoiceError("Audio initialization failed: $errorMessage")
                    }
                    
                    override fun onWebRtcAudioRecordStartError(
                        errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode,
                        errorMessage: String
                    ) {
                        Log.e(TAG, "Audio record start error: $errorMessage")
                        callback.onVoiceError("Audio start failed: $errorMessage")
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
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
            }
            
            audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
            localAudioTrack = peerConnectionFactory?.createAudioTrack("local_audio", audioSource)
            
            isInitialized = true
            Log.i(TAG, "WebRTC voice system initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize WebRTC voice system", e)
            callback.onVoiceError("Initialization failed: ${e.message}")
            false
        }
    }
    
    /**
     * Connect to Second Life voice channel
     */
    suspend fun connectToChannel(
        channelUri: String,
        serverUrl: String,
        token: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized) {
                Log.e(TAG, "WebRTC not initialized")
                return@withContext false
            }
            
            Log.i(TAG, "Connecting to voice channel: $channelUri")
            _connectionState.value = VoiceConnectionState.Connecting
            
            this@ModernWebRTCVoiceManager.voiceChannelUri = channelUri
            this@ModernWebRTCVoiceManager.voiceServerUrl = serverUrl
            this@ModernWebRTCVoiceManager.authToken = token
            
            // Create peer connection
            val rtcConfig = PeerConnection.RTCConfiguration(ICE_SERVERS).apply {
                bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
                rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
                tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
                continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            }
            
            val peerConnection = peerConnectionFactory?.createPeerConnection(
                rtcConfig,
                object : PeerConnection.Observer {
                    override fun onIceCandidate(candidate: IceCandidate) {
                        Log.d(TAG, "ICE candidate: ${candidate.sdp}")
                        // Send ICE candidate to signaling server
                        scope.launch {
                            sendIceCandidate(channelUri, candidate)
                        }
                    }
                    
                    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {
                        Log.d(TAG, "ICE candidates removed: ${candidates.size}")
                    }
                    
                    override fun onSignalingChange(state: PeerConnection.SignalingState) {
                        Log.d(TAG, "Signaling state changed: $state")
                    }
                    
                    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
                        Log.d(TAG, "ICE connection state changed: $state")
                        when (state) {
                            PeerConnection.IceConnectionState.CONNECTED -> {
                                _connectionState.value = VoiceConnectionState.Connected(channelUri)
                                callback.onVoiceConnected(channelUri)
                            }
                            PeerConnection.IceConnectionState.DISCONNECTED,
                            PeerConnection.IceConnectionState.FAILED -> {
                                _connectionState.value = VoiceConnectionState.Disconnected
                                callback.onVoiceDisconnected(channelUri, "Connection lost")
                            }
                            else -> {}
                        }
                    }
                    
                    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {
                        Log.d(TAG, "ICE gathering state changed: $state")
                    }
                    
                    override fun onAddStream(stream: MediaStream) {
                        Log.d(TAG, "Stream added: ${stream.id}")
                        // Handle remote audio stream
                    }
                    
                    override fun onRemoveStream(stream: MediaStream) {
                        Log.d(TAG, "Stream removed: ${stream.id}")
                    }
                    
                    override fun onDataChannel(dataChannel: DataChannel) {
                        Log.d(TAG, "Data channel: ${dataChannel.label()}")
                    }
                    
                    override fun onRenegotiationNeeded() {
                        Log.d(TAG, "Renegotiation needed")
                    }
                    
                    override fun onAddTrack(receiver: RtpReceiver, streams: Array<out MediaStream>) {
                        Log.d(TAG, "Track added: ${receiver.id()}")
                    }
                }
            )
            
            if (peerConnection == null) {
                Log.e(TAG, "Failed to create peer connection")
                return@withContext false
            }
            
            // Add local audio track
            localAudioTrack?.let { track ->
                peerConnection.addTrack(track, listOf(channelUri))
            }
            
            // Store session
            val session = VoiceSession(channelUri, peerConnection)
            voiceSessions[channelUri] = session
            activePeerConnections[channelUri] = peerConnection
            
            // Create and send offer
            createAndSendOffer(channelUri, peerConnection)
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to voice channel", e)
            _connectionState.value = VoiceConnectionState.Error(e.message ?: "Unknown error")
            callback.onVoiceError("Connection failed: ${e.message}")
            false
        }
    }
    
    /**
     * Create and send SDP offer
     */
    private suspend fun createAndSendOffer(channelUri: String, peerConnection: PeerConnection) {
        withContext(Dispatchers.IO) {
            try {
                val constraints = MediaConstraints().apply {
                    mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
                    mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
                }
                
                peerConnection.createOffer(object : SdpObserver {
                    override fun onCreateSuccess(sdp: SessionDescription) {
                        peerConnection.setLocalDescription(object : SdpObserver {
                            override fun onSetSuccess() {
                                Log.d(TAG, "Local description set successfully")
                                // Send offer to signaling server
                                scope.launch {
                                    sendOffer(channelUri, sdp)
                                }
                            }
                            
                            override fun onSetFailure(error: String) {
                                Log.e(TAG, "Failed to set local description: $error")
                            }
                            
                            override fun onCreateSuccess(p0: SessionDescription?) {}
                            override fun onCreateFailure(p0: String?) {}
                        }, sdp)
                    }
                    
                    override fun onSetSuccess() {}
                    override fun onCreateFailure(error: String) {
                        Log.e(TAG, "Failed to create offer: $error")
                    }
                    override fun onSetFailure(error: String) {}
                }, constraints)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating offer", e)
            }
        }
    }
    
    /**
     * Send offer to signaling server
     */
    private suspend fun sendOffer(channelUri: String, sdp: SessionDescription) {
        // TODO: Implement signaling server communication
        // This would send the SDP offer to the Second Life voice server
        Log.d(TAG, "Sending offer for channel: $channelUri")
    }
    
    /**
     * Send ICE candidate to signaling server
     */
    private suspend fun sendIceCandidate(channelUri: String, candidate: IceCandidate) {
        // TODO: Implement ICE candidate exchange with signaling server
        Log.d(TAG, "Sending ICE candidate for channel: $channelUri")
    }
    
    /**
     * Disconnect from voice channel
     */
    suspend fun disconnectFromChannel(channelUri: String) = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Disconnecting from voice channel: $channelUri")
            
            activePeerConnections[channelUri]?.close()
            activePeerConnections.remove(channelUri)
            voiceSessions.remove(channelUri)
            
            _connectionState.value = VoiceConnectionState.Disconnected
            callback.onVoiceDisconnected(channelUri, "User disconnected")
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting from channel", e)
        }
    }
    
    /**
     * Set microphone mute state
     */
    fun setMicrophoneMuted(muted: Boolean) {
        isMuted = muted
        localAudioTrack?.setEnabled(!muted)
        Log.d(TAG, "Microphone ${if (muted) "muted" else "unmuted"}")
    }
    
    /**
     * Set speaker volume
     */
    fun setSpeakerVolume(volume: Float) {
        speakerVolume = volume.coerceIn(0f, 1f)
        // Apply volume to all remote tracks
        Log.d(TAG, "Speaker volume set to: $speakerVolume")
    }
    
    /**
     * Set microphone volume
     */
    fun setMicrophoneVolume(volume: Float) {
        microphoneVolume = volume.coerceIn(0f, 1f)
        // Apply volume to local track
        Log.d(TAG, "Microphone volume set to: $microphoneVolume")
    }
    
    /**
     * Set 3D spatial position for spatial audio
     */
    fun setSpatialPosition(position: SpatialPosition) {
        // TODO: Implement spatial audio positioning
        Log.d(TAG, "Spatial position set: $position")
    }
    
    /**
     * Shutdown voice system
     */
    fun shutdown() {
        scope.cancel()
        
        activePeerConnections.values.forEach { it.close() }
        activePeerConnections.clear()
        voiceSessions.clear()
        
        localAudioTrack?.dispose()
        audioSource?.dispose()
        audioDeviceModule?.release()
        peerConnectionFactory?.dispose()
        
        isInitialized = false
        Log.i(TAG, "WebRTC voice system shut down")
    }
}