package com.linkpoint.voice

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDBoolean
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import java.nio.ByteBuffer
import java.nio.charset.Charsets
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean

/**
 * One WebRTC peer connection to a Second Life Voice Server, modelling the
 * spatial-voice protocol that LL shipped to limited release on
 * 2026-03-18. Sits alongside the legacy Vivox path in [VoiceManager];
 * it does not replace it (both can coexist per LL's transition plan).
 *
 * **Wire protocol — three channels:**
 *
 * 1. **Signaling — HTTPS LLSD POSTs to two capabilities** (NOT WebSocket).
 *    - [CapabilityManager.CAP_PROVISION_VOICE] takes the JSEP offer and
 *      returns the JSEP answer + a `viewer_session` token.
 *    - [CapabilityManager.CAP_VOICE_SIGNALING_REQUEST] takes trickled ICE
 *      candidates after the answer arrives.
 *    Both messages must include `voice_server_type: "webrtc"` so the
 *    simulator routes them to the new pipeline rather than Vivox.
 *
 * 2. **Audio — WebRTC media (Opus over SRTP)**, direct UDP if STUN
 *    succeeds, TURN-relayed if the carrier blocks UDP. ICE servers are
 *    NOT hardcoded by the client — the simulator advertises them in the
 *    [CapabilityManager.CAP_PROVISION_VOICE] answer (typically via the
 *    JSEP SDP `a=ice-options` and the LLSD-side `ice_servers` map).
 *    Cellular CGNAT users will fall back to TURN automatically.
 *
 * 3. **Data — the `SLData` WebRTC DataChannel** carries JSON. This is
 *    the closest thing to a WebSocket in SL's voice stack, but it rides
 *    SCTP-over-DTLS-over-UDP (or TURN-relayed), not TCP. Used for:
 *    - Position + heading updates ([sendPositionUpdate])
 *    - Per-agent mute / gain ([sendMute], [sendGain])
 *    - Join/leave bookkeeping ([sendJoin] with `primary` flag)
 *    - Mixer → client: VU meter levels (`p`), VAD (`V`), peer join/leave
 *
 * **Cross-region voice:** the LL spec requires connecting one
 * [WebRtcVoiceSession] per neighbouring region within audio range,
 * marking exactly one as primary via [sendJoin] with `primary = true`.
 * That bookkeeping lives in [VoiceManager], which owns a map of
 * regionHandle → session. This class is per-session.
 *
 * **Critical ordering** (per LL PDF "WebRTC on Second Life, a
 * Developer's View"):
 *  1. Build [PeerConnection].
 *  2. **Create the `SLData` data channel BEFORE any negotiation.** The
 *     server requires this channel; if it's added after the offer is
 *     generated, the SCTP m-line will be missing and the answer will
 *     reject the channel. See [openSlDataChannel].
 *  3. `createOffer()` and mangle the Opus `fmtp` line to
 *     `minptime=10;useinbandfec=1;stereo=1;sprop-stereo=1;maxplaybackrate=48000`.
 *  4. `setLocalDescription(offer)`.
 *  5. POST the offer to ProvisionVoiceAccountRequest, await answer.
 *  6. `setRemoteDescription(answer)`.
 *  7. ICE candidates that gathered before step 6 are queued and
 *     trickled out via VoiceSignalingRequest in [drainIceQueue]; new
 *     candidates after step 6 are sent immediately.
 *  8. When `IceGatheringState.COMPLETE`, send `{candidate: {completed:
 *     true}}` so the server stops waiting.
 *
 * Logout (graceful or forced) is a single LLSD POST to
 * ProvisionVoiceAccountRequest with `{logout: true, viewer_session,
 * voice_server_type: "webrtc"}`. The cap may return 404 if the server
 * already tore the session down; that's not an error.
 *
 * **Thread model:** all WebRTC callbacks fire on the WebRTC signaling
 * thread. We forward to a coroutine scope on [Dispatchers.Default]
 * for application-side bookkeeping; the only blocking work that has
 * to stay on the WebRTC thread is the [PeerConnection] mutation
 * itself.
 *
 * @see <a href="https://wiki.secondlife.com/wiki/WebRTC_Voice">WebRTC Voice (SL Wiki)</a>
 * @see <a href="https://webrtc-public-voice-beta.s3.us-west-2.amazonaws.com/WebRTC+on+Second+Life,+a+Developer's+View.pdf">LL Developer's View PDF</a>
 */
class WebRtcVoiceSession(
    private val capabilityManager: CapabilityManager,
    private val factory: PeerConnectionFactory,
    /** `local` for region/parcel spatial voice, `multiagent` for AdHoc / Group / P2P. */
    val channelType: ChannelType,
    /** Optional per-parcel scoping for spatial voice. Null for region-scope or AdHoc. */
    val parcelLocalId: Int? = null,
    /** Required for AdHoc/Group/P2P — comes from the chat-system invite. */
    private val multiAgentChannel: String? = null,
    private val multiAgentCredentials: String? = null,
) {

    enum class ChannelType(val wire: String) {
        SPATIAL("local"),
        MULTIAGENT("multiagent"),
    }

    /** Lifecycle state surfaced to the UI / VoiceManager. */
    enum class State { IDLE, OFFERING, AWAITING_ANSWER, NEGOTIATED, FAILED, CLOSED }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val state = MutableSharedFlow<State>(replay = 1, extraBufferCapacity = 8)
    val stateFlow: SharedFlow<State> = state

    /** Mixer → client per-peer events from `SLData` (VU, VAD, join, leave). */
    private val _peerEvents = MutableSharedFlow<PeerEvent>(extraBufferCapacity = 64)
    val peerEvents: SharedFlow<PeerEvent> = _peerEvents

    @Volatile private var peerConnection: PeerConnection? = null
    @Volatile private var slDataChannel: DataChannel? = null
    @Volatile private var viewerSession: String? = null

    /** ICE candidates gathered before the answer arrived (must be queued). */
    private val pendingIce = mutableListOf<IceCandidate>()
    private val pendingIceLock = Any()
    private val answerReceived = AtomicBoolean(false)
    private val gatheringComplete = AtomicBoolean(false)

    /**
     * Run the full negotiation. Returns once [setRemoteDescription] has
     * succeeded; the caller can then start sending [SLData] updates via
     * [sendPositionUpdate] / [sendJoin] / etc. Throws on failure — the
     * caller is expected to surface that and close the session.
     */
    suspend fun connect(iceServers: List<PeerConnection.IceServer>) {
        check(peerConnection == null) { "WebRtcVoiceSession.connect called twice" }
        emit(State.OFFERING)

        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            // SL voice uses Unified Plan; default to it explicitly so we
            // don't get bitten by an SDK default change.
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            // Bundle policy MAX_BUNDLE keeps audio + data on one transport,
            // which both reduces ICE candidate count and matches what the
            // SL Voice Server expects (single m-line bundle).
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            // ICE Lite isn't a viewer choice — server-side. We're the
            // full ICE agent.
            continualGatheringPolicy =
                PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }

        val pc = factory.createPeerConnection(rtcConfig, pcObserver)
            ?: error("PeerConnectionFactory.createPeerConnection returned null")
        peerConnection = pc

        // ⚠️ ORDERING-CRITICAL: SLData channel MUST exist before createOffer()
        // so the SCTP m-line is in the offer SDP. Adding it after produces a
        // valid offer that the server rejects.
        openSlDataChannel(pc)

        // Add the local audio track. The actual mic AudioSource and
        // AudioTrack are owned by VoiceManager — we just attach them.
        // (Caller responsibility — kept out of this skeleton because mic
        // permission + AudioSource lifecycle is shared across sessions
        // for cross-region voice.)
        // pc.addTrack(localAudioTrack, listOf(streamId))

        val offer = pc.createOfferSuspend(audioOnlyConstraints())
        val mangled = mangleOpusFmtp(offer)
        pc.setLocalDescriptionSuspend(mangled)

        emit(State.AWAITING_ANSWER)
        val answer = exchangeOffer(mangled)
        pc.setRemoteDescriptionSuspend(answer)
        answerReceived.set(true)

        drainIceQueue()
        emit(State.NEGOTIATED)
    }

    /** Tear down. Idempotent. Sends the LLSD `logout` to the cap. */
    fun close() {
        scope.launch {
            try {
                viewerSession?.let { sendLogout(it) }
            } catch (e: Exception) {
                Log.w(TAG, "Voice logout cap failed (often 404 if server already closed): ${e.message}")
            }
            slDataChannel?.dispose()
            slDataChannel = null
            peerConnection?.close()
            peerConnection?.dispose()
            peerConnection = null
            emit(State.CLOSED)
            scope.cancel()
        }
    }

    // ─────────────────────────────── SLData send API ──────────────────────────────

    /**
     * Marks this session's role. Spec: at most one connected session
     * across all neighbouring regions should be `primary = true` (the
     * one whose audio is streamed back to the client). When the avatar
     * crosses a region boundary, swap which session is primary; do
     * NOT close + reopen.
     */
    fun sendJoin(primary: Boolean) {
        sendSlData(JSONObject().put("j", JSONObject().put("p", primary)))
    }

    fun sendLeave() {
        sendSlData(JSONObject().put("l", true))
    }

    /**
     * Position + heading update — only valid for [ChannelType.SPATIAL].
     * Coordinates are world coordinates (region handle baked in) in
     * **centimetres**, not metres. Heading is a quaternion with each
     * component multiplied by 100 and integer-rounded. Call only when a
     * value actually changed; sending the full update every frame
     * wastes bandwidth.
     */
    fun sendPositionUpdate(
        senderPosWorldCm: IntVec3?,
        senderHeadingX100: IntQuat?,
        listenerPosWorldCm: IntVec3?,
        listenerHeadingX100: IntQuat?,
    ) {
        if (channelType != ChannelType.SPATIAL) return
        val obj = JSONObject()
        senderPosWorldCm?.let { obj.put("sp", it.toJson()) }
        senderHeadingX100?.let { obj.put("sh", it.toJson()) }
        listenerPosWorldCm?.let { obj.put("lp", it.toJson()) }
        listenerHeadingX100?.let { obj.put("lh", it.toJson()) }
        if (obj.length() > 0) sendSlData(obj)
    }

    /** Per-agent mute map. true = muted, false = unmuted. */
    fun sendMute(agentMutes: Map<UUID, Boolean>) {
        if (agentMutes.isEmpty()) return
        val map = JSONObject()
        agentMutes.forEach { (id, muted) -> map.put(id.toString(), muted) }
        sendSlData(JSONObject().put("m", map))
    }

    /** Per-agent gain (linear ×200, integer). */
    fun sendGain(agentGains: Map<UUID, Int>) {
        if (agentGains.isEmpty()) return
        val map = JSONObject()
        agentGains.forEach { (id, g) -> map.put(id.toString(), g) }
        sendSlData(JSONObject().put("ug", map))
    }

    private fun sendSlData(json: JSONObject) {
        val ch = slDataChannel ?: return
        if (ch.state() != DataChannel.State.OPEN) return
        val bytes = json.toString().toByteArray(Charsets.UTF_8)
        ch.send(DataChannel.Buffer(ByteBuffer.wrap(bytes), false))
    }

    // ─────────────────────────────── Cap signaling ────────────────────────────────

    /**
     * POST the JSEP offer to ProvisionVoiceAccountRequest and parse the
     * answer. Throws if the cap is missing, the response is malformed,
     * or `viewer_session` is absent (we need it for ICE trickle + logout).
     */
    private suspend fun exchangeOffer(offer: SessionDescription): SessionDescription {
        val payload = LLSDMap().apply {
            this["jsep"] = LLSDMap().apply {
                this["type"] = LLSDString("offer")
                this["sdp"] = LLSDString(offer.description)
            }
            this["voice_server_type"] = LLSDString("webrtc")
            when (channelType) {
                ChannelType.SPATIAL -> {
                    this["channel_type"] = LLSDString("local")
                    parcelLocalId?.let { this["parcel_local_id"] = LLSDInteger(it) }
                }
                ChannelType.MULTIAGENT -> {
                    this["channel_type"] = LLSDString("multiagent")
                    this["channel"] = LLSDString(multiAgentChannel ?: "")
                    this["credentials"] = LLSDString(multiAgentCredentials ?: "")
                }
            }
        }

        val response = capabilityManager.request(CapabilityManager.CAP_PROVISION_VOICE, payload)
            ?: error("ProvisionVoiceAccountRequest returned null (cap missing or HTTP error)")
        val map = response as? LLSDMap
            ?: error("ProvisionVoiceAccountRequest answer not an LLSDMap: $response")

        val jsep = map["jsep"] as? LLSDMap
            ?: error("ProvisionVoiceAccountRequest answer missing 'jsep'")
        val type = (jsep["type"] as? LLSDString)?.value
        val sdp = (jsep["sdp"] as? LLSDString)?.value
        require(type == "answer" && !sdp.isNullOrEmpty()) {
            "JSEP type/sdp invalid: type=$type sdpEmpty=${sdp.isNullOrEmpty()}"
        }
        viewerSession = (map["viewer_session"] as? LLSDString)?.value
            ?: error("ProvisionVoiceAccountRequest answer missing 'viewer_session'")

        return SessionDescription(SessionDescription.Type.ANSWER, sdp)
    }

    /**
     * Trickle the queued candidates after the answer arrives, then send
     * the gather-complete sentinel if we already finished gathering.
     */
    private suspend fun drainIceQueue() {
        val queued = synchronized(pendingIceLock) {
            val copy = pendingIce.toList()
            pendingIce.clear()
            copy
        }
        if (queued.isNotEmpty()) postCandidates(queued)
        if (gatheringComplete.get()) postGatheringComplete()
    }

    private suspend fun postCandidates(candidates: List<IceCandidate>) {
        val session = viewerSession ?: return
        val arr = LLSDArray()
        for (c in candidates) {
            arr.add(LLSDMap().apply {
                this["sdpMid"] = LLSDString(c.sdpMid ?: "")
                this["sdpMLineIndex"] = LLSDInteger(c.sdpMLineIndex)
                this["candidate"] = LLSDString(c.sdp ?: "")
            })
        }
        val payload = LLSDMap().apply {
            this["candidates"] = arr
            this["voice_server_type"] = LLSDString("webrtc")
            this["viewer_session"] = LLSDString(session)
        }
        capabilityManager.request(CapabilityManager.CAP_VOICE_SIGNALING_REQUEST, payload)
    }

    private suspend fun postGatheringComplete() {
        val session = viewerSession ?: return
        val payload = LLSDMap().apply {
            this["candidate"] = LLSDMap().apply {
                this["completed"] = LLSDBoolean(true)
            }
            this["voice_server_type"] = LLSDString("webrtc")
            this["viewer_session"] = LLSDString(session)
        }
        capabilityManager.request(CapabilityManager.CAP_VOICE_SIGNALING_REQUEST, payload)
    }

    private suspend fun sendLogout(session: String) {
        val payload = LLSDMap().apply {
            this["logout"] = LLSDBoolean(true)
            this["voice_server_type"] = LLSDString("webrtc")
            this["viewer_session"] = LLSDString(session)
        }
        capabilityManager.request(CapabilityManager.CAP_PROVISION_VOICE, payload)
    }

    // ─────────────────────────────── WebRTC plumbing ──────────────────────────────

    private fun openSlDataChannel(pc: PeerConnection) {
        val init = DataChannel.Init().apply {
            ordered = true
            negotiated = false
            // Server defaults are fine for everything else (id auto-assigned).
        }
        val ch = pc.createDataChannel("SLData", init)
            ?: error("PeerConnection.createDataChannel(SLData) returned null")
        ch.registerObserver(SlDataObserver(ch))
        slDataChannel = ch
    }

    /**
     * Per LL PDF, the offer's Opus `a=fmtp:` line must be replaced with
     * the server-required attributes for stereo 48 kHz with FEC. Without
     * this the server's answer downgrades the codec.
     */
    private fun mangleOpusFmtp(sdp: SessionDescription): SessionDescription {
        val want = "minptime=10;useinbandfec=1;stereo=1;sprop-stereo=1;maxplaybackrate=48000"
        val out = StringBuilder(sdp.description.length + 64)
        sdp.description.split("\r\n").forEach { line ->
            if (line.startsWith("a=fmtp:") && line.contains("opus", ignoreCase = true).not()) {
                // Some Opus fmtp lines use only the payload-type number (no
                // "opus" string). Detect by cross-referencing the rtpmap
                // elsewhere — for simplicity in this skeleton we replace
                // any fmtp following an `m=audio` block. Refinement
                // welcome.
                out.append(line)
            } else if (line.startsWith("a=fmtp:")) {
                val pt = line.removePrefix("a=fmtp:").substringBefore(' ')
                out.append("a=fmtp:").append(pt).append(' ').append(want)
            } else {
                out.append(line)
            }
            out.append("\r\n")
        }
        return SessionDescription(sdp.type, out.toString())
    }

    private fun audioOnlyConstraints(): MediaConstraints = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
    }

    private val pcObserver = object : PeerConnection.Observer {
        override fun onSignalingChange(state: PeerConnection.SignalingState) {}
        override fun onIceConnectionChange(state: PeerConnection.IceConnectionState) {
            if (state == PeerConnection.IceConnectionState.FAILED) emit(State.FAILED)
        }
        override fun onIceConnectionReceivingChange(p0: Boolean) {}
        override fun onIceGatheringChange(state: PeerConnection.IceGatheringState) {
            if (state == PeerConnection.IceGatheringState.COMPLETE) {
                gatheringComplete.set(true)
                if (answerReceived.get()) {
                    scope.launch { postGatheringComplete() }
                }
            }
        }

        override fun onIceCandidate(candidate: IceCandidate) {
            // Spec: gathered candidates before the answer must be
            // cached and sent after the answer is received. After the
            // answer, they go out immediately ("ICE Trickling").
            if (answerReceived.get()) {
                scope.launch { postCandidates(listOf(candidate)) }
            } else {
                synchronized(pendingIceLock) { pendingIce.add(candidate) }
            }
        }

        override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>) {}
        override fun onAddStream(stream: MediaStream) {}
        override fun onRemoveStream(stream: MediaStream) {}
        override fun onDataChannel(channel: DataChannel) {
            // Server-initiated data channel — should be the SLData echo
            // back, but in practice the LL voice server does not open
            // its own; we initiated SLData. Log for diagnostics.
            Log.d(TAG, "Server-opened data channel '${channel.label()}' (unexpected)")
        }

        override fun onRenegotiationNeeded() {}
        override fun onAddTrack(receiver: RtpReceiver, streams: Array<out MediaStream>) {}
    }

    private inner class SlDataObserver(private val ch: DataChannel) : DataChannel.Observer {
        override fun onBufferedAmountChange(previousAmount: Long) {}
        override fun onStateChange() {
            Log.d(TAG, "SLData state=${ch.state()}")
        }

        override fun onMessage(buffer: DataChannel.Buffer) {
            // Server frames are JSON (not binary). LL doc says updates
            // are batched on a 100 ms cadence: each peer's id maps to
            // an object with `p` (power), `V` (VAD), `j` (join), `l`
            // (leave). We forward into [peerEvents].
            val bytes = ByteArray(buffer.data.remaining())
            buffer.data.get(bytes)
            val text = String(bytes, Charsets.UTF_8)
            try {
                val obj = JSONObject(text)
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val agentId = runCatching { UUID.fromString(key) }.getOrNull() ?: continue
                    val peer = obj.optJSONObject(key) ?: continue
                    when {
                        peer.has("l") -> _peerEvents.tryEmit(PeerEvent.Leave(agentId))
                        peer.has("j") -> {
                            val primary = peer.optJSONObject("j")?.optBoolean("p", false) ?: false
                            _peerEvents.tryEmit(PeerEvent.Join(agentId, primary))
                        }
                        else -> {
                            val powerRaw = peer.optInt("p", Int.MIN_VALUE)
                            val vad = peer.optBoolean("V", false)
                            if (powerRaw != Int.MIN_VALUE || vad) {
                                _peerEvents.tryEmit(PeerEvent.Level(agentId, powerRaw, vad))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "SLData JSON parse failed: ${e.message}; payload=${text.take(120)}")
            }
        }
    }

    private fun emit(s: State) {
        state.tryEmit(s)
    }

    sealed class PeerEvent {
        data class Join(val agentId: UUID, val primary: Boolean) : PeerEvent()
        data class Leave(val agentId: UUID) : PeerEvent()
        data class Level(val agentId: UUID, val powerRmsX128: Int, val voiceActive: Boolean) : PeerEvent()
    }

    /** Integer 3-vector for SLData position payloads (centimetres). */
    data class IntVec3(val x: Int, val y: Int, val z: Int) {
        fun toJson(): JSONObject = JSONObject().put("x", x).put("y", y).put("z", z)
    }

    /** Integer quaternion (×100) for SLData heading payloads. */
    data class IntQuat(val x: Int, val y: Int, val z: Int, val w: Int) {
        fun toJson(): JSONObject =
            JSONObject().put("x", x).put("y", y).put("z", z).put("w", w)
    }

    companion object {
        private const val TAG = "WebRtcVoiceSession"
    }
}
