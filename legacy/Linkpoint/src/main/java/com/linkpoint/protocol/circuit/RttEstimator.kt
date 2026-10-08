package com.linkpoint.protocol.circuit

import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Karn/Jacobson smoothed-RTT estimator that drives the reliable-resend
 * timeout in [com.linkpoint.protocol.messages.UDPConnectionFixed].
 *
 * The SL viewer reference and Lumiya both used a fixed 5s timeout
 * ([LinkpointConstants.MESSAGE_TIMEOUT_MS]). On Wi-Fi with ~30ms RTT that's
 * ~150x the actual RTT — fine, just slow to recover from real loss. On
 * cellular it cuts both ways: LTE idle RTT sits at 80–250ms (over-resending
 * is rare) but a congested or fading cell can push RTT to 2–5s, which means
 * 5s legitimately fires as a false-loss while the simulator's reply is still
 * en route. Both the Balakrishnan et al. comparison ("A Comparison of
 * Mechanisms for Improving TCP Performance over Wireless Links", 1996) and
 * the TCP-RRE paper (IEEE 6733597) point at the same fix: estimate RTT and
 * back off on retransmit (Karn), don't pace on a fixed clock.
 *
 * Algorithm (RFC 6298 with cellular-friendly clamps):
 *   - First sample:    SRTT = R, RTTVAR = R/2
 *   - Subsequent:      RTTVAR = 3/4 * RTTVAR + 1/4 * |SRTT - R|
 *                      SRTT   = 7/8 * SRTT   + 1/8 * R
 *   - RTO = SRTT + max(G, 4 * RTTVAR), clamped to [MIN_RTO_MS, MAX_RTO_MS]
 *   - Karn's algorithm: never sample a retransmitted packet (ambiguous).
 *   - Karn's backoff: double RTO on every retry; reset to estimator on next
 *     clean ACK.
 *
 * Thread-safety: All mutating ops are guarded by a monitor on the instance.
 * Reads of [currentRtoMs] and [smoothedRttMs] are lock-free (the underlying
 * fields are AtomicLong) so the I/O thread can poll without contending.
 */
class RttEstimator {

    private val srttMs = AtomicLong(-1L)
    private val rttvarMs = AtomicLong(0L)
    private val rtoMs = AtomicLong(DEFAULT_RTO_MS)
    private val backoffShift = AtomicLong(0L)
    private val sampleCount = AtomicLong(0L)

    /** Current SRTT in ms, or -1 until the first sample. */
    val smoothedRttMs: Long get() = srttMs.get()

    /** Current RTO in ms, including any Karn backoff. */
    val currentRtoMs: Long
        get() {
            val base = rtoMs.get()
            val shift = backoffShift.get().toInt().coerceIn(0, MAX_BACKOFF_SHIFT)
            val backed = if (shift == 0) base else min(MAX_RTO_MS, base shl shift)
            return backed.coerceIn(MIN_RTO_MS, MAX_RTO_MS)
        }

    /** Number of clean RTT samples ingested. */
    val samples: Long get() = sampleCount.get()

    /**
     * Record a clean RTT sample. **Only call this for ACKs that resolve a
     * packet that was sent exactly once** (retries == 0). Karn's algorithm
     * forbids sampling retransmitted packets because the ACK is ambiguous —
     * we don't know which copy it acknowledges.
     */
    @Synchronized
    fun recordCleanSample(rttMs: Long) {
        if (rttMs <= 0L || rttMs > MAX_PLAUSIBLE_SAMPLE_MS) return
        val current = srttMs.get()
        if (current < 0L) {
            srttMs.set(rttMs)
            rttvarMs.set(rttMs / 2)
        } else {
            val delta = abs(current - rttMs)
            val newVar = (rttvarMs.get() * 3 + delta) / 4
            val newSrtt = (current * 7 + rttMs) / 8
            rttvarMs.set(newVar)
            srttMs.set(newSrtt)
        }
        recomputeRto()
        backoffShift.set(0L)
        sampleCount.incrementAndGet()
    }

    /**
     * Apply Karn's exponential backoff. Call once per retransmission. RTO
     * doubles on each retry up to [MAX_BACKOFF_SHIFT] and is restored to the
     * estimator-derived value on the next clean sample.
     */
    fun onRetransmit() {
        backoffShift.updateAndGet { (it + 1).coerceAtMost(MAX_BACKOFF_SHIFT.toLong()) }
    }

    /** Reset to the cold-start defaults. Used on circuit reconnect. */
    @Synchronized
    fun reset() {
        srttMs.set(-1L)
        rttvarMs.set(0L)
        rtoMs.set(DEFAULT_RTO_MS)
        backoffShift.set(0L)
        sampleCount.set(0L)
    }

    private fun recomputeRto() {
        val srtt = srttMs.get()
        if (srtt < 0L) {
            rtoMs.set(DEFAULT_RTO_MS)
            return
        }
        val variance = rttvarMs.get()
        val raw = srtt + max(CLOCK_GRANULARITY_MS, 4 * variance)
        rtoMs.set(raw.coerceIn(MIN_RTO_MS, MAX_RTO_MS))
    }

    companion object {
        /**
         * Lower bound of the RTO. RFC 6298 recommends 1s for general
         * Internet TCP; we drop to 300ms because the SL circuit's
         * per-message reliability is opt-in and small (PacketAck etc.)
         * which makes false-loss cheap, and because cellular LTE idle RTT
         * is regularly under 150ms — a 1s floor over-waits.
         */
        const val MIN_RTO_MS = 300L

        /**
         * Upper bound. Sized to absorb multi-second fades on cellular and
         * satellite links (7+ second RTT spikes observed in the wild) without
         * triggering premature retransmissions that flood the socket buffers.
         * The unanswered-ping watchdog ([LinkpointConstants.UNANSWERED_PINGS_DISCONNECT])
         * still backstops the circuit at 25–60s.
         */
        const val MAX_RTO_MS = 20_000L

        /** Default cold-start RTO before the first sample. Matches Lumiya's 5s. */
        const val DEFAULT_RTO_MS = LinkpointConstants.MESSAGE_TIMEOUT_MS

        /**
         * Cap how far Karn's backoff can shift the RTO. 4 doublings of the
         * 6s ceiling is already 96s — well past any reasonable cellular
         * handoff. Beyond that the higher-level reconnect path is the right
         * tool, not more retransmits.
         */
        const val MAX_BACKOFF_SHIFT = 4

        /**
         * RTT samples larger than this are dropped as nonsense (probably a
         * very-late duplicate ACK or a clock skew). The unanswered-ping
         * watchdog catches genuinely-broken paths.
         */
        const val MAX_PLAUSIBLE_SAMPLE_MS = 30_000L

        /** RFC 6298 G — clock granularity floor on the RTTVAR term. */
        const val CLOCK_GRANULARITY_MS = 50L
    }
}
