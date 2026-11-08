package com.linkpoint.voice

import com.linkpoint.protocol.GridKind
import org.webrtc.PeerConnection

/**
 * Interface defining grid-aware voice transport behavior.
 * Encapsulates ICE server resolution, WebRTC spatial/parcel session connection, and signaling setup.
 */
interface VoiceTransportAdapter {
    /** Target grid kind for this transport adapter. */
    val gridKind: GridKind

    /**
     * Resolves WebRTC ICE servers for peer connection establishment, combining
     * provisioned capability servers with grid defaults or user-configured custom servers.
     */
    fun resolveIceServers(
        provisionedIceServers: List<IceServerSpec>,
        config: VoiceConfig? = null
    ): List<PeerConnection.IceServer>

    /**
     * Resolves IceServerSpec objects for unit testing or inspection without WebRTC native bindings.
     */
    fun resolveIceServerSpecs(
        provisionedIceServers: List<IceServerSpec>,
        config: VoiceConfig? = null
    ): List<IceServerSpec>

    /**
     * Connects to spatial voice using grid-specific signaling rules and SDP exchange.
     * @param parcelLocalId Optional parcel local ID for parcel-scoped spatial voice.
     * @param config Optional voice configuration overrides.
     * @return True if connection succeeded.
     */
    suspend fun connectSpatialVoice(
        parcelLocalId: Int? = null,
        config: VoiceConfig? = null
    ): Boolean

    /** Disconnects the current voice transport session. */
    fun disconnect()

    /** Disposes any resources associated with this adapter. */
    fun dispose()
}

/** Helper extension to map IceServerSpec to WebRTC PeerConnection.IceServer */
fun IceServerSpec.toWebRtcIceServer(): List<PeerConnection.IceServer> {
    return urls.map { url ->
        val builder = PeerConnection.IceServer.builder(url)
        if (!username.isNullOrEmpty()) {
            builder.setUsername(username)
        }
        if (!credential.isNullOrEmpty()) {
            builder.setPassword(credential)
        }
        builder.createIceServer()
    }
}
