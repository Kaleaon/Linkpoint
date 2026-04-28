package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLUUID

class CircuitData(
    val host: Host,
    initialPacketInId: UInt = 0u,
    val heartbeatInterval: Float = 5f,
    val heartbeatTimeout: Float = 60f
) {
    val localEndPointId: LLUUID = LLUUID.generate()
    var remoteId: LLUUID = LLUUID.NULL
    var remoteSessionId: LLUUID = LLUUID.NULL

    var isAlive: Boolean = true
        private set
    var isBlocked: Boolean = false
        private set
    var allowTimeout: Boolean = true
        private set
    var isTrusted: Boolean = false
        private set

    var lastPingId: UByte = 0u
        private set
    var pingDelay: UInt = 1000u
        private set
    var pingDelayAveraged: Float = 1000f
        private set
    var pingsInTransit: Int = 0
        private set

    var packetsSent: Long = 0L
        private set
    var packetsReceived: Long = 0L
        private set
    var packetsLost: Long = 0L
        private set
    var bytesSent: Long = 0L
        private set
    var bytesReceived: Long = 0L
        private set

    private var packetsOutId: UInt = 0u
    private var packetsInId: UInt = initialPacketInId
    private var highestPacketId: UInt = initialPacketInId

    private var pingStartTimeMs: Long = 0L
    private var lastPingSendTimeMs: Long = 0L
    private var lastPingReceivedTimeMs: Long = System.currentTimeMillis()
    var nextPingSendTimeMs: Long = System.currentTimeMillis() + 1000L
        private set
    var lastPacketInTimeMs: Long = System.currentTimeMillis()
        private set

    private var lastPacketLog: Long = 0L
    private var unackedPacketCount: Int = 0
    private var unackedPacketBytes: Int = 0

    fun pingTimerStart() {
        pingStartTimeMs = System.currentTimeMillis()
        pingsInTransit++
        lastPingId++
        lastPingSendTimeMs = pingStartTimeMs
    }

    fun pingTimerStop(pingId: UByte) {
        if (pingsInTransit > 0) pingsInTransit--
        val rtt = (System.currentTimeMillis() - pingStartTimeMs).toUInt()
        setPingDelay(rtt)
        lastPingReceivedTimeMs = System.currentTimeMillis()
        nextPingSendTimeMs = lastPingReceivedTimeMs + heartbeatInterval.toLong() * 1000L
    }

    fun acknowledge(packetNum: UInt) {
        lastPacketInTimeMs = System.currentTimeMillis()
        packetsReceived++
    }

    fun setAlive(alive: Boolean) { isAlive = alive }
    fun setAllowTimeout(allow: Boolean) { allowTimeout = allow }
    fun setTrusted(trusted: Boolean) { isTrusted = trusted }
    fun setBlocked(blocked: Boolean) { isBlocked = blocked }

    fun addBytesIn(bytes: Long) { bytesReceived += bytes }
    fun addBytesOut(bytes: Long) { bytesSent += bytes }
    fun addPacketsOut() { packetsSent++ }

    fun nextPingId(): UByte { lastPingId++; return lastPingId }

    fun getAgeInSeconds(): Float =
        (System.currentTimeMillis() - lastPacketInTimeMs) / 1000f

    private fun setPingDelay(ms: UInt) {
        pingDelay = ms
        val alpha = 0.2f
        pingDelayAveraged = pingDelayAveraged * (1f - alpha) + ms.toFloat() * alpha
        pingDelayAveraged = pingDelayAveraged.coerceIn(100f, 2000f)
    }

    fun nextPacketOutId(): UInt {
        packetsOutId = (packetsOutId + 1u) and 0x00FFFFFFu
        if (packetsOutId == 0u) packetsOutId = 1u
        return packetsOutId
    }
}
