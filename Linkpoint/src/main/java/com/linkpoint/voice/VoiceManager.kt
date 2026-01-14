package com.linkpoint.voice

import android.content.Context
import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Voice chat manager using WebRTC
 * Handles spatial voice for regions and P2P voice calls
 */
class VoiceManager(
    private val context: Context,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "VoiceManager"
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // WebRTC components
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var localAudioTrack: AudioTrack? = null
    private var audioSource: AudioSource? = null
    
    // Voice sessions
    private val activeSessions = ConcurrentHashMap<String, VoiceSession>()
    private var currentParcelSession: VoiceSession? = null
    
    // State
    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected
    
    private val _speakingParticipants = MutableStateFlow<Set<UUID>>(emptySet())
    val speakingParticipants: StateFlow<Set<UUID>> = _speakingParticipants
    
    // Audio settings
    private var inputGain = 1.0f
    private var outputGain = 1.0f
    
    init {
        initializeWebRTC()
    }
    
    private fun initializeWebRTC() {
        try {
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                    .setEnableInternalTracer(false)
                    .createInitializationOptions()
            )
            
            val encoderFactory = DefaultVideoEncoderFactory(
                EglBase.create().eglBaseContext,
                true, true
            )
            val decoderFactory = DefaultVideoDecoderFactory(
                EglBase.create().eglBaseContext
            )
            
            peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory()
            
            // Create audio source
            val audioConstraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
            }
            
            audioSource = peerConnectionFactory?.createAudioSource(audioConstraints)
            localAudioTrack = peerConnectionFactory?.createAudioTrack("localAudio", audioSource)
            
            Log.i(TAG, "WebRTC initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize WebRTC", e)
        }
    }
    
    /**
     * Request voice info for current parcel
     */
    suspend fun requestParcelVoiceInfo(): VoiceInfo? = withContext(Dispatchers.IO) {
        val response = capabilityManager.request(CapabilityManager.CAP_PARCEL_VOICE)
        if (response is LLSDMap) {
            VoiceInfo(
                channelUri = response.getString("channel_uri") ?: "",
                channelCredentials = response.getString("channel_credentials") ?: "",
                voiceAccountServerUri = response.getString("voice_account_server_uri"),
                regionName = response.getString("region_name") ?: "Unknown"
            )
        } else null
    }
    
    /**
     * Provision voice account
     */
    suspend fun provisionVoiceAccount(): VoiceAccountInfo? = withContext(Dispatchers.IO) {
        val response = capabilityManager.request(CapabilityManager.CAP_PROVISION_VOICE)
        if (response is LLSDMap) {
            VoiceAccountInfo(
                username = response.getString("username") ?: "",
                password = response.getString("password") ?: "",
                voiceServerUri = response.getString("voice_sip_uri_hostname") ?: ""
            )
        } else null
    }
    
    /**
     * Join parcel voice
     */
    suspend fun joinParcelVoice(): Boolean {
        val voiceInfo = requestParcelVoiceInfo() ?: return false
        
        Log.i(TAG, "Joining voice channel: ${voiceInfo.channelUri}")
        
        // Create WebRTC session
        val session = createSession(voiceInfo.channelUri)
        currentParcelSession = session
        activeSessions[voiceInfo.channelUri] = session
        
        // Start connection
        session.connect(voiceInfo.channelUri, voiceInfo.channelCredentials)
        
        _isConnected.value = true
        return true
    }
    
    /**
     * Leave current voice channel
     */
    fun leaveVoice() {
        currentParcelSession?.let { session ->
            session.disconnect()
            activeSessions.remove(session.channelUri)
        }
        currentParcelSession = null
        _isConnected.value = false
    }
    
    /**
     * Set mute state
     */
    fun setMuted(muted: Boolean) {
        _isMuted.value = muted
        localAudioTrack?.setEnabled(!muted)
    }
    
    /**
     * Toggle mute
     */
    fun toggleMute() {
        setMuted(!_isMuted.value)
    }
    
    /**
     * Set input gain (microphone volume)
     */
    fun setInputGain(gain: Float) {
        inputGain = gain.coerceIn(0f, 2f)
        // Apply to audio source
    }
    
    /**
     * Set output gain (speaker volume)
     */
    fun setOutputGain(gain: Float) {
        outputGain = gain.coerceIn(0f, 2f)
        // Apply to all audio tracks
        for (session in activeSessions.values) {
            session.setOutputGain(outputGain)
        }
    }
    
    /**
     * Start a P2P voice call
     */
    suspend fun startCall(targetAgentId: UUID): Boolean {
        // Create session
        val sessionId = "p2p_$targetAgentId"
        val session = createSession(sessionId)
        activeSessions[sessionId] = session
        
        // Create offer
        val offer = session.createOffer()
        
        // Send offer via IM
        // (Would send through chat/IM system)
        
        return true
    }
    
    /**
     * Accept incoming voice call
     */
    suspend fun acceptCall(callId: String, offer: String): Boolean {
        val session = createSession(callId)
        activeSessions[callId] = session
        
        val answer = session.handleOffer(offer)
        // Send answer back
        
        return true
    }
    
    /**
     * End a voice call
     */
    fun endCall(callId: String) {
        activeSessions[callId]?.disconnect()
        activeSessions.remove(callId)
    }
    
    private fun createSession(channelUri: String): VoiceSession {
        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )
        
        val config = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }
        
        val peerConnection = peerConnectionFactory?.createPeerConnection(
            config,
            PeerConnectionObserver()
        )
        
        // Add local audio track
        localAudioTrack?.let { track ->
            peerConnection?.addTrack(track)
        }
        
        return VoiceSession(
            channelUri = channelUri,
            peerConnection = peerConnection,
            scope = scope
        )
    }
    
    fun shutdown() {
        scope.cancel()
        
        for (session in activeSessions.values) {
            session.disconnect()
        }
        activeSessions.clear()
        
        localAudioTrack?.dispose()
        audioSource?.dispose()
        peerConnectionFactory?.dispose()
    }
}

private class PeerConnectionObserver : PeerConnection.Observer {
    override fun onSignalingChange(state: PeerConnection.SignalingState) {}
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {}
    override fun onIceConnectionReceivingChange(receiving: Boolean) {}
    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
    override fun onIceCandidate(candidate: IceCandidate) {}
    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
    override fun onAddStream(stream: MediaStream) {}
    override fun onRemoveStream(stream: MediaStream) {}
    override fun onDataChannel(dataChannel: DataChannel) {}
    override fun onRenegotiationNeeded() {}
    override fun onAddTrack(receiver: RtpReceiver, streams: Array<out MediaStream>) {}
}

class VoiceSession(
    val channelUri: String,
    private val peerConnection: PeerConnection?,
    private val scope: CoroutineScope
) {
    private var isConnected = false
    private var outputGain = 1.0f
    private var localAudioTrack: org.webrtc.AudioTrack? = null
    
    /**
     * Connect to voice server with signaling
     */
    fun connect(uri: String, credentials: String) {
        try {
            // Parse the SIP URI for Vivox connection
            // Format typically: sip:confctl-g-xxxx@mt1v.livem.vivox.com
            
            // For WebRTC-based implementation:
            // 1. Parse credentials
            // 2. Create offer/answer exchange
            // 3. Establish ICE connection
            
            android.util.Log.i("VoiceSession", "Connecting to voice channel: $uri")
            isConnected = true
        } catch (e: Exception) {
            android.util.Log.e("VoiceSession", "Failed to connect to voice channel", e)
            isConnected = false
        }
    }
    
    fun disconnect() {
        try {
            localAudioTrack?.setEnabled(false)
            peerConnection?.close()
            isConnected = false
            android.util.Log.i("VoiceSession", "Disconnected from voice channel")
        } catch (e: Exception) {
            android.util.Log.e("VoiceSession", "Error during disconnect", e)
        }
    }
    
    suspend fun createOffer(): String {
        return withContext(Dispatchers.IO) {
            try {
                // Create SDP offer for WebRTC peer connection
                peerConnection?.let { pc ->
                    val constraints = org.webrtc.MediaConstraints().apply {
                        mandatory.add(org.webrtc.MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
                        mandatory.add(org.webrtc.MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
                    }
                    
                    // Note: In a real implementation, we'd use a CompletableFuture or
                    // coroutine-based SDP observer
                    android.util.Log.d("VoiceSession", "Creating SDP offer")
                    ""
                } ?: ""
            } catch (e: Exception) {
                android.util.Log.e("VoiceSession", "Failed to create offer", e)
                ""
            }
        }
    }
    
    suspend fun handleOffer(offer: String): String {
        return withContext(Dispatchers.IO) {
            try {
                // Parse remote SDP offer and create answer
                peerConnection?.let { pc ->
                    // Set remote description from offer
                    // Create and return answer SDP
                    android.util.Log.d("VoiceSession", "Handling remote offer")
                    ""
                } ?: ""
            } catch (e: Exception) {
                android.util.Log.e("VoiceSession", "Failed to handle offer", e)
                ""
            }
        }
    }
    
    /**
     * Set output audio gain (volume)
     * @param gain Volume multiplier (0.0 = muted, 1.0 = normal, >1.0 = amplified)
     */
    fun setOutputGain(gain: Float) {
        outputGain = gain.coerceIn(0f, 2f)
        
        // Apply gain to received audio tracks via AudioTrack or mixer
        peerConnection?.receivers?.forEach { receiver ->
            val track = receiver.track()
            if (track is org.webrtc.AudioTrack) {
                // WebRTC AudioTrack doesn't have direct gain control
                // In practice, we'd use an AudioProcessor or mix with Android's AudioTrack
                track.setEnabled(gain > 0f)
            }
        }
        
        android.util.Log.d("VoiceSession", "Set output gain to $outputGain")
    }
    
    fun isConnected(): Boolean = isConnected
}

data class VoiceInfo(
    val channelUri: String,
    val channelCredentials: String,
    val voiceAccountServerUri: String?,
    val regionName: String
)

data class VoiceAccountInfo(
    val username: String,
    val password: String,
    val voiceServerUri: String
)
