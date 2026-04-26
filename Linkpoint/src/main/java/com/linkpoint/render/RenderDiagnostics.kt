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

    /**
     * Last frame-rendered timestamp per subsystem ("Filament", "Lumiya").
     * Used to compute FPS / stall detection without coupling to the
     * subsystem's own counters.
     */
    private val lastFrameWallClock = mutableMapOf<String, AtomicLong>()
    private val frameCounters = mutableMapOf<String, AtomicLong>()
    private val lastReportedFrameCount = mutableMapOf<String, AtomicLong>()
    private val activeSubsystems = mutableSetOf<String>()

    private val heartbeatStarted = AtomicBoolean(false)
    private val heartbeatScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var heartbeatJob: Job? = null

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
        if (heartbeatStarted.compareAndSet(false, true)) {
            startHeartbeat()
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
        SessionLogRecorder.logRender(
            "Filament",
            "swapchain_created",
            if (width > 0 && height > 0) "${width}x${height}" else null
        )
        val dims = if (width > 0 && height > 0) " (${width}x${height})" else ""
        logLifecycle(NetworkLogger.Level.INFO, "🖼️ Filament SwapChain created$dims")
    }

    fun filamentSwapChainDestroyed(reason: String) {
        SessionLogRecorder.logRender("Filament", "swapchain_destroyed", reason)
        logLifecycle(NetworkLogger.Level.WARN, "🖼️ Filament SwapChain destroyed: $reason")
    }

    fun filamentSwapChainFailed(reason: String) {
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
        val counter = frameCounters["Filament"]
            ?: AtomicLong(0).also {
                trackSubsystem("Filament")
            }
        val total = (frameCounters["Filament"] ?: counter).incrementAndGet()
        lastFrameWallClock["Filament"]?.set(System.currentTimeMillis())
        if (total == 1L) {
            SessionLogRecorder.logRender("Filament", "first_frame", "frameCount=1")
        }
    }

    fun filamentShutdown(reason: String) {
        SessionLogRecorder.logRender("Filament", "shutdown", reason)
    }

    // ────────────────────────────────────────────────────────────────────
    // OpenGL / Lumiya events
    // ────────────────────────────────────────────────────────────────────

    fun glSurfaceCreated() {
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
        val counter = frameCounters["Lumiya"]
            ?: AtomicLong(0).also {
                trackSubsystem("Lumiya")
            }
        val total = (frameCounters["Lumiya"] ?: counter).incrementAndGet()
        lastFrameWallClock["Lumiya"]?.set(System.currentTimeMillis())
        if (total == 1L) {
            SessionLogRecorder.logRender("Lumiya", "first_frame", "frameCount=1")
        }
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
        Log.d(TAG, "Render diagnostics reset (test hook)")
    }
}
