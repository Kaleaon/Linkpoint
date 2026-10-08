package com.linkpoint.voice

import com.linkpoint.protocol.GridKind
import com.linkpoint.protocol.capabilities.CapabilityManager
import org.webrtc.PeerConnectionFactory

/**
 * Factory creating dynamic [VoiceTransportAdapter] instances based on [GridKind].
 */
open class VoiceTransportAdapterFactory(
    private val capabilityManager: CapabilityManager,
    private val peerConnectionFactoryProvider: () -> PeerConnectionFactory?
) {
    open fun createAdapter(
        gridKind: GridKind,
        config: VoiceConfig? = null
    ): VoiceTransportAdapter {
        return when (gridKind) {
            GridKind.SECOND_LIFE -> SLWebRTCTransportAdapter(
                capabilityManager = capabilityManager,
                peerConnectionFactoryProvider = peerConnectionFactoryProvider,
                initialConfig = config
            )
            GridKind.OPENSIM -> OpenSimVoiceTransportAdapter(
                capabilityManager = capabilityManager,
                peerConnectionFactoryProvider = peerConnectionFactoryProvider,
                initialConfig = config
            )
        }
    }
}
