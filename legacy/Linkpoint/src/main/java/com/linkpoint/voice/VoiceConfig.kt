package com.linkpoint.voice

/**
 * Configuration options for grid-aware voice transport.
 */
data class VoiceConfig(
    val customStunServers: List<IceServerSpec> = emptyList(),
    val customTurnServers: List<IceServerSpec> = emptyList(),
    val parcelLocalId: Int? = null,
    val isVoiceEnabled: Boolean = true,
    val fallbackStunServers: List<IceServerSpec> = emptyList()
) {
    companion object {
        val DEFAULT_SL_STUN_SERVERS = listOf(
            IceServerSpec(urls = listOf("stun:stun1.agni.secondlife.io:3478")),
            IceServerSpec(urls = listOf("stun:stun2.agni.secondlife.io:3478")),
            IceServerSpec(urls = listOf("stun:stun3.agni.secondlife.io:3478")),
            IceServerSpec(urls = listOf("stun:stun.l.google.com:19302")),
            IceServerSpec(urls = listOf("stun:stun2.l.google.com:19302")),
            IceServerSpec(urls = listOf("stun:stun.nextcloud.com:443")),
            IceServerSpec(urls = listOf("stun:stun.twilio.com:3478")),
        )

        val DEFAULT_OPENSIM_STUN_SERVERS = listOf(
            IceServerSpec(urls = listOf("stun:stun.l.google.com:19302")),
            IceServerSpec(urls = listOf("stun:stun2.l.google.com:19302")),
            IceServerSpec(urls = listOf("stun:stun.twilio.com:3478"))
        )
    }
}
