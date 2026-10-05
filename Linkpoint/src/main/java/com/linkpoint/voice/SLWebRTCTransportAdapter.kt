package com.linkpoint.voice

import android.util.Log
import com.linkpoint.protocol.GridKind
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDString
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory

/**
 * Voice transport adapter for Second Life (`GridKind.SECOND_LIFE`).
 * Uses Second Life capability-driven signaling and Agni STUN fallbacks.
 */
class SLWebRTCTransportAdapter(
    private val capabilityManager: CapabilityManager,
    private val peerConnectionFactoryProvider: () -> PeerConnectionFactory?,
    private val sessionProvider: ((parcelLocalId: Int?) -> WebRtcVoiceSession)? = null,
    initialConfig: VoiceConfig? = null
) : VoiceTransportAdapter {

    companion object {
        private const val TAG = "SLWebRTCTransportAdapter"
    }

    override val gridKind: GridKind = GridKind.SECOND_LIFE

    private var currentConfig: VoiceConfig? = initialConfig
    @Volatile private var activeSession: WebRtcVoiceSession? = null

    override fun resolveIceServerSpecs(
        provisionedIceServers: List<IceServerSpec>,
        config: VoiceConfig?
    ): List<IceServerSpec> {
        val merged = mutableListOf<IceServerSpec>()

        // 1. Provisioned capability ICE servers
        if (provisionedIceServers.isNotEmpty()) {
            merged.addAll(provisionedIceServers)
        }

        // 2. Custom STUN / TURN configuration overrides
        val activeCfg = config ?: currentConfig
        activeCfg?.customTurnServers?.let { merged.addAll(it) }
        activeCfg?.customStunServers?.let { merged.addAll(it) }

        // 3. Fall back to Agni STUN defaults if no servers provided
        if (merged.isEmpty()) {
            val fallbacks = activeCfg?.fallbackStunServers?.takeIf { it.isNotEmpty() }
                ?: VoiceConfig.DEFAULT_SL_STUN_SERVERS
            merged.addAll(fallbacks)
        }

        return merged
    }

    override fun resolveIceServers(
        provisionedIceServers: List<IceServerSpec>,
        config: VoiceConfig?
    ): List<PeerConnection.IceServer> {
        return resolveIceServerSpecs(provisionedIceServers, config)
            .flatMap { it.toWebRtcIceServer() }
    }

    override suspend fun connectSpatialVoice(
        parcelLocalId: Int?,
        config: VoiceConfig?
    ): Boolean {
        currentConfig = config ?: currentConfig
        val activeParcelId = parcelLocalId ?: currentConfig?.parcelLocalId

        // Fetch provisioned account info to obtain sim-advertised ICE servers
        val account = provisionVoiceAccount()
        val provisionedIce = account?.iceServers ?: emptyList()
        val resolvedIceServers = resolveIceServers(provisionedIce, currentConfig)

        val factory = peerConnectionFactoryProvider()
        if (factory == null && sessionProvider == null) {
            Log.w(TAG, "PeerConnectionFactory is null; cannot connect Second Life WebRTC spatial voice")
            return false
        }

        disconnect()

        val session = sessionProvider?.invoke(activeParcelId) ?: WebRtcVoiceSession(
            capabilityManager = capabilityManager,
            factory = factory!!,
            channelType = WebRtcVoiceSession.ChannelType.SPATIAL,
            parcelLocalId = activeParcelId
        )

        activeSession = session

        return try {
            session.connect(resolvedIceServers)
            session.sendJoin(primary = true)
            Log.i(TAG, "Connected Second Life WebRTC spatial voice (parcel=$activeParcelId, iceCount=${resolvedIceServers.size})")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Second Life WebRTC spatial voice connection failed: ${e.message}", e)
            session.close()
            activeSession = null
            false
        }
    }

    private suspend fun provisionVoiceAccount(): VoiceAccountInfo? {
        val response = capabilityManager.request(CapabilityManager.CAP_PROVISION_VOICE)
        if (response !is LLSDMap) return null

        val credsMap = response.getMap("voice_credentials")
        val username = credsMap?.getString("username")
            ?: response.getString("username") ?: ""
        val password = credsMap?.getString("password")
            ?: response.getString("password") ?: ""
        val serverUri = response.getString("voice_server_url")
            ?: response.getString("voice_sip_uri_hostname")
            ?: ""

        val iceServers = parseIceServers(response.getArray("ice_servers"))

        return VoiceAccountInfo(
            username = username,
            password = password,
            voiceServerUri = serverUri,
            iceServers = iceServers
        )
    }

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

    override fun disconnect() {
        activeSession?.close()
        activeSession = null
    }

    override fun dispose() {
        disconnect()
    }
}
