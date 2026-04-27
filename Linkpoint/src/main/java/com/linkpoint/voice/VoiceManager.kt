package com.linkpoint.voice

import android.content.Context
import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
    
    private val voiceDispatcher: ExecutorCoroutineDispatcher = Executors.newSingleThreadExecutor { runnable ->
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
     * Provision voice account.
     *
     * Linden's WebRTC voice spec returns an `ice_servers` array alongside
     * the credentials so the client doesn't have to ship a hardcoded STUN
     * list. We accept both the modern WebRTC layout
     * (`voice_credentials` object + `ice_servers`) and the legacy Vivox
     * layout (`username` / `password` / `voice_sip_uri_hostname`) so this
     * works against sims that haven't migrated yet.
     */
    suspend fun provisionVoiceAccount(): VoiceAccountInfo? = withContext(voiceDispatcher) {
        val response = capabilityManager.request(CapabilityManager.CAP_PROVISION_VOICE)
        if (response !is LLSDMap) return@withContext null

        // Modern WebRTC layout nests credentials under `voice_credentials`.
        val credsMap = response.getMap("voice_credentials")
        val username = credsMap?.getString("username")
            ?: response.getString("username") ?: ""
        val password = credsMap?.getString("password")
            ?: response.getString("password") ?: ""
        val serverUri = response.getString("voice_server_url")
            ?: response.getString("voice_sip_uri_hostname")
            ?: ""

        val iceServers = parseIceServers(response.getArray("ice_servers"))

        VoiceAccountInfo(
            username = username,
            password = password,
            voiceServerUri = serverUri,
            iceServers = iceServers
        )
    }

    /**
     * Parse the `ice_servers` LLSD array into [IceServerSpec]s. The wire
     * shape per server is `{urls: [string|string[]], username?, credential?}`,
     * matching the W3C RTCIceServer dictionary that Linden exposes.
     */
    private fun parseIceServers(arr: LLSDArray?): List<IceServerSpec> {
        if (arr == null) return emptyList()
        val out = mutableListOf<IceServerSpec>()
        for (entry in arr.value) {
            val map = entry as? LLSDMap ?: continue
            val urls: List<String> = when (val raw = map["urls"]) {
                is LLSDString -> listOf(raw.value)
                is LLSDArray -> raw.value.mapNotNull { (it as? LLSDString)?.value }
                else -> map.getString("url")?.let { listOf(it) } ?: emptyList()
            }
            if (urls.isEmpty()) continue
            out += IceServerSpec(
                urls = urls,
                username = map.getString("username"),
                credential = map.getString("credential") ?: map.getString("password")
            )
        }
        return out
    }
    
    /**
     * Join parcel voice. Fetches `ParcelVoiceInfoRequest` for the channel
     * URI and `ProvisionVoiceAccountRequest` for credentials + ICE
     * servers, then builds a [VoiceSession] configured with the
     * sim-provided ICE servers (instead of the previous hardcoded
     * Google STUN). The signaling layer that POSTs SDP to the channel
     * URI is still pending — see the [VoiceSession] class doc.
     */
    suspend fun joinParcelVoice(): Boolean = withContext(voiceDispatcher) {
        val voiceInfo = requestParcelVoiceInfo() ?: return@withContext false
        val account = provisionVoiceAccount() // best-effort; may be null on Vivox sims

        Log.i(TAG, "Joining voice channel: ${voiceInfo.channelUri} " +
            "(${account?.iceServers?.size ?: 0} ICE servers from sim)")

        val session = createSession(voiceInfo.channelUri, account?.iceServers ?: emptyList())
        currentParcelSession = session
        activeSessions[voiceInfo.channelUri] = session

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
    
    private fun createSession(
        channelUri: String,
        iceServerSpecs: List<IceServerSpec> = emptyList()
    ): VoiceSession {
        // Build the WebRTC IceServer list from the simulator-provided
        // specs, falling back to a public STUN if the sim returned none
        // (legacy Vivox flow or older OpenSim). Hardcoded fallback should
        // only ever apply in OpenSim test environments — production SL
        // sims always advertise ICE servers via ProvisionVoiceAccountRequest.
        val iceServers = if (iceServerSpecs.isEmpty()) {
            listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())
        } else iceServerSpecs.map { spec ->
            PeerConnection.IceServer.builder(spec.urls).apply {
                spec.username?.let { setUsername(it) }
                spec.credential?.let { setPassword(it) }
            }.createIceServer()
        }

        val config = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }

        val session = VoiceSession(
            channelUri = channelUri,
            peerConnection = null,
            dispatcher = voiceDispatcher
        )

        val peerConnection = peerConnectionFactory?.createPeerConnection(
            config,
            session.observer
        )

        // Late-bind so the observer above can refer to the session before
        // the peerConnection actually exists. The observer surfaces ICE
        // candidates, signaling state, and remote tracks via session
        // flows; signaling layer (which is still pending — see VoiceSession
        // doc) is responsible for draining those flows and relaying.
        session.attachPeerConnection(peerConnection)

        // Add local audio track so the offer includes our mic
        localAudioTrack?.let { track ->
            peerConnection?.addTrack(track)
        }

        return session
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

/**
 * A real PeerConnection.Observer that surfaces WebRTC events as
 * coroutine-friendly flows on its owning [VoiceSession]. The previous
 * implementation was an empty-override stub which meant ICE candidates
 * were silently dropped — voice could not have established even if SDP
 * had worked.
 */
private class SessionObserver(private val session: VoiceSession) : PeerConnection.Observer {
    override fun onSignalingChange(state: PeerConnection.SignalingState) {
        session.onSignalingState(state)
    }
    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
        session.onIceConnectionState(state)
    }
    override fun onIceConnectionReceivingChange(receiving: Boolean) {}
    override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {}
    override fun onIceCandidate(candidate: IceCandidate) {
        // Push to the session's local-candidate flow so the signaling
        // layer (still pending; see VoiceSession class doc) can relay them
        // to the remote peer over the ParcelVoiceInfo / WebRTC
        // signaling channel.
        session.onLocalIceCandidate(candidate)
    }
    override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
    override fun onAddStream(stream: MediaStream) {}
    override fun onRemoveStream(stream: MediaStream) {}
    override fun onDataChannel(dataChannel: DataChannel) {}
    override fun onRenegotiationNeeded() {}
    override fun onAddTrack(receiver: RtpReceiver, streams: Array<out MediaStream>) {
        session.onRemoteTrack(receiver)
    }
}

/**
 * One WebRTC peer connection's worth of state for a voice channel.
 *
 * **What works after this change:** SDP offer/answer creation through
 * proper coroutine-suspending wrappers around WebRTC's callback API,
 * setLocalDescription / setRemoteDescription plumbing, ICE candidate
 * gathering surfaced via [localIceCandidates] flow, and remote ICE
 * candidate application via [addRemoteIceCandidate].
 *
 * Signaling orchestration (offer/answer exchange, local ICE trickle,
 * and remote ICE ingest/polling) is delegated to a dedicated layer so
 * transport + REST parsing remain separate from peer-connection state.
 */
class VoiceSession(
    val channelUri: String,
    @Volatile private var peerConnection: PeerConnection?,
    private val dispatcher: CoroutineDispatcher,
    private val signalingOrchestrator: VoiceSignalingOrchestrator = VoiceSignalingOrchestrator(
        transport = HttpVoiceSignalingTransport(),
        codec = JsonVoiceSignalingPayloadCodec()
    )
) {
    private var isConnected = false
    private var outputGain = 1.0f
    private var localAudioTrack: org.webrtc.AudioTrack? = null

    val observer: PeerConnection.Observer = SessionObserver(this)

    private val _signalingState = MutableStateFlow(PeerConnection.SignalingState.STABLE)
    val signalingState: StateFlow<PeerConnection.SignalingState> = _signalingState

    private val _iceConnectionState = MutableStateFlow(PeerConnection.IceConnectionState.NEW)
    val iceConnectionState: StateFlow<PeerConnection.IceConnectionState> = _iceConnectionState

    private val _localIceCandidates = MutableSharedFlow<IceCandidate>(
        replay = 0,
        extraBufferCapacity = 32,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    /** ICE candidates gathered locally that the signaling layer must relay. */
    val localIceCandidates: SharedFlow<IceCandidate> = _localIceCandidates
    private val sessionScope = CoroutineScope(dispatcher + SupervisorJob())
    @Volatile private var signalingJob: Job? = null

    fun attachPeerConnection(pc: PeerConnection?) {
        peerConnection = pc
    }

    internal fun onSignalingState(state: PeerConnection.SignalingState) {
        _signalingState.value = state
    }
    internal fun onIceConnectionState(state: PeerConnection.IceConnectionState) {
        _iceConnectionState.value = state
        isConnected = state == PeerConnection.IceConnectionState.CONNECTED ||
            state == PeerConnection.IceConnectionState.COMPLETED
    }
    internal fun onLocalIceCandidate(candidate: IceCandidate) {
        _localIceCandidates.tryEmit(candidate)
    }
    internal fun onRemoteTrack(receiver: RtpReceiver) {
        // Remote audio track arrived — WebRTC plays it through the
        // default audio output device automatically. We only surface
        // it for diagnostics / future per-peer volume control.
        android.util.Log.d("VoiceSession", "Remote track arrived: ${receiver.track()?.kind()}")
    }

    /**
     * Establish the voice session by completing the SDP offer/answer
     * exchange against the simulator's WebRTC voice endpoint.
     *
     * Linden's WebRTC voice REST shape (per the open-source viewer's
     * `LLWebRTCVoiceClient::sessionEstablished`) is a single POST with a
     * JSON body of `{ "jsep": { "type": "offer", "sdp": "..." } }` plus
     * the channel credentials carried as an `Authorization: Bearer ...`
     * header. The response carries `{ "jsep": { "type": "answer",
     * "sdp": "..." } }` with optional initial-candidate trickle data
     * inside `ice_candidates`. Subsequent local ICE candidates and
     * optional remote-candidate polling are handled by the signaling
     * orchestrator injected into this session.
     *
     * If the SDP offer can't be created (no peer connection) or the
     * POST fails, [isConnected] stays false so callers can fall back
     * to the legacy Vivox path or surface the failure to the user.
     */
    fun connect(uri: String, credentials: String) {
        signalingJob?.cancel()
        signalingJob = sessionScope.launch {
            try {
                signalingOrchestrator.connect(this@VoiceSession, uri, credentials, localIceCandidates)
                isConnected = true
                android.util.Log.i("VoiceSession", "Voice signaling complete for $uri")
            } catch (e: Exception) {
                android.util.Log.e("VoiceSession", "Failed to connect to voice channel", e)
                isConnected = false
            }
        }
    }

    fun disconnect() {
        try {
            signalingJob?.cancel()
            signalingJob = null
            localAudioTrack?.setEnabled(false)
            peerConnection?.close()
            isConnected = false
            android.util.Log.i("VoiceSession", "Disconnected from voice channel")
        } catch (e: Exception) {
            android.util.Log.e("VoiceSession", "Error during disconnect", e)
        }
    }

    /**
     * Create a real SDP offer using the suspend-friendly WebRTC wrappers.
     * Caller is responsible for shipping the resulting SDP to the remote
     * peer via the (still pending) signaling layer.
     */
    suspend fun createOffer(): String = withContext(dispatcher) {
        val pc = peerConnection ?: return@withContext ""
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
        }
        try {
            val offer = pc.createOfferSuspend(constraints)
            pc.setLocalDescriptionSuspend(offer)
            offer.description
        } catch (e: SdpException) {
            android.util.Log.e("VoiceSession", "createOffer: ${e.message}")
            ""
        }
    }

    /**
     * Apply a remote SDP offer and return our SDP answer. The reverse
     * direction of [createOffer] — used when the simulator pushes an
     * offer rather than expecting one from us.
     */
    suspend fun handleOffer(offer: String): String = withContext(dispatcher) {
        val pc = peerConnection ?: return@withContext ""
        try {
            pc.setRemoteDescriptionSuspend(SessionDescription(SessionDescription.Type.OFFER, offer))
            val answer = pc.createAnswerSuspend(MediaConstraints())
            pc.setLocalDescriptionSuspend(answer)
            answer.description
        } catch (e: SdpException) {
            android.util.Log.e("VoiceSession", "handleOffer: ${e.message}")
            ""
        }
    }

    /**
     * Apply a remote SDP answer (the reverse path from [createOffer]).
     */
    suspend fun handleAnswer(answer: String): Boolean = withContext(dispatcher) {
        val pc = peerConnection ?: return@withContext false
        try {
            pc.setRemoteDescriptionSuspend(SessionDescription(SessionDescription.Type.ANSWER, answer))
            true
        } catch (e: SdpException) {
            android.util.Log.e("VoiceSession", "handleAnswer: ${e.message}")
            false
        }
    }

    /** Apply a remote ICE candidate received from the signaling channel. */
    fun addRemoteIceCandidate(sdpMid: String, sdpMLineIndex: Int, candidate: String): Boolean =
        peerConnection?.addRemoteIceCandidate(sdpMid, sdpMLineIndex, candidate) ?: false
    
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
    val voiceServerUri: String,
    /**
     * STUN/TURN servers advertised by the simulator. Linden's WebRTC
     * voice spec returns these in the ProvisionVoiceAccountRequest
     * response so the client doesn't have to ship hardcoded ICE servers.
     * Empty list = legacy / Vivox flow that doesn't supply them.
     */
    val iceServers: List<IceServerSpec> = emptyList()
)

data class IceServerSpec(
    val urls: List<String>,
    val username: String? = null,
    val credential: String? = null
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
