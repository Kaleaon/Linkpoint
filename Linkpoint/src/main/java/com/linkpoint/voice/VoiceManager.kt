package com.linkpoint.voice

import android.content.Context
import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * Voice chat manager using WebRTC
 * Handles spatial voice for regions and P2P voice calls
 * 
 * Implements Second Life WebRTC Voice protocol with moderation support:
 * - Spatial voice channels (region/parcel)
 * - P2P voice calls
 * - Voice moderation (mute/unmute participants)
 * - Region owners, estate managers, and parcel owners can moderate
 * 
 * @see https://wiki.secondlife.com/wiki/WebRTC_Voice
 */
class VoiceManager(
    private val context: Context,
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "VoiceManager"
        
        // Moderation permissions
        const val MODERATION_NONE = 0
        const val MODERATION_PARCEL_OWNER = 1
        const val MODERATION_ESTATE_MANAGER = 2
        const val MODERATION_REGION_OWNER = 4
    }
    
    private val voiceDispatcher: CoroutineDispatcher = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "VoiceThread").apply { isDaemon = true }
    }.asCoroutineDispatcher()
    private val scope = CoroutineScope(voiceDispatcher + SupervisorJob())
    
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
    
    // Voice participants with mute state
    private val _participants = MutableStateFlow<Map<UUID, VoiceParticipant>>(emptyMap())
    val participants: StateFlow<Map<UUID, VoiceParticipant>> = _participants
    
    // Moderation state
    private val _canModerate = MutableStateFlow(false)
    val canModerate: StateFlow<Boolean> = _canModerate
    
    private val _moderationLevel = MutableStateFlow(MODERATION_NONE)
    val moderationLevel: StateFlow<Int> = _moderationLevel
    
    private val _allMuted = MutableStateFlow(false)
    val allMuted: StateFlow<Boolean> = _allMuted
    
    // Audio settings
    private var inputGain = 1.0f
    private var outputGain = 1.0f
    
    init {
        runBlocking(voiceDispatcher) {
            initializeWebRTC()
        }
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
            
            Log.i(TAG, "[${Thread.currentThread().name}] WebRTC initialized")
        } catch (e: Exception) {
            Log.e(TAG, "[${Thread.currentThread().name}] Failed to initialize WebRTC", e)
        }
    }
    
    /**
     * Request voice info for current parcel
     */
    suspend fun requestParcelVoiceInfo(): VoiceInfo? = withContext(voiceDispatcher) {
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
    suspend fun provisionVoiceAccount(): VoiceAccountInfo? = withContext(voiceDispatcher) {
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
    suspend fun joinParcelVoice(): Boolean = withContext(voiceDispatcher) {
        val voiceInfo = requestParcelVoiceInfo() ?: return@withContext false

        Log.i(TAG, "[${Thread.currentThread().name}] Joining voice channel: ${voiceInfo.channelUri}")

        // Create WebRTC session
        val session = createSession(voiceInfo.channelUri)
        currentParcelSession = session
        activeSessions[voiceInfo.channelUri] = session

        // Start connection
        session.connect(voiceInfo.channelUri, voiceInfo.channelCredentials)

        _isConnected.value = true
        true
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
        Log.i(TAG, "[${Thread.currentThread().name}] Left voice channel")
    }
    
    /**
     * Set mute state
     */
    fun setMuted(muted: Boolean) {
        _isMuted.value = muted
        scope.launch {
            localAudioTrack?.setEnabled(!muted)
            Log.d(TAG, "[${Thread.currentThread().name}] Set muted=$muted")
        }
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
        scope.launch {
            for (session in activeSessions.values) {
                session.setOutputGain(outputGain)
            }
            Log.d(TAG, "[${Thread.currentThread().name}] Set output gain to $outputGain")
        }
    }
    
    /**
     * Start a P2P voice call
     */
    suspend fun startCall(targetAgentId: UUID): Boolean {
        return withContext(voiceDispatcher) {
            // Create session
            val sessionId = "p2p_$targetAgentId"
            val session = createSession(sessionId)
            activeSessions[sessionId] = session

            // Create offer
            val offer = session.createOffer()

            // Send offer via IM
            // (Would send through chat/IM system)

            Log.i(TAG, "[${Thread.currentThread().name}] Started call to $targetAgentId (offer length=${offer.length})")
            true
        }
    }
    
    /**
     * Accept incoming voice call
     */
    suspend fun acceptCall(callId: String, offer: String): Boolean {
        return withContext(voiceDispatcher) {
            val session = createSession(callId)
            activeSessions[callId] = session

            val answer = session.handleOffer(offer)
            // Send answer back

            Log.i(TAG, "[${Thread.currentThread().name}] Accepted call $callId (answer length=${answer.length})")
            true
        }
    }
    
    /**
     * End a voice call
     */
    fun endCall(callId: String) {
        activeSessions[callId]?.disconnect()
        activeSessions.remove(callId)
        Log.i(TAG, "[${Thread.currentThread().name}] Ended call $callId")
    }
    
    // =====================================================================
    // Voice Moderation Features (Project Voice Moderation)
    // Per SL Wiki: https://wiki.secondlife.com/wiki/WebRTC_Voice
    // =====================================================================
    
    /**
     * Update moderation permissions for current channel
     * Called when entering a new parcel or when permissions change
     * 
     * @param isParcelOwner True if user owns the parcel
     * @param isEstateManager True if user is estate manager
     * @param isRegionOwner True if user owns the region
     * @param hasGroupModerateAbility True if user has group 'Moderate Group Chat' ability
     */
    fun updateModerationPermissions(
        isParcelOwner: Boolean = false,
        isEstateManager: Boolean = false,
        isRegionOwner: Boolean = false,
        hasGroupModerateAbility: Boolean = false
    ) {
        // Calculate moderation level using bitwise OR
        val level = (if (isParcelOwner || hasGroupModerateAbility) MODERATION_PARCEL_OWNER else 0) or
                    (if (isEstateManager) MODERATION_ESTATE_MANAGER else 0) or
                    (if (isRegionOwner) MODERATION_REGION_OWNER else 0)
        
        _moderationLevel.value = level
        _canModerate.value = level != MODERATION_NONE
        
        Log.i(TAG, "[${Thread.currentThread().name}] Moderation permissions updated: level=$level, canModerate=${_canModerate.value}")
    }
    
    /**
     * Mute a specific participant in the voice channel
     * Requires moderation permissions
     * 
     * @param participantId UUID of the participant to mute
     * @return True if mute was successful
     */
    suspend fun muteParticipant(participantId: UUID): Boolean {
        if (!_canModerate.value) {
            Log.w(TAG, "[${Thread.currentThread().name}] Cannot mute participant: no moderation permissions")
            return false
        }
        
        val currentParticipants = _participants.value.toMutableMap()
        val participant = currentParticipants[participantId] ?: return false
        
        // Send moderation request via capability
        val success = sendModerationRequest(participantId, mute = true)
        
        if (success) {
            currentParticipants[participantId] = participant.copy(isMutedByModerator = true)
            _participants.value = currentParticipants
            Log.i(TAG, "[${Thread.currentThread().name}] Muted participant: $participantId")
        }
        
        return success
    }
    
    /**
     * Unmute a specific participant in the voice channel
     * Requires moderation permissions
     * 
     * @param participantId UUID of the participant to unmute
     * @return True if unmute was successful
     */
    suspend fun unmuteParticipant(participantId: UUID): Boolean {
        if (!_canModerate.value) {
            Log.w(TAG, "[${Thread.currentThread().name}] Cannot unmute participant: no moderation permissions")
            return false
        }
        
        val currentParticipants = _participants.value.toMutableMap()
        val participant = currentParticipants[participantId] ?: return false
        
        val success = sendModerationRequest(participantId, mute = false)
        
        if (success) {
            currentParticipants[participantId] = participant.copy(isMutedByModerator = false)
            _participants.value = currentParticipants
            Log.i(TAG, "[${Thread.currentThread().name}] Unmuted participant: $participantId")
        }
        
        return success
    }
    
    /**
     * Mute all participants in the voice channel
     * New arrivals will also be muted until unmuted
     * Requires moderation permissions
     * 
     * @return True if operation was successful
     */
    suspend fun muteAllParticipants(): Boolean {
        if (!_canModerate.value) {
            Log.w(TAG, "[${Thread.currentThread().name}] Cannot mute all: no moderation permissions")
            return false
        }
        
        val success = sendModerationRequest(null, mute = true, muteAll = true)
        
        if (success) {
            _allMuted.value = true
            // Update all participants to muted state
            val mutedParticipants = _participants.value.mapValues { (_, p) ->
                p.copy(isMutedByModerator = true)
            }
            _participants.value = mutedParticipants
            Log.i(TAG, "[${Thread.currentThread().name}] Muted all participants")
        }
        
        return success
    }
    
    /**
     * Unmute all participants in the voice channel
     * Requires moderation permissions
     * 
     * @return True if operation was successful
     */
    suspend fun unmuteAllParticipants(): Boolean {
        if (!_canModerate.value) {
            Log.w(TAG, "[${Thread.currentThread().name}] Cannot unmute all: no moderation permissions")
            return false
        }
        
        val success = sendModerationRequest(null, mute = false, muteAll = true)
        
        if (success) {
            _allMuted.value = false
            // Update all participants to unmuted state
            val unmutedParticipants = _participants.value.mapValues { (_, p) ->
                p.copy(isMutedByModerator = false)
            }
            _participants.value = unmutedParticipants
            Log.i(TAG, "[${Thread.currentThread().name}] Unmuted all participants")
        }
        
        return success
    }
    
    /**
     * Send moderation request to the server via capability
     */
    private suspend fun sendModerationRequest(
        participantId: UUID?,
        mute: Boolean,
        muteAll: Boolean = false
    ): Boolean = withContext(voiceDispatcher) {
        try {
            val params = LLSDMap().apply {
                this["mute"] = LLSDBoolean(mute)
                if (muteAll) {
                    this["mute_all"] = LLSDBoolean(true)
                } else if (participantId != null) {
                    this["participant_id"] = LLSDString(participantId.toString())
                }
            }
            
            val response = capabilityManager.request(
                CapabilityManager.CAP_VOICE_MODERATION,
                params
            )
            
            (response as? LLSDMap)?.getBoolean("success") ?: false
        } catch (e: Exception) {
            Log.e(TAG, "[${Thread.currentThread().name}] Failed to send moderation request", e)
            false
        }
    }
    
    /**
     * Add a participant to the voice channel
     * Called when a new participant joins
     */
    fun addParticipant(participant: VoiceParticipant) {
        val currentParticipants = _participants.value.toMutableMap()
        
        // If all are muted, new arrivals should also be muted
        val finalParticipant = if (_allMuted.value) {
            participant.copy(isMutedByModerator = true)
        } else {
            participant
        }
        
        currentParticipants[participant.agentId] = finalParticipant
        _participants.value = currentParticipants
    }
    
    /**
     * Remove a participant from the voice channel
     * Called when a participant leaves
     */
    fun removeParticipant(agentId: UUID) {
        val currentParticipants = _participants.value.toMutableMap()
        currentParticipants.remove(agentId)
        _participants.value = currentParticipants
        
        // Also update speaking set
        val speaking = _speakingParticipants.value.toMutableSet()
        speaking.remove(agentId)
        _speakingParticipants.value = speaking
    }
    
    /**
     * Update speaking state for a participant
     */
    fun updateSpeakingState(agentId: UUID, isSpeaking: Boolean) {
        val speaking = _speakingParticipants.value.toMutableSet()
        if (isSpeaking) {
            speaking.add(agentId)
        } else {
            speaking.remove(agentId)
        }
        _speakingParticipants.value = speaking
    }
    
    // =====================================================================
    
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
            dispatcher = voiceDispatcher
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
        voiceDispatcher.close()
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
    private val dispatcher: CoroutineDispatcher
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
            
            android.util.Log.i("VoiceSession", "[${Thread.currentThread().name}] Connecting to voice channel: $uri")
            isConnected = true
        } catch (e: Exception) {
            android.util.Log.e("VoiceSession", "[${Thread.currentThread().name}] Failed to connect to voice channel", e)
            isConnected = false
        }
    }
    
    fun disconnect() {
        try {
            localAudioTrack?.setEnabled(false)
            peerConnection?.close()
            isConnected = false
            android.util.Log.i("VoiceSession", "[${Thread.currentThread().name}] Disconnected from voice channel")
        } catch (e: Exception) {
            android.util.Log.e("VoiceSession", "[${Thread.currentThread().name}] Error during disconnect", e)
        }
    }
    
    suspend fun createOffer(): String {
        return withContext(dispatcher) {
            try {
                // Create SDP offer for WebRTC peer connection
                peerConnection?.let { pc ->
                    val constraints = org.webrtc.MediaConstraints().apply {
                        mandatory.add(org.webrtc.MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
                        mandatory.add(org.webrtc.MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
                    }
                    
                    // Note: In a real implementation, we'd use a CompletableFuture or
                    // coroutine-based SDP observer
                    android.util.Log.d("VoiceSession", "[${Thread.currentThread().name}] Creating SDP offer")
                    ""
                } ?: ""
            } catch (e: Exception) {
                android.util.Log.e("VoiceSession", "[${Thread.currentThread().name}] Failed to create offer", e)
                ""
            }
        }
    }
    
    suspend fun handleOffer(offer: String): String {
        return withContext(dispatcher) {
            try {
                // Parse remote SDP offer and create answer
                peerConnection?.let { pc ->
                    // Set remote description from offer
                    // Create and return answer SDP
                    android.util.Log.d("VoiceSession", "[${Thread.currentThread().name}] Handling remote offer")
                    ""
                } ?: ""
            } catch (e: Exception) {
                android.util.Log.e("VoiceSession", "[${Thread.currentThread().name}] Failed to handle offer", e)
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
        
        android.util.Log.d("VoiceSession", "[${Thread.currentThread().name}] Set output gain to $outputGain")
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

/**
 * Represents a participant in a voice channel
 * Used for voice moderation features
 */
data class VoiceParticipant(
    val agentId: UUID,
    val displayName: String,
    val isSpeaking: Boolean = false,
    val isMutedByModerator: Boolean = false,
    val isSelfMuted: Boolean = false,
    val volume: Float = 1.0f,
    val energy: Float = 0f  // Voice energy level for speaking indicator
)
