package com.linkpoint.protocol.transport

import com.linkpoint.protocol.circuit.LinkpointConstants

object ReliabilityConstants {
    val MESSAGE_TIMEOUT_MS: Long = LinkpointConstants.MESSAGE_TIMEOUT_MS
    val MESSAGE_MAX_RETRIES: Int = LinkpointConstants.MESSAGE_MAX_RETRIES
    val NEED_PING_TIMEOUT_MS: Long = LinkpointConstants.NEED_PING_TIMEOUT_MS
    val UNANSWERED_PINGS_DISCONNECT: Int = LinkpointConstants.UNANSWERED_PINGS_DISCONNECT
    val CELLULAR_UNANSWERED_PINGS_DISCONNECT: Int = LinkpointConstants.CELLULAR_UNANSWERED_PINGS_DISCONNECT
    val CELLULAR_KEEPALIVE_INTERVAL_MS: Long = LinkpointConstants.CELLULAR_KEEPALIVE_INTERVAL_MS
    const val CONSECUTIVE_ERROR_THRESHOLD: Int = 5
    const val INBOUND_DATA_STALL_MS: Long = 45_000L
    const val POST_RECONNECT_VERIFY_MS: Long = 12_000L
}
