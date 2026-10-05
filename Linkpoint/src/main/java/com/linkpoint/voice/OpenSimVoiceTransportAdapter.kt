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
 * Voice transport adapter for OpenSim grids (`GridKind.OPENSIM`).
 * Supports user custom STUN/TURN configurations, parcel local IDs, and OpenSim voice signaling fallback.
 */
class OpenSimVoiceTransportAdapter(
    private val capabilityManager: CapabilityManager,
    private val peerConnectionFactoryProvider: () -> PeerConnectionFactory?,
    private val sessionProvider: ((parcelLocalId: Int?) -> WebRtcVoiceSession)? = null,
    initialConfig: VoiceConfig? = null
) : VoiceTransportAdapter {

    companion object {
        private const val TAG = "OpenSimVoiceTransportAdapter"
    }

    override val gridKind: GridKind = GridKind.OPENSIM

    private var currentConfig: VoiceConfig? = initialConfig
    @Volatile private var activeSession: WebRtcVoiceSession? = null

    override fun resolveIceServerSpecs(
        provisionedIceServers: List<IceServerSpec>,
        config: VoiceConfig?
    ): List<IceServerSpec> {
        val merged = mutableListOf<IceServerSpec>()
        val activeCfg = config ?: currentConfig

        // 1. Prioritize user or grid custom TURN servers
        activeCfg?.customTurnServers?.let { merged.addAll(it) }

        // 2. Add custom STUN servers
        activeCfg?.customStunServers?.let { merged.addAll(it) }

        // 3. Add provisioned capability servers if custom ones weren't specified
        if (merged.isEmpty() && provisionedIceServers.isNotEmpty()) {
            merged.addAll(provisionedIceServers)
        }

        // 4. Fall back to OpenSim default STUN servers
        if (merged.isEmpty()) {
            val fallbacks = activeCfg?.fallbackStunServers?.takeIf { it.isNotEmpty() }
                ?: VoiceConfig.DEFAULT_OPENSIM_STUN_SERVERS
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

        val account = provisionVoiceAccount()
        val provisionedIce = account?.iceServers ?: emptyList()
        val resolvedIceServers = resolveIceServers(provisionedIce, currentConfig)

        val factory = peerConnectionFactoryProvider()
        if (factory == null && sessionProvider == null) {
            Log.w(TAG, "PeerConnectionFactory is null; cannot connect OpenSim WebRTC spatial voice")
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
            Log.i(TAG, "Connected OpenSim WebRTC spatial voice (parcel=$activeParcelId, iceCount=${resolvedIceServers.size})")
            true
        } catch (e: Exception) {
            Log.w(TAG, "OpenSim WebRTC spatial voice connection failed: ${e.message}", e)
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
