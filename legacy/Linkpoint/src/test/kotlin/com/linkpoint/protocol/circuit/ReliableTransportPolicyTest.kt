package com.linkpoint.protocol.circuit

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReliableTransportPolicyTest {

    private class FakeTime(var now: Long) : TimeSource {
        override fun nowMs(): Long = now
    }

    @Test
    fun `bounded exponential backoff with jitter stays within caps`() {
        val policy = ReliableTransportPolicy(
            timeSource = FakeTime(0),
            jitterRandom = Random(1234),
            baseTimeoutMs = 1_000,
            maxBackoffMs = 8_000,
            expiryMs = 60_000
        )

        repeat(8) { retry ->
            val delay = policy.computeRetryDelayMs(retry)
            assertTrue(delay in 1_000..8_000)
        }
    }

    @Test
    fun `retry and expiry decisions are deterministic with fake clock`() {
        val time = FakeTime(0)
        val policy = ReliableTransportPolicy(timeSource = time, jitterRandom = Random(1), baseTimeoutMs = 1_000, maxBackoffMs = 4_000, expiryMs = 10_000)

        assertFalse(policy.shouldRetry(sentTimeMs = 0, retryCount = 0))
        time.now = 1_500
        assertTrue(policy.shouldRetry(sentTimeMs = 0, retryCount = 0))

        assertFalse(policy.isExpired(0))
        time.now = 10_001
        assertTrue(policy.isExpired(0))
    }

    @Test
    fun `duplicate ack handling and metrics remain stable`() {
        val policy = ReliableTransportPolicy(timeSource = FakeTime(0), jitterRandom = Random(7))

        assertTrue(policy.onAck(10, 100))
        assertFalse(policy.onAck(10, 120))
        assertTrue(policy.onAck(11, 600))
        policy.markRetransmit()
        policy.markRetransmit()
        policy.markExpiredDrop()

        val metrics = policy.metrics(outstandingReliablePackets = 3)
        assertEquals(3, metrics.outstandingReliablePackets)
        assertEquals(2, metrics.retransmitCount)
        assertEquals(1, metrics.droppedExpiredPacketCount)
        assertEquals(1, metrics.ackLatencyHistogram.under250ms)
        assertEquals(1, metrics.ackLatencyHistogram.from500To1000ms)
    }
}
