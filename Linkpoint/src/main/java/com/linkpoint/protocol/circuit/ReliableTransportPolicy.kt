package com.linkpoint.protocol.circuit

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min
import kotlin.random.Random

internal interface TimeSource {
    fun nowMs(): Long
}

internal object SystemTimeSource : TimeSource {
    override fun nowMs(): Long = System.currentTimeMillis()
}

data class AckLatencyHistogram(
    val under250ms: Int,
    val from250To500ms: Int,
    val from500To1000ms: Int,
    val from1000To2000ms: Int,
    val over2000ms: Int
)

data class TransportMetricsSnapshot(
    val outstandingReliablePackets: Int,
    val retransmitCount: Long,
    val droppedExpiredPacketCount: Long,
    val ackLatencyHistogram: AckLatencyHistogram
)

internal class ReliableTransportPolicy(
    private val timeSource: TimeSource = SystemTimeSource,
    private val jitterRandom: Random = Random.Default,
    private val baseTimeoutMs: Long = LinkpointConstants.MESSAGE_TIMEOUT_MS,
    private val maxBackoffMs: Long = LinkpointConstants.MESSAGE_RETRY_BACKOFF_MAX_MS,
    private val expiryMs: Long = LinkpointConstants.RELIABLE_PACKET_EXPIRY_MS
) {
    private val duplicateAckGuard = ConcurrentHashMap.newKeySet<Int>()
    private val ackUnder250 = AtomicInteger(0)
    private val ack250to500 = AtomicInteger(0)
    private val ack500to1000 = AtomicInteger(0)
    private val ack1000to2000 = AtomicInteger(0)
    private val ackOver2000 = AtomicInteger(0)

    private val retransmitCount = java.util.concurrent.atomic.AtomicLong(0)
    private val droppedExpiredCount = java.util.concurrent.atomic.AtomicLong(0)

    fun shouldRetry(sentTimeMs: Long, retryCount: Int): Boolean {
        val elapsed = timeSource.nowMs() - sentTimeMs
        return elapsed >= computeRetryDelayMs(retryCount)
    }

    fun isExpired(firstSentTimeMs: Long): Boolean =
        timeSource.nowMs() - firstSentTimeMs >= expiryMs

    fun computeRetryDelayMs(retryCount: Int): Long {
        val shift = retryCount.coerceIn(0, 8)
        val expDelay = min(maxBackoffMs, baseTimeoutMs shl shift)
        val jitterSpan = (expDelay / 4).coerceAtLeast(1L)
        val jitter = jitterRandom.nextLong(-jitterSpan, jitterSpan + 1)
        return (expDelay + jitter).coerceIn(baseTimeoutMs, maxBackoffMs)
    }

    fun markRetransmit() {
        retransmitCount.incrementAndGet()
    }

    fun markExpiredDrop() {
        droppedExpiredCount.incrementAndGet()
    }

    fun onAck(sequenceNumber: Int, ackLatencyMs: Long): Boolean {
        val firstTime = duplicateAckGuard.add(sequenceNumber)
        if (!firstTime) return false
        when {
            ackLatencyMs < 250 -> ackUnder250.incrementAndGet()
            ackLatencyMs < 500 -> ack250to500.incrementAndGet()
            ackLatencyMs < 1000 -> ack500to1000.incrementAndGet()
            ackLatencyMs < 2000 -> ack1000to2000.incrementAndGet()
            else -> ackOver2000.incrementAndGet()
        }
        return true
    }

    fun metrics(outstandingReliablePackets: Int): TransportMetricsSnapshot = TransportMetricsSnapshot(
        outstandingReliablePackets = outstandingReliablePackets,
        retransmitCount = retransmitCount.get(),
        droppedExpiredPacketCount = droppedExpiredCount.get(),
        ackLatencyHistogram = AckLatencyHistogram(
            under250ms = ackUnder250.get(),
            from250To500ms = ack250to500.get(),
            from500To1000ms = ack500to1000.get(),
            from1000To2000ms = ack1000to2000.get(),
            over2000ms = ackOver2000.get()
        )
    )
}
