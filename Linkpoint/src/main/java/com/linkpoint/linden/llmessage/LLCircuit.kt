package com.linkpoint.linden.llmessage

import com.linkpoint.linden.llcommon.LLTimer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class LLCircuit(val host: Host) {
    private val seqNum = AtomicInteger(1)
    private val pendingAcks: MutableSet<UInt> = ConcurrentHashMap.newKeySet()
    private val waitingAcks: MutableMap<UInt, ByteArray> = ConcurrentHashMap()
    private val pingTimer = LLTimer()
    private val timeoutTimer = LLTimer()

    var pingDelay: Float = 0.1f
    var pingTimeout: Float = 60f
    var valid: Boolean = true
    var trusted: Boolean = false
    var packetsSent: Long = 0L
    var packetsReceived: Long = 0L
    var bytesIn: Long = 0L
    var bytesOut: Long = 0L
    var lastPingId: UByte = 0u
    var thisOutID: UInt = 0u
    var thisInID: UInt = 0u
    var blocked: Boolean = false

    fun nextSequenceNumber(): UInt = seqNum.getAndIncrement().toUInt()

    fun ackPacket(seq: UInt) {
        pendingAcks.add(seq)
        waitingAcks.remove(seq)
    }

    fun needsResend(seq: UInt): Boolean = seq in waitingAcks

    fun queueForResend(seq: UInt, packet: ByteArray) {
        waitingAcks[seq] = packet
    }

    fun getPendingAcks(): Set<UInt> = pendingAcks.toSet()

    fun clearPendingAcks() = pendingAcks.clear()

    fun isAlive(): Boolean = valid && !pingTimer.hasExpired()

    fun updateLastPacketInTime() = pingTimer.setTimerExpirySec(pingTimeout.toDouble())

    fun getResendList(): Map<UInt, ByteArray> = waitingAcks.toMap()

    fun markInvalid() { valid = false }
}
