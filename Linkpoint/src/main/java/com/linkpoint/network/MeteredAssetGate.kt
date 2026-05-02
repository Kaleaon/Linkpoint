package com.linkpoint.network

import android.util.Log
import kotlinx.coroutines.sync.Semaphore

/**
 * Concurrency cap for HTTPS asset fetches that adapts to whether the active
 * network is metered (cellular, tethered hotspot).
 *
 * **Why this exists.** The IEEE 6733597 ("TCP-RRE") study showed that on
 * cellular, ACK return packets share the narrow uplink with whatever else
 * the device is sending — and when that uplink saturates, ACK delivery
 * stalls, which stalls the unrelated downlink even though the downlink
 * itself is healthy ("uplink bufferbloat poisons downlink ACK timing").
 * The mitigation that does not require a new TCP variant or ISP proxy is
 * trivial at the application layer: cap how many concurrent HTTPS streams
 * the app launches on a metered link, so we don't fight ourselves for the
 * uplink.
 *
 * Linkpoint's hot HTTPS path is texture and mesh prefetch via
 * [CronetHttpClient]/the OkHttp fallback. On Wi-Fi we want as much
 * parallelism as the JVM/Cronet engine will give us; on cellular 2-3 in
 * flight is the sweet spot from the IMC 2016 cellular-bufferbloat
 * measurements (more than that and median TTFB doubles for unrelated
 * traffic — including the SL UDP circuit's pings).
 *
 * **Usage**:
 * ```
 * val gate = MeteredAssetGate.shared
 * gate.withPermit { cronet.get(url) }
 * ```
 * Callers that want to bypass the gate (e.g. login traffic) just don't
 * call it — this is opt-in throttling, not a global queue.
 *
 * **Wiring**: Update the cap when the metered state changes via
 * [updateMetered]. The recommended wire-up is a long-lived collector on
 * `ConnectionQualityManager.isMetered` in `LinkpointApp.onCreate`.
 */
class MeteredAssetGate(
    initialMetered: Boolean = false
) {

    @Volatile
    private var current: Semaphore = buildSemaphore(initialMetered)

    @Volatile
    private var meteredNow: Boolean = initialMetered

    /**
     * Whether the gate is currently in metered mode (low concurrency).
     * Exposed for diagnostics and tests; do not branch on this in
     * production code — `withPermit` already does the right thing.
     */
    val isMetered: Boolean get() = meteredNow

    /** Current concurrency cap — [METERED_PERMITS] or [UNMETERED_PERMITS]. */
    val concurrencyCap: Int
        get() = if (meteredNow) METERED_PERMITS else UNMETERED_PERMITS

    /**
     * Update the metered state. Atomically swaps in a fresh semaphore sized
     * to the new cap. In-flight callers continue to drain on the old
     * semaphore — they were already counted against the previous cap and
     * forcing them to re-acquire would deadlock the system.
     */
    @Synchronized
    fun updateMetered(metered: Boolean) {
        if (metered == meteredNow) return
        meteredNow = metered
        current = buildSemaphore(metered)
        Log.i(TAG, "Asset-fetch concurrency cap → ${if (metered) METERED_PERMITS else UNMETERED_PERMITS} (metered=$metered)")
    }

    /**
     * Acquire a permit for the duration of [block]. Suspending; respects
     * coroutine cancellation. Callers MUST use this rather than holding a
     * permit across multiple operations — the semaphore swap on
     * [updateMetered] only changes future acquisitions.
     */
    suspend fun <R> withPermit(block: suspend () -> R): R {
        // Snapshot once: we want a single semaphore to govern this call,
        // not be racing the metered-state swap. The cap change takes effect
        // on the *next* withPermit call.
        val sem = current
        sem.acquire()
        try {
            return block()
        } finally {
            sem.release()
        }
    }

    private fun buildSemaphore(metered: Boolean): Semaphore =
        Semaphore(if (metered) METERED_PERMITS else UNMETERED_PERMITS)

    companion object {
        private const val TAG = "MeteredAssetGate"

        /**
         * Concurrency cap on metered links. 2 in flight matches the ICMP/
         * cellular-bufferbloat measurements from IMC 2016: ≤2 streams
         * keeps median uplink ACK delay under 200ms, ≥4 streams pushes it
         * past 1s on commodity LTE. Two is also the SL viewer reference's
         * own UDP texture-transfer cap ([com.linkpoint.protocol.circuit
         * .LinkpointConstants.MAX_UDP_TEXTURE_TRANSFERS]) so we stay
         * consistent across protocols.
         */
        const val METERED_PERMITS = 2

        /**
         * Concurrency cap on unmetered links. 8 is generous enough to
         * saturate a residential Wi-Fi but not so large that we exhaust
         * Cronet's per-host pool (default 6) — matches the OkHttp
         * `dispatcher.maxRequestsPerHost` we use elsewhere.
         */
        const val UNMETERED_PERMITS = 8

        /**
         * Process-wide shared instance. Built lazily so it's safe to
         * reference from anywhere; the actual metered state is wired in
         * `LinkpointApp.onCreate` from `ConnectionQualityManager.isMetered`.
         */
        @Volatile private var sharedInstance: MeteredAssetGate? = null

        val shared: MeteredAssetGate
            get() = sharedInstance ?: synchronized(this) {
                sharedInstance ?: MeteredAssetGate().also { sharedInstance = it }
            }
    }
}
