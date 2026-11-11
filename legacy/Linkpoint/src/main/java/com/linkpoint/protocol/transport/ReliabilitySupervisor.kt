package com.linkpoint.protocol.transport

class ReliabilitySupervisor(
    private val timeSource: () -> Long = { System.currentTimeMillis() }
) {
    data class Snapshot(
        val lastReceiveTime: Long,
        val unansweredPings: Int,
        val isCellular: Boolean,
        val consecutiveSendErrors: Int,
        val postReconnectStartedAt: Long,
        val postReconnectPacketsReceived: Int
    )

    fun shouldDisconnectForPing(snapshot: Snapshot): Boolean {
        val threshold = if (snapshot.isCellular) ReliabilityConstants.CELLULAR_UNANSWERED_PINGS_DISCONNECT else ReliabilityConstants.UNANSWERED_PINGS_DISCONNECT
        return snapshot.unansweredPings >= threshold
    }

    fun shouldForceRebind(snapshot: Snapshot): Boolean {
        return timeSource() - snapshot.lastReceiveTime >= ReliabilityConstants.INBOUND_DATA_STALL_MS ||
            snapshot.consecutiveSendErrors >= ReliabilityConstants.CONSECUTIVE_ERROR_THRESHOLD
    }

    fun shouldEscalatePostReconnect(snapshot: Snapshot): Boolean {
        if (snapshot.postReconnectStartedAt <= 0L) return false
        return snapshot.postReconnectPacketsReceived <= 0 &&
            timeSource() - snapshot.postReconnectStartedAt >= ReliabilityConstants.POST_RECONNECT_VERIFY_MS
    }
}
