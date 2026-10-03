package com.linkpoint.render

import android.opengl.GLSurfaceView
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

/**
 * Manages adaptive frame rate throttling and dirty-flag rendering state.
 *
 * Requirements:
 * 1. Active navigation mode at 60 FPS (~16ms frame target) when camera or avatar moves.
 * 2. Inactivity detection: switch frame rate from 60 FPS to 15 FPS (~66ms frame target)
 *    after two seconds (2000 ms) of camera/avatar inactivity.
 * 3. Dirty-flag rendering mode (`RENDERMODE_WHEN_DIRTY`) when a full-screen 2D overlay
 *    (e.g., full-screen chat) covers the 3D viewport, lowering GPU power draw below 0.3W
 *    while preserving OpenGL ES graphics context and surface textures.
 * 4. Surface invalidation: triggers `requestRender()` calls only when new 3D world updates
 *    or user inputs occur in dirty-flag mode.
 */
class RenderStateManager {

    enum class RenderMode(
        val targetFps: Int,
        val targetFrameIntervalMs: Long,
        val estimatedGpuPowerW: Float
    ) {
        /** Uncapped active 60 FPS rendering for navigation and camera motion (~1.5W power draw). */
        ACTIVE_NAVIGATION_60FPS(60, 16L, 1.5f),

        /** Throttled 15 FPS rendering for stationary in-world sessions (~0.4W power draw). */
        STATIONARY_IDLE_15FPS(15, 66L, 0.4f),

        /** Dirty-flag event-driven rendering when full-screen 2D overlay covers 3D viewport (<0.3W power draw). */
        DIRTY_FLAG_OVERLAY(0, 0L, 0.2f)
    }

    interface RenderStateListener {
        fun onRenderModeChanged(previousMode: RenderMode, newMode: RenderMode)
        fun onSurfaceInvalidated(reason: String)
    }

    companion object {
        private const val TAG = "RenderStateManager"

        /** Timeout before stationary camera state triggers 15 FPS throttling (2 seconds). */
        const val INACTIVITY_TIMEOUT_MS = 2000L

        /** Target frame interval for 60 FPS mode (~16.6ms). */
        const val TARGET_INTERVAL_60FPS_MS = 16L

        /** Target frame interval for 15 FPS mode (~66.6ms). */
        const val TARGET_INTERVAL_15FPS_MS = 66L
    }

    private val currentModeRef = AtomicReference(RenderMode.ACTIVE_NAVIGATION_60FPS)
    private val isOverlayActiveRef = AtomicBoolean(false)
    private val lastActivityTimeMsRef = AtomicLong(System.currentTimeMillis())
    private val listeners = CopyOnWriteArrayList<RenderStateListener>()

    @Volatile
    private var attachedGlSurfaceView: GLSurfaceView? = null

    /** Current render mode. */
    val currentMode: RenderMode
        get() = currentModeRef.get()

    /** Is a full-screen 2D overlay covering the 3D viewport. */
    val isOverlayActive: Boolean
        get() = isOverlayActiveRef.get()

    /** Timestamp of the last detected camera/avatar motion or user input. */
    val lastActivityTimeMs: Long
        get() = lastActivityTimeMsRef.get()

    /**
     * Attach a [GLSurfaceView] to automatically update its [GLSurfaceView.setRenderMode]
     * and trigger [GLSurfaceView.requestRender] on invalidations.
     */
    fun attachGlSurfaceView(view: GLSurfaceView?) {
        attachedGlSurfaceView = view
        applyRenderModeToSurfaceView(currentModeRef.get())
    }

    fun addListener(listener: RenderStateListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: RenderStateListener) {
        listeners.remove(listener)
    }

    /**
     * Signal that camera or avatar motion, gesture touch, or movement controls touch occurred.
     * Triggers an instant transition from 15 FPS back to 60 FPS within 1 frame cycle (<16ms).
     */
    fun notifyCameraOrAvatarMotion(nowMs: Long = System.currentTimeMillis()) {
        lastActivityTimeMsRef.set(nowMs)

        if (isOverlayActiveRef.get()) {
            // Still in overlay mode, but trigger invalidation if world state updated
            invalidateSurface("motion_during_overlay")
            return
        }

        val previous = currentModeRef.get()
        if (previous != RenderMode.ACTIVE_NAVIGATION_60FPS) {
            if (currentModeRef.compareAndSet(previous, RenderMode.ACTIVE_NAVIGATION_60FPS)) {
                Log.i(TAG, "Instant transition: $previous -> ACTIVE_NAVIGATION_60FPS (camera/avatar motion)")
                RenderDiagnostics.filamentDrawingResumed("motion_detected")
                applyRenderModeToSurfaceView(RenderMode.ACTIVE_NAVIGATION_60FPS)
                notifyListenersModeChanged(previous, RenderMode.ACTIVE_NAVIGATION_60FPS)
            }
        }
    }

    /**
     * Set whether a full-screen 2D overlay (e.g. chat overlay) covers the 3D viewport.
     * When true, configures dirty-flag rendering (`RENDERMODE_WHEN_DIRTY`) to pause continuous 3D rendering.
     * When false, restores continuous rendering at 60 FPS.
     */
    fun setFullScreenOverlayActive(active: Boolean, nowMs: Long = System.currentTimeMillis()) {
        isOverlayActiveRef.set(active)
        lastActivityTimeMsRef.set(nowMs)

        val previous = currentModeRef.get()
        val targetMode = if (active) RenderMode.DIRTY_FLAG_OVERLAY else RenderMode.ACTIVE_NAVIGATION_60FPS

        if (previous != targetMode) {
            if (currentModeRef.compareAndSet(previous, targetMode)) {
                Log.i(TAG, "Overlay state change ($active): $previous -> $targetMode")
                if (active) {
                    RenderDiagnostics.filamentDrawingPaused("fullscreen_chat_overlay")
                } else {
                    RenderDiagnostics.filamentDrawingResumed("overlay_dismissed")
                }
                applyRenderModeToSurfaceView(targetMode)
                notifyListenersModeChanged(previous, targetMode)
            }
        }
    }

    /**
     * Evaluate camera/avatar inactivity. If inactive for >= 2 seconds (2000 ms),
     * transitions frame rate from 60 FPS to 15 FPS.
     */
    fun evaluateInactivity(nowMs: Long = System.currentTimeMillis()): RenderMode {
        if (isOverlayActiveRef.get()) {
            return RenderMode.DIRTY_FLAG_OVERLAY
        }

        val previous = currentModeRef.get()
        if (previous == RenderMode.ACTIVE_NAVIGATION_60FPS) {
            val idleDuration = nowMs - lastActivityTimeMsRef.get()
            if (idleDuration >= INACTIVITY_TIMEOUT_MS) {
                if (currentModeRef.compareAndSet(RenderMode.ACTIVE_NAVIGATION_60FPS, RenderMode.STATIONARY_IDLE_15FPS)) {
                    Log.i(TAG, "Inactivity timeout (${idleDuration}ms): ACTIVE_NAVIGATION_60FPS -> STATIONARY_IDLE_15FPS")
                    applyRenderModeToSurfaceView(RenderMode.STATIONARY_IDLE_15FPS)
                    notifyListenersModeChanged(previous, RenderMode.STATIONARY_IDLE_15FPS)
                    return RenderMode.STATIONARY_IDLE_15FPS
                }
            }
        }
        return currentModeRef.get()
    }

    /**
     * Calculate required frame delay in milliseconds for frame pacing based on current render mode.
     */
    fun calculateFrameDelayMs(elapsedMs: Long, nowMs: Long = System.currentTimeMillis()): Long {
        val mode = evaluateInactivity(nowMs)
        val targetInterval = mode.targetFrameIntervalMs
        if (targetInterval <= 0L) return 0L
        return (targetInterval - elapsedMs).coerceAtLeast(0L)
    }

    /**
     * Trigger surface invalidation (`requestRender()`) when new 3D world updates or user inputs occur.
     */
    fun invalidateSurface(reason: String = "world_update") {
        attachedGlSurfaceView?.requestRender()
        for (listener in listeners) {
            try {
                listener.onSurfaceInvalidated(reason)
            } catch (e: Exception) {
                Log.w(TAG, "Listener error on onSurfaceInvalidated: ${e.message}")
            }
        }
    }

    private fun applyRenderModeToSurfaceView(mode: RenderMode) {
        val sv = attachedGlSurfaceView ?: return
        try {
            when (mode) {
                RenderMode.DIRTY_FLAG_OVERLAY -> {
                    sv.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
                }
                RenderMode.ACTIVE_NAVIGATION_60FPS,
                RenderMode.STATIONARY_IDLE_15FPS -> {
                    sv.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error setting surface renderMode: ${e.message}")
        }
    }

    private fun notifyListenersModeChanged(previous: RenderMode, newMode: RenderMode) {
        for (listener in listeners) {
            try {
                listener.onRenderModeChanged(previous, newMode)
            } catch (e: Exception) {
                Log.w(TAG, "Listener error on onRenderModeChanged: ${e.message}")
            }
        }
    }
}
