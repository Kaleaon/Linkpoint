package com.linkpoint.protocol.transport

import com.linkpoint.protocol.circuit.LinkpointConstants

object TransportConstants {
    val BUFFER_SIZE: Int = LinkpointConstants.MAX_MESSAGE_SIZE
    val SELECTOR_TIMEOUT_MS: Long = LinkpointConstants.DEFAULT_IDLE_INTERVAL_MS
    val PACKET_HEADER_SIZE: Int = LinkpointConstants.PACKET_HEADER_SIZE
    const val SOCKET_REBIND_COOLDOWN_MS: Long = 30_000L
}
