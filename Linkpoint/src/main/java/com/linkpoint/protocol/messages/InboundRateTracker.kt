package com.linkpoint.protocol.messages

/**
 * Rolling per-second buckets of inbound UDP packet count and byte volume.
 *
 * Cumulative totals (`packetsReceived`, `bytesReceived`) tell you how much
 * traffic arrived over the entire session but hide *when* it arrived. The
 * 2026-04-26 Athanasia capture showed 220 objects loading cleanly, then 24
 * seconds of complete inbound silence before the circuit watchdog tripped.
 * Cumulative counters can't distinguish "steady stream" from "burst then
 * nothing" — per-second buckets can.
 *
 * Implementation note: receive happens on a single I/O thread (`SLCircuitIO`
 * / `CircuitThread`), so [record] is effectively single-producer. [snapshot]
 * may be called from the debug-report thread, hence the synchronized block.
 */
class InboundRateTracker(private val windowSeconds: Int = 60) {

    private val packetBuckets = LongArray(windowSeconds)
    private val byteBuckets = LongArray(windowSeconds)
    private val bucketSeconds = LongArray(windowSeconds)
    private val lock = Any()

    /**
     * Record one received packet of [bytes] bytes against the current second.
     * If the slot held a stale second (i.e. the window has wrapped), it is
     * reset before incrementing.
     */
    fun record(bytes: Int) {
        val nowSec = System.currentTimeMillis() / 1000L
        val slot = ((nowSec % windowSeconds).toInt() + windowSeconds) % windowSeconds
        synchronized(lock) {
            if (bucketSeconds[slot] != nowSec) {
                bucketSeconds[slot] = nowSec
                packetBuckets[slot] = 0
                byteBuckets[slot] = 0
            }
            packetBuckets[slot]++
            byteBuckets[slot] += bytes.toLong()
        }
    }

    /**
     * Return the most recent [maxBuckets] one-second buckets, oldest first.
     * Buckets that were never written within their second appear as zeros so
     * the gaps are visible in the report.
     */
    fun snapshot(maxBuckets: Int = windowSeconds): List<RateBucket> {
        val n = maxBuckets.coerceIn(1, windowSeconds)
        val nowSec = System.currentTimeMillis() / 1000L
        val result = ArrayList<RateBucket>(n)
        synchronized(lock) {
            for (offset in (n - 1) downTo 0) {
                val sec = nowSec - offset
                val slot = ((sec % windowSeconds).toInt() + windowSeconds) % windowSeconds
                if (bucketSeconds[slot] == sec) {
                    result += RateBucket(sec, packetBuckets[slot], byteBuckets[slot])
                } else {
                    result += RateBucket(sec, 0L, 0L)
                }
            }
        }
        return result
    }

    fun reset() {
        synchronized(lock) {
            packetBuckets.fill(0L)
            byteBuckets.fill(0L)
            bucketSeconds.fill(0L)
        }
    }

    data class RateBucket(
        val epochSecond: Long,
        val packets: Long,
        val bytes: Long
    )
}
