package com.linkpoint.ui.overlay

import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * Decoupled 2D UI Overlay Composition Layer.
 *
 * Manages the lifecycle and rendering behavior of 2D UI overlays over the 3D surface view.
 * When full-screen 2D overlays (such as chat or inventory) obscure the viewport,
 * the 3D render thread is suspended via `onPause()` to achieve 0 FPS 3D rendering and
 * eliminate GPU power overhead.
 *
 * OpenGL ES shaders and hardware textures are preserved in memory via `preserveEGLContextOnPause = true`.
 * When full-screen overlays are closed, `onResume()` restores 3D rendering within 50 ms.
 * Semi-transparent HUD overlays fall back to dirty-flag rendering (`RENDERMODE_WHEN_DIRTY`)
 * instead of thread pausing.
 */
class OverlayManager {

    enum class OverlayType {
        /**
         * Full-screen 2D UI layout (e.g. Chat, Inventory, Settings).
         * Obscures the 3D viewport completely and pauses the 3D render loop.
         */
        FULL_SCREEN_2D,

        /**
         * Semi-transparent HUD layout (e.g. HUD attachments, joysticks, status bars).
         * Keeps 3D surface active in dirty-flag rendering mode (RENDERMODE_WHEN_DIRTY).
         */
        SEMI_TRANSPARENT_HUD
    }

    data class OverlayInfo(
        val id: String,
        val type: OverlayType,
        val title: String = "",
        val timestamp: Long = System.currentTimeMillis()
    )

    data class TelemetrySnapshot(
        val activeOverlayCount: Int,
        val fullScreenOverlayCount: Int,
        val hudOverlayCount: Int,
        val is3dPaused: Boolean,
        val current3dFps: Float,
        val isEglContextPreserved: Boolean,
        val lastResumeDurationMs: Long,
        val isDirtyFlagMode: Boolean
    )

    interface OverlaySurfaceTarget {
        fun pause3dRendering()
        fun resume3dRendering()
        fun setDirtyFlagRenderMode(enabled: Boolean)
        fun requestRenderFrame()
        fun isEglContextPreserved(): Boolean
        fun getCurrent3dFps(): Float
    }

    companion object {
        private const val TAG = "OverlayManager"
        const val MAX_RESUME_LATENCY_GOAL_MS = 50L

        @Volatile
        private var instance: OverlayManager? = null

        fun getInstance(): OverlayManager {
            return instance ?: synchronized(this) {
                instance ?: OverlayManager().also { instance = it }
            }
        }
    }

    private val activeOverlays = ConcurrentHashMap<String, OverlayInfo>()
    private var surfaceTarget: OverlaySurfaceTarget? = null

    @Volatile
    private var is3dPaused: Boolean = false

    @Volatile
    private var isDirtyFlagMode: Boolean = false

    @Volatile
    private var lastResumeDurationMs: Long = 0L

    /**
     * Binds an [OverlaySurfaceTarget] (such as `LumiyaGLSurfaceView` or a delegate)
     * to receive render thread pause/resume and mode change commands.
     */
    fun bindSurfaceTarget(target: OverlaySurfaceTarget?) {
        this.surfaceTarget = target
        Log.i(TAG, "Bound surface target: ${target?.javaClass?.simpleName}")
        evaluateOverlayState()
    }

    /**
     * Registers and shows a 2D UI overlay.
     */
    fun showOverlay(id: String, type: OverlayType, title: String = "") {
        val info = OverlayInfo(id, type, title)
        activeOverlays[id] = info
        Log.i(TAG, "Overlay shown: id=$id, type=$type, title=$title")
        evaluateOverlayState()
    }

    /**
     * Unregisters and closes a 2D UI overlay.
     */
    fun hideOverlay(id: String) {
        val removed = activeOverlays.remove(id)
        if (removed != null) {
            Log.i(TAG, "Overlay hidden: id=$id, type=${removed.type}")
            evaluateOverlayState()
        }
    }

    /**
     * Clears all registered overlays.
     */
    fun clearAllOverlays() {
        if (activeOverlays.isNotEmpty()) {
            activeOverlays.clear()
            Log.i(TAG, "All overlays cleared")
            evaluateOverlayState()
        }
    }

    fun isOverlayActive(id: String): Boolean = activeOverlays.containsKey(id)

    fun isFullScreenOverlayActive(): Boolean {
        return activeOverlays.values.any { it.type == OverlayType.FULL_SCREEN_2D }
    }

    fun isHudOverlayActive(): Boolean {
        return activeOverlays.values.any { it.type == OverlayType.SEMI_TRANSPARENT_HUD }
    }

    /**
     * Requests a dirty-flag render frame when a semi-transparent HUD overlay updates.
     */
    fun requestHudDirtyRender() {
        if (isDirtyFlagMode && !is3dPaused) {
            surfaceTarget?.requestRenderFrame()
        }
    }

    /**
     * Evaluates current active overlays and updates the 3D surface view state.
     */
    private fun evaluateOverlayState() {
        synchronized(this) {
            val target = surfaceTarget ?: return
            val hasFullScreen = isFullScreenOverlayActive()
            val hasHud = isHudOverlayActive()

            if (hasFullScreen) {
                // Full-screen 2D UI active -> pause 3D render thread (0 FPS, zero GPU power overhead)
                if (!is3dPaused) {
                    Log.i(TAG, "Full-screen overlay active. Pausing 3D render thread.")
                    target.pause3dRendering()
                    is3dPaused = true
                    isDirtyFlagMode = false
                }
            } else {
                // No full-screen 2D UI active -> resume 3D render thread if paused
                if (is3dPaused) {
                    Log.i(TAG, "Full-screen overlays closed. Resuming 3D render thread.")
                    val resumeTimeMs = measureTimeMillis {
                        target.resume3dRendering()
                    }
                    lastResumeDurationMs = resumeTimeMs
                    is3dPaused = false
                    Log.i(TAG, "3D render thread resumed in ${resumeTimeMs}ms (target: <${MAX_RESUME_LATENCY_GOAL_MS}ms)")
                }

                // Semi-transparent HUD handling: fall back to dirty-flag rendering if HUD is active
                if (hasHud) {
                    if (!isDirtyFlagMode) {
                        Log.i(TAG, "HUD overlay active. Switching to dirty-flag render mode.")
                        target.setDirtyFlagRenderMode(true)
                        isDirtyFlagMode = true
                    }
                } else {
                    if (isDirtyFlagMode) {
                        Log.i(TAG, "HUD overlay cleared. Reverting to continuous render mode.")
                        target.setDirtyFlagRenderMode(false)
                        isDirtyFlagMode = false
                    }
                }
            }
        }
    }

    /**
     * Returns a snapshot of telemetry data for monitoring power and rendering performance.
     */
    fun getTelemetry(): TelemetrySnapshot {
        val target = surfaceTarget
        val fps = if (is3dPaused) 0.0f else (target?.getCurrent3dFps() ?: 0.0f)
        val contextPreserved = target?.isEglContextPreserved() ?: true

        return TelemetrySnapshot(
            activeOverlayCount = activeOverlays.size,
            fullScreenOverlayCount = activeOverlays.values.count { it.type == OverlayType.FULL_SCREEN_2D },
            hudOverlayCount = activeOverlays.values.count { it.type == OverlayType.SEMI_TRANSPARENT_HUD },
            is3dPaused = is3dPaused,
            current3dFps = fps,
            isEglContextPreserved = contextPreserved,
            lastResumeDurationMs = lastResumeDurationMs,
            isDirtyFlagMode = isDirtyFlagMode
        )
    }
}
