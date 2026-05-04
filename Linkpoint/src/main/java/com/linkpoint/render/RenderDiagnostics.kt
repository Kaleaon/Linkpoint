package com.linkpoint.render

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.utils.SessionLogRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Auto-diagnostics for the rendering subsystems (Filament + OpenGL ES via
 * Lumiya). Every meaningful lifecycle moment funnels through here:
 *
 *  - Engine / context creation (Filament Engine, GL context)
 *  - Surface attach / detach / size change
 *  - SwapChain create / destroy
 *  - Viewport apply (with size) and any size mismatch retry
 *  - Frame loop start, first-frame, periodic FPS heartbeat, stall detection
 *  - Shutdown
 *  - Errors (init failures, GL_*_ERROR codes, exceptions during draw)
 *
 * Output is appended to [SessionLogRecorder] under [SessionLogRecorder.EntryType.RENDER]
 * so render events are interleaved with packets, HTTP, capability
 * activity, and login/auth events — making the "what was the renderer
 * doing when X happened" question answerable from a single timeline
 * without a separate render-only log.
 *
 * Recording is automatic: as soon as [SessionLogRecorder] is started at
 * app launch, render events flow in. There is no opt-in step, no
 * settings flag, and no per-call "is render logging enabled" guard.
 *
 * The class is a single object so the renderer subsystems don't need to
 * carry a reference to it; this is intentional given how many surface
 * callbacks (some on the GL thread, some on the main thread, some on
 * the Choreographer driver) need to emit. Internal state is small and
 * thread-safe via atomics.
 */
object RenderDiagnostics {

    private const val TAG = "RenderDiagnostics"

    private const val HEARTBEAT_INTERVAL_MS = 5_000L
    private const val STALL_THRESHOLD_MS = 2_000L
    private const val FRAME_INTERVAL_HISTORY_LIMIT = 512

    /**
     * Last frame-rendered timestamp per subsystem ("Filament", "Lumiya").
     * Used to compute FPS / stall detection without coupling to the
     * subsystem's own counters.
     */
    private val lastFrameWallClock = mutableMapOf<String, AtomicLong>()
    private val frameCounters = mutableMapOf<String, AtomicLong>()
    private val lastReportedFrameCount = mutableMapOf<String, AtomicLong>()
    private val activeSubsystems = mutableSetOf<String>()
    private val frameIntervalHistoryMs = mutableMapOf<String, ArrayDeque<Long>>()

    private val heartbeatStarted = AtomicBoolean(false)
    private val heartbeatScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var heartbeatJob: Job? = null
    private val lumiyaUboUploadTimeNsTotal = AtomicLong(0L)
    private val lumiyaUboUploadSamples = AtomicLong(0L)
    private val lumiyaFrameAllocations = AtomicLong(0L)
    private val filamentSwapChainCreateCount = AtomicLong(0L)
    private val filamentSwapChainDestroyCount = AtomicLong(0L)
    private val filamentSwapChainFailureCount = AtomicLong(0L)
    private val lumiyaContextCreateCount = AtomicLong(0L)

    /**
     * Mark a render subsystem as active and ensure the heartbeat ticker
     * is running. Idempotent.
     */
    @Synchronized
    private fun trackSubsystem(subsystem: String) {
        activeSubsystems += subsystem
        lastFrameWallClock.getOrPut(subsystem) { AtomicLong(0) }
        frameCounters.getOrPut(subsystem) { AtomicLong(0) }
        lastReportedFrameCount.getOrPut(subsystem) { AtomicLong(0) }
        frameIntervalHistoryMs.getOrPut(subsystem) { ArrayDeque() }
        if (heartbeatStarted.compareAndSet(false, true)) {
            startHeartbeat()
        }
    }

    @Synchronized
    private fun recordFrame(subsystem: String) {
        trackSubsystem(subsystem)
        val now = System.currentTimeMillis()
        val previous = lastFrameWallClock[subsystem]?.getAndSet(now) ?: 0L
        frameCounters[subsystem]?.incrementAndGet()
        if (previous <= 0L) return
        val deltaMs = (now - previous).coerceAtLeast(0L)
        val history = frameIntervalHistoryMs.getOrPut(subsystem) { ArrayDeque() }
        history.addLast(deltaMs)
        while (history.size > FRAME_INTERVAL_HISTORY_LIMIT) {
            history.removeFirst()
        }
    }

    private fun startHeartbeat() {
        heartbeatJob = heartbeatScope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL_MS)
                emitHeartbeats()
            }
        }
    }

    private fun emitHeartbeats() {
        val now = System.currentTimeMillis()
        synchronized(this) { activeSubsystems.toList() }.forEach { subsystem ->
            val total = frameCounters[subsystem]?.get() ?: return@forEach
            val previously = lastReportedFrameCount[subsystem]?.getAndSet(total) ?: 0L
            val delta = total - previously
            val fps = (delta.toDouble() * 1000.0 / HEARTBEAT_INTERVAL_MS)
            val lastFrame = lastFrameWallClock[subsystem]?.get() ?: 0L
            val sinceLastFrame = if (lastFrame == 0L) -1L else now - lastFrame
            val details = buildString {
                append("frames=$total")
                append(" Δ=${delta}/${HEARTBEAT_INTERVAL_MS}ms")
                append(" fps=${"%.1f".format(fps)}")
                if (sinceLastFrame >= 0) append(" sinceLast=${sinceLastFrame}ms")
                if (subsystem == "Lumiya") {
                    val samples = lumiyaUboUploadSamples.get()
                    if (samples > 0) {
                        val avgUs = (lumiyaUboUploadTimeNsTotal.get() / samples) / 1_000.0
                        append(" uboAvgUs=${"%.1f".format(avgUs)}")
                    }
                    append(" frameAllocs=${lumiyaFrameAllocations.get()}")
                }
            }
            SessionLogRecorder.logRender(subsystem, "heartbeat", details)
            // Stall: we expect a frame at least every STALL_THRESHOLD_MS
            // while a subsystem is active. Emit a distinct event so it's
            // greppable separate from heartbeat.
            if (lastFrame > 0 && sinceLastFrame > STALL_THRESHOLD_MS) {
                SessionLogRecorder.logRender(
                    subsystem,
                    "stall",
                    "no frame for ${sinceLastFrame}ms (threshold=${STALL_THRESHOLD_MS}ms)"
                )
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────
    // Filament events
    // ────────────────────────────────────────────────────────────────────

    fun filamentInitStart(viewportHint: String? = null) {
        trackSubsystem("Filament")
        SessionLogRecorder.logRender("Filament", "init_start", viewportHint)
    }

    fun filamentEngineCreated(extra: String? = null) {
        SessionLogRecorder.logRender("Filament", "engine_created", extra)
    }

    fun filamentInitDone(durationMs: Long, components: String) {
        SessionLogRecorder.logRender(
            "Filament",
            "init_done",
            "durationMs=$durationMs components=$components"
        )
    }

    fun filamentInitFailed(stage: String, error: Throwable?) {
        SessionLogRecorder.logRender(
            "Filament",
            "init_failed",
            "stage=$stage error=${error?.message ?: "null"}"
        )
        if (error != null) {
            SessionLogRecorder.logError("Filament", "init failed at stage=$stage", error)
        }
    }

    fun filamentSurfaceAttached(width: Int, height: Int) {
        SessionLogRecorder.logRender("Filament", "surface_attached", "${width}x${height}")
        logLifecycle(NetworkLogger.Level.INFO, "🖼️ Filament surface attached ${width}x${height}")
    }

    fun filamentSurfaceDetached(reason: String) {
        SessionLogRecorder.logRender("Filament", "surface_detached", reason)
        // WARN level so it's visible at default log filter — surface loss
        // is the strongest correlate with the inbound-stall/reconnect bug.
        logLifecycle(NetworkLogger.Level.WARN, "🖼️ Filament surface detached: $reason")
    }

    fun filamentSurfaceChanged(width: Int, height: Int) {
        SessionLogRecorder.logRender("Filament", "surface_changed", "${width}x${height}")
        logLifecycle(NetworkLogger.Level.DEBUG, "🖼️ Filament surface changed ${width}x${height}")
    }

    fun filamentSwapChainCreated(width: Int, height: Int) {
        filamentSwapChainCreateCount.incrementAndGet()
        SessionLogRecorder.logRender(
            "Filament",
            "swapchain_created",
            if (width > 0 && height > 0) "${width}x${height}" else null
        )
        val dims = if (width > 0 && height > 0) " (${width}x${height})" else ""
        logLifecycle(NetworkLogger.Level.INFO, "🖼️ Filament SwapChain created$dims")
    }

    fun filamentSwapChainDestroyed(reason: String) {
        filamentSwapChainDestroyCount.incrementAndGet()
        SessionLogRecorder.logRender("Filament", "swapchain_destroyed", reason)
        logLifecycle(NetworkLogger.Level.WARN, "🖼️ Filament SwapChain destroyed: $reason")
    }

    fun filamentSwapChainFailed(reason: String) {
        filamentSwapChainFailureCount.incrementAndGet()
        SessionLogRecorder.logRender("Filament", "swapchain_failed", reason)
        logLifecycle(NetworkLogger.Level.ERROR, "🖼️ Filament SwapChain FAILED: $reason")
    }

    /**
     * Mirror render-lifecycle events into [NetworkLogger] under the LIFECYCLE
     * category so they appear interleaved with UDP/HTTP traffic in
     * `network_log_*.txt`. SessionLogRecorder still gets the structured
     * record for the render-focused timeline; this is purely additional.
     */
    private fun logLifecycle(level: NetworkLogger.Level, message: String) {
        NetworkLogger.log(level, NetworkLogger.Category.LIFECYCLE, message)
    }

    fun filamentViewport(width: Int, height: Int, callSite: String) {
        SessionLogRecorder.logRender(
            "Filament",
            "viewport",
            "${width}x${height} via=$callSite"
        )
    }

    fun filamentFrame() {
        recordFrame("Filament")
        val total = frameCounters["Filament"]?.get() ?: 0L
        if (total == 1L) {
            SessionLogRecorder.logRender("Filament", "first_frame", "frameCount=1")
        }
    }

    fun filamentShutdown(reason: String) {
        SessionLogRecorder.logRender("Filament", "shutdown", reason)
    }

    /**
     * Drawing paused — render thread is alive and the Filament Engine is
     * intact, but `renderFrame` is gated and skips the GPU portion. Used
     * when an Activity/panel covers the world view so the renderer goes
     * to background instead of being torn down (Lumiya parity).
     */
    fun filamentDrawingPaused(reason: String) {
        SessionLogRecorder.logRender("Filament", "drawing_paused", reason)
        logLifecycle(NetworkLogger.Level.INFO, "🖼️ Filament drawing paused: $reason")
    }

    fun filamentDrawingResumed(reason: String) {
        SessionLogRecorder.logRender("Filament", "drawing_resumed", reason)
        logLifecycle(NetworkLogger.Level.INFO, "🖼️ Filament drawing resumed: $reason")
    }

    /**
     * A `renderFrame` body threw. Captured-and-swallowed so the render
     * thread keeps running instead of dying with an uncaught exception
     * (which previously crashed the app and looped via auto-restart).
     */
    fun filamentFrameError(error: Throwable) {
        SessionLogRecorder.logRender(
            "Filament",
            "frame_error",
            "${error.javaClass.simpleName}: ${error.message}"
        )
        logLifecycle(
            NetworkLogger.Level.ERROR,
            "🖼️ Filament frame error (swallowed): ${error.javaClass.simpleName}: ${error.message}"
        )
    }

    // ────────────────────────────────────────────────────────────────────
    // OpenGL / Lumiya events
    // ────────────────────────────────────────────────────────────────────

    fun glSurfaceCreated() {
        lumiyaContextCreateCount.incrementAndGet()
        trackSubsystem("Lumiya")
        SessionLogRecorder.logRender("Lumiya", "surface_created")
    }

    fun glSurfaceChanged(width: Int, height: Int) {
        SessionLogRecorder.logRender("Lumiya", "surface_changed", "${width}x${height}")
    }

    fun glSurfaceDestroyed() {
        SessionLogRecorder.logRender("Lumiya", "surface_destroyed")
    }

    fun glContextInfo(vendor: String, renderer: String, version: String, maxTextureSize: Int) {
        SessionLogRecorder.logRender(
            "Lumiya",
            "gl_info",
            "vendor=$vendor renderer=$renderer version=$version maxTex=$maxTextureSize"
        )
    }

    /**
     * Driver-capability summary, emitted once after [glContextInfo].
     * Captures the pre-context probe profile (Vulkan presence,
     * OpenGL ES requested version, vendor family) alongside the
     * post-context capability snapshot and resolved feature flags
     * so a session log alone can answer "which optional passes did
     * the renderer actually run on this device?".
     */
    fun glDriverCapabilities(profileSummary: String, capsSummary: String, featureFlags: String) {
        SessionLogRecorder.logRender(
            "Lumiya",
            "driver_caps",
            "profile=[$profileSummary] caps=[$capsSummary] flags=[$featureFlags]"
        )
    }

    fun glInitDone(durationMs: Long) {
        SessionLogRecorder.logRender("Lumiya", "init_done", "durationMs=$durationMs")
    }

    fun glInitFailed(error: Throwable?) {
        SessionLogRecorder.logRender("Lumiya", "init_failed", error?.message)
        if (error != null) {
            SessionLogRecorder.logError("Lumiya", "GL init failed", error)
        }
    }

    fun glFrame() {
        recordFrame("Lumiya")
        val total = frameCounters["Lumiya"]?.get() ?: 0L
        if (total == 1L) {
            SessionLogRecorder.logRender("Lumiya", "first_frame", "frameCount=1")
        }
    }

    fun glGlobalUboUpload(uploadTimeNs: Long, frameAllocations: Int) {
        lumiyaUboUploadTimeNsTotal.addAndGet(uploadTimeNs.coerceAtLeast(0L))
        lumiyaUboUploadSamples.incrementAndGet()
        lumiyaFrameAllocations.addAndGet(frameAllocations.toLong().coerceAtLeast(0L))
    }

    fun glError(code: Int, location: String) {
        SessionLogRecorder.logRender(
            "Lumiya",
            "gl_error",
            "code=0x${code.toString(16)} at=$location"
        )
    }

    fun glShutdown(reason: String) {
        SessionLogRecorder.logRender("Lumiya", "shutdown", reason)
    }

    // ────────────────────────────────────────────────────────────────────
    // Generic
    // ────────────────────────────────────────────────────────────────────

    /**
     * One-shot snapshot: emits a summary line for any active subsystem.
     * Useful to call from places where we want a "where were we now"
     * marker (e.g. on connection state changes) without waiting for the
     * next heartbeat tick.
     */
    fun snapshot(reason: String) {
        synchronized(this) { activeSubsystems.toList() }.forEach { subsystem ->
            val total = frameCounters[subsystem]?.get() ?: 0L
            val lastFrame = lastFrameWallClock[subsystem]?.get() ?: 0L
            val sinceLastFrame = if (lastFrame == 0L) -1L else System.currentTimeMillis() - lastFrame
            val details = buildString {
                append("reason=$reason frames=$total")
                if (sinceLastFrame >= 0) append(" sinceLast=${sinceLastFrame}ms")
            }
            SessionLogRecorder.logRender(subsystem, "snapshot", details)
        }
    }

    /**
     * Active backend name for the HUD telemetry overlay. Returns the first
     * subsystem that has produced a frame within the stall threshold, or
     * `null` if no backend is currently rendering.
     */
    fun activeRenderSubsystem(): String? {
        val now = System.currentTimeMillis()
        return synchronized(this) {
            activeSubsystems.firstOrNull { subsystem ->
                val lastFrame = lastFrameWallClock[subsystem]?.get() ?: 0L
                lastFrame > 0L && (now - lastFrame) <= (STALL_THRESHOLD_MS * 2)
            } ?: activeSubsystems.firstOrNull()
        }
    }

    /**
     * Total frames rendered for [subsystem] since process start. Callers
     * (e.g. the HUD telemetry sampler) compute FPS by reading this twice
     * and dividing the delta by the elapsed time, which avoids coupling
     * the UI to the heartbeat cadence.
     */
    fun frameCount(subsystem: String): Long = frameCounters[subsystem]?.get() ?: 0L

    data class Percentiles(val p50Ms: Long?, val p90Ms: Long?, val p99Ms: Long?)
    data class DiagnosticsSnapshot(
        val activeBackend: String,
        val frameTimePercentiles: Map<String, Percentiles>,
        val swapChainCreateCount: Long,
        val swapChainDestroyCount: Long,
        val swapChainFailureCount: Long,
        val swapChainRestartCount: Long,
        val glContextCreateCount: Long,
        val glContextRestartCount: Long
    )

    @Synchronized
    fun diagnosticsSnapshot(): DiagnosticsSnapshot {
        val now = System.currentTimeMillis()
        val activeBackends = activeSubsystems.filter { subsystem ->
            val lastFrame = lastFrameWallClock[subsystem]?.get() ?: 0L
            lastFrame > 0L && (now - lastFrame) <= (STALL_THRESHOLD_MS * 2)
        }
        val activeBackend = when (activeBackends.size) {
            0 -> "none"
            1 -> activeBackends.first()
            else -> activeBackends.joinToString("+")
        }

        val percentiles = frameIntervalHistoryMs.mapValues { (_, values) ->
            val sorted = values.toList().sorted()
            Percentiles(
                p50Ms = percentile(sorted, 0.50),
                p90Ms = percentile(sorted, 0.90),
                p99Ms = percentile(sorted, 0.99)
            )
        }

        val swapCreates = filamentSwapChainCreateCount.get()
        val contextCreates = lumiyaContextCreateCount.get()
        return DiagnosticsSnapshot(
            activeBackend = activeBackend,
            frameTimePercentiles = percentiles,
            swapChainCreateCount = swapCreates,
            swapChainDestroyCount = filamentSwapChainDestroyCount.get(),
            swapChainFailureCount = filamentSwapChainFailureCount.get(),
            swapChainRestartCount = (swapCreates - 1L).coerceAtLeast(0L),
            glContextCreateCount = contextCreates,
            glContextRestartCount = (contextCreates - 1L).coerceAtLeast(0L)
        )
    }

    private fun percentile(sortedValues: List<Long>, q: Double): Long? {
        if (sortedValues.isEmpty()) return null
        val idx = ((sortedValues.size - 1) * q).toInt().coerceIn(0, sortedValues.lastIndex)
        return sortedValues[idx]
    }

    /**
     * Test hook: stop the heartbeat ticker. Production callers should
     * not need this — the ticker lives for the app's lifetime.
     */
    @Synchronized
    fun stopForTest() {
        heartbeatJob?.let {
            it.cancel()
            heartbeatJob = null
        }
        heartbeatStarted.set(false)
        activeSubsystems.clear()
        frameCounters.clear()
        lastFrameWallClock.clear()
        lastReportedFrameCount.clear()
        lumiyaUboUploadTimeNsTotal.set(0L)
        lumiyaUboUploadSamples.set(0L)
        lumiyaFrameAllocations.set(0L)
        frameIntervalHistoryMs.clear()
        filamentSwapChainCreateCount.set(0L)
        filamentSwapChainDestroyCount.set(0L)
        filamentSwapChainFailureCount.set(0L)
        lumiyaContextCreateCount.set(0L)
        Log.d(TAG, "Render diagnostics reset (test hook)")
    }
}
