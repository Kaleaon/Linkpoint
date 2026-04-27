package com.linkpoint.render.lumiya.core

import android.content.Context
import android.view.Surface

/**
 * Abstraction over render engine backends.
 *
 * Linkpoint ships with two render engines:
 *   1. **Filament** – Google's PBR engine (default, high quality)
 *   2. **Lumiya** – OpenGL ES 3.2 engine ported from the classic Lumiya Viewer,
 *      modernised to current standards (lower overhead, mobile-optimised)
 *
 * Any class implementing this interface can be plugged in as the active renderer.
 */
interface RenderEngineProvider {

    /** Human-readable name for UI / diagnostics. */
    val engineName: String

    /** Whether [initialize] completed successfully. */
    val isInitialized: Boolean

    /** Current viewport dimensions (pixels). */
    val viewportWidth: Int
    val viewportHeight: Int

    /** Cumulative number of rendered frames. */
    val frameCount: Long

    // ── Lifecycle ────────────────────────────────────────────────────────

    /** One-time initialization.  Must be called on the render thread. */
    fun initialize(context: Context, surface: Surface, width: Int, height: Int): Boolean

    /** Notify the engine that the surface changed size. */
    fun onSurfaceChanged(width: Int, height: Int)

    /** Notify the engine that the surface was destroyed. */
    fun onSurfaceDestroyed()

    /** Render a single frame.  Must be called on the render thread. */
    fun renderFrame()

    /**
     * Block until outstanding GPU work completes for this backend.
     * Must be called on the backend render thread.
     */
    fun waitForGpuIdle(reason: String = "unspecified") {}

    /** Release all GPU resources.  Must be called on the render thread. */
    fun shutdown()

    // ── Camera ───────────────────────────────────────────────────────────

    fun setCameraPosition(x: Float, y: Float, z: Float)
    fun setCameraTarget(x: Float, y: Float, z: Float)
    fun setFieldOfView(fovDegrees: Float)
    fun setDrawDistance(distance: Float)

    // ── Scene manipulation ───────────────────────────────────────────────

    fun addObject(id: Long, posX: Float, posY: Float, posZ: Float)
    fun removeObject(id: Long)
    fun updateTerrain(heightmap: FloatArray, width: Int, depth: Int)
    fun clearScene()
}
