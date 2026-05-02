package com.linkpoint.protocol.circuit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the Karn/Jacobson estimator used by [com.linkpoint.protocol.messages
 * .UDPConnectionFixed] for reliable-resend timeout. The whole point is to
 * adapt to cellular RTT instead of using the fixed 5s default; if any of
 * these break we'd be back to the fixed-timeout pathology described in
 * docs/cellular_networking_plan.md.
 */
class RttEstimatorTest {

    @Test
    fun `cold start uses default RTO until first sample`() {
        val est = RttEstimator()
        assertEquals(RttEstimator.DEFAULT_RTO_MS, est.currentRtoMs)
        assertEquals(-1L, est.smoothedRttMs)
        assertEquals(0L, est.samples)
    }

    @Test
    fun `first sample seeds SRTT and RTTVAR per RFC 6298`() {
        val est = RttEstimator()
        est.recordCleanSample(120L)
        // RFC 6298: SRTT = R; RTTVAR = R/2
        assertEquals(120L, est.smoothedRttMs)
        // RTO = SRTT + max(G, 4 * RTTVAR) = 120 + max(50, 240) = 360
        // … clamped up to MIN_RTO_MS if below floor.
        assertTrue(est.currentRtoMs >= RttEstimator.MIN_RTO_MS)
        assertEquals(1L, est.samples)
    }

    @Test
    fun `RTO never drops below MIN_RTO_MS even on a fast Wi-Fi path`() {
        val est = RttEstimator()
        // Feed it 30ms samples (typical Wi-Fi) repeatedly so the variance
        // collapses toward zero. RTO would drift toward ~30ms without the
        // floor; with the floor we expect MIN_RTO_MS exactly.
        repeat(40) { est.recordCleanSample(30L) }
        assertEquals(RttEstimator.MIN_RTO_MS, est.currentRtoMs)
    }

    @Test
    fun `RTO is capped at MAX_RTO_MS even when the path is awful`() {
        val est = RttEstimator()
        // 8s sample is implausible-but-allowed; drives SRTT up. The clamp
        // matters because the unanswered-ping watchdog should backstop —
        // letting the resend timer run to e.g. 32s would defeat the whole
        // point of having the watchdog.
        est.recordCleanSample(8_000L)
        assertEquals(RttEstimator.MAX_RTO_MS, est.currentRtoMs)
    }

    @Test
    fun `Karn backoff doubles RTO per retransmit and clamps at MAX_RTO_MS`() {
        val est = RttEstimator()
        est.recordCleanSample(200L)
        val baseline = est.currentRtoMs
        est.onRetransmit()
        assertEquals(minOf(RttEstimator.MAX_RTO_MS, baseline * 2), est.currentRtoMs)
        est.onRetransmit()
        assertEquals(minOf(RttEstimator.MAX_RTO_MS, baseline * 4), est.currentRtoMs)
        // Beyond MAX_BACKOFF_SHIFT, further onRetransmit calls don't grow it.
        repeat(10) { est.onRetransmit() }
        assertEquals(RttEstimator.MAX_RTO_MS, est.currentRtoMs)
    }

    @Test
    fun `clean sample resets Karn backoff`() {
        val est = RttEstimator()
        est.recordCleanSample(200L)
        est.onRetransmit()
        est.onRetransmit()
        val backedOff = est.currentRtoMs
        // Receiving a clean ACK after the retransmits drops backoff to 0.
        // SRTT moves slightly toward the new sample but stays in the
        // same order of magnitude.
        est.recordCleanSample(220L)
        assertTrue(
            "Backoff should clear after clean sample (was=$backedOff, now=${est.currentRtoMs})",
            est.currentRtoMs < backedOff
        )
    }

    @Test
    fun `recordCleanSample ignores non-positive and implausibly large samples`() {
        val est = RttEstimator()
        est.recordCleanSample(0L)
        est.recordCleanSample(-5L)
        est.recordCleanSample(RttEstimator.MAX_PLAUSIBLE_SAMPLE_MS + 1L)
        assertEquals("No samples should have been recorded", 0L, est.samples)
        assertEquals(RttEstimator.DEFAULT_RTO_MS, est.currentRtoMs)
    }

    @Test
    fun `reset clears state to cold start`() {
        val est = RttEstimator()
        est.recordCleanSample(200L)
        est.onRetransmit()
        est.reset()
        assertEquals(RttEstimator.DEFAULT_RTO_MS, est.currentRtoMs)
        assertEquals(-1L, est.smoothedRttMs)
        assertEquals(0L, est.samples)
    }

    @Test
    fun `SRTT smoothing follows RFC 6298 weighting`() {
        val est = RttEstimator()
        est.recordCleanSample(100L)
        // SRTT = (7 * 100 + 1 * 200) / 8 = 112
        est.recordCleanSample(200L)
        assertEquals(112L, est.smoothedRttMs)
    }

    @Test
    fun `cellular-shaped trace stays within sane RTO bounds`() {
        val est = RttEstimator()
        // Synthetic: idle LTE ~80ms, occasional spikes to 800ms (congestion).
        val trace = listOf(80L, 90L, 110L, 95L, 800L, 120L, 100L, 85L, 90L, 250L)
        trace.forEach { est.recordCleanSample(it) }
        val rto = est.currentRtoMs
        assertTrue(
            "Cellular RTO should land between 300ms and 6s (was $rto)",
            rto in RttEstimator.MIN_RTO_MS..RttEstimator.MAX_RTO_MS
        )
        // Should be well under the legacy 5s default once the estimator
        // has converged on the typical 100ms-ish RTT.
        assertNotEquals(RttEstimator.DEFAULT_RTO_MS, rto)
    }
}
