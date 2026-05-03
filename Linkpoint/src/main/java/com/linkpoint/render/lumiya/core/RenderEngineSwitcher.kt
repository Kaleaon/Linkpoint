package com.linkpoint.render.lumiya.core

import android.content.Context
import android.util.Log
import android.view.Surface

/**
 * Manages switching between render engine backends at runtime.
 *
 * Usage:
 * ```
 *   val switcher = RenderEngineSwitcher(context)
 *   switcher.registerEngine(EngineType.LUMIYA, LumiyaRenderer())
 *   switcher.registerEngine(EngineType.FILAMENT, filamentProvider) // optional
 *   switcher.setActiveEngine(EngineType.LUMIYA) // primary
 *   switcher.initialize(surface, width, height)
 *   // In render loop:
 *   switcher.renderFrame()
 * ```
 *
 * Lumiya is the primary engine; Filament is an opt-in fallback.
 */
class RenderEngineSwitcher(private val context: Context) {

    companion object {
        private const val TAG = "RenderEngineSwitcher"
    }

    enum class EngineType {
        /**
         * Lumiya-lineage OpenGL ES 3.0+ engine. Primary path. Hand-rolled
         * forward renderer modelled on Singularity Viewer's LLPipeline /
         * LLDrawPool architecture, ported through Lumiya Viewer's mobile
         * GLSurfaceView pipeline.
         */
        LUMIYA,
        /**
         * Google Filament PBR engine. Retained as an opt-in fallback for
         * devices that benefit from Filament's pre-baked materials, but no
         * longer the default — a series of crashes on real-world Adreno /
         * Mali drivers and incompatibility with the SL/OpenSim asset
         * pipeline pushed us back to a hand-rolled GL renderer.
         */
        FILAMENT
    }

    private val engines = mutableMapOf<EngineType, RenderEngineProvider>()
    private var activeType: EngineType = EngineType.LUMIYA
    private var active: RenderEngineProvider? = null
    private var pendingSwitch: EngineType? = null
    private var lastSurface: Surface? = null
    private var lastWidth: Int = 0
    private var lastHeight: Int = 0

    // ── Registration ─────────────────────────────────────────────────────

    fun registerEngine(type: EngineType, provider: RenderEngineProvider) {
        engines[type] = provider
        Log.i(TAG, "Registered engine: ${type.name} → ${provider.engineName}")
    }

    // ── Switching ────────────────────────────────────────────────────────

    /**
     * Request an engine switch.  The switch is deferred to the next frame
     * to avoid destroying resources mid-render.
     */
    fun setActiveEngine(type: EngineType) {
        if (type == activeType && active != null) return
        pendingSwitch = type
        Log.i(TAG, "Engine switch requested: ${activeType.name} → ${type.name}")
    }

    fun getActiveType(): EngineType = activeType

    fun getActiveEngine(): RenderEngineProvider? = active

    // ── Lifecycle ────────────────────────────────────────────────────────

    fun initialize(surface: Surface, width: Int, height: Int): Boolean {
        lastSurface = surface
        lastWidth = width
        lastHeight = height

        val engine = engines[activeType]
        if (engine == null) {
            Log.e(TAG, "No engine registered for ${activeType.name}")
            return false
        }
        val ok = engine.initialize(context, surface, width, height)
        if (ok) {
            active = engine
            Log.i(TAG, "Active engine initialised: ${engine.engineName}")
        }
        return ok
    }

    fun renderFrame() {
        // Handle pending switch
        pendingSwitch?.let { newType ->
            performSwitch(newType)
            pendingSwitch = null
        }
        active?.renderFrame()
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        lastWidth = width
        lastHeight = height
        active?.onSurfaceChanged(width, height)
    }

    fun onSurfaceDestroyed() {
        active?.onSurfaceDestroyed()
        lastSurface = null
    }

    fun shutdown() {
        active?.shutdown()
        active = null
        engines.values.forEach { if (it.isInitialized) it.shutdown() }
        engines.clear()
    }

    // ── Diagnostics ──────────────────────────────────────────────────────

    fun getDiagnostics(): Map<String, String> {
        val diag = mutableMapOf<String, String>()
        diag["activeEngine"] = activeType.name
        active?.let {
            diag["engineName"] = it.engineName
            diag["initialized"] = it.isInitialized.toString()
            diag["viewport"] = "${it.viewportWidth}x${it.viewportHeight}"
            diag["frameCount"] = it.frameCount.toString()
        }
        diag["registeredEngines"] = engines.keys.joinToString(", ") { it.name }
        return diag
    }

    // ── Internal ─────────────────────────────────────────────────────────

    private fun performSwitch(newType: EngineType) {
        val newEngine = engines[newType]
        if (newEngine == null) {
            Log.e(TAG, "Cannot switch to ${newType.name}: not registered")
            return
        }

        val previousType = activeType
        Log.i(TAG, "Performing engine switch: ${activeType.name} → ${newType.name}")

        // Drain backend GPU queues before tearing down contexts/surfaces.
        active?.waitForGpuIdle("switch ${activeType.name} -> ${newType.name}")

        // Shutdown old engine
        active?.shutdown()
        active = null
        activeType = newType

        val surface = lastSurface
        val canInitialize = surface != null && lastWidth > 0 && lastHeight > 0
        if (!canInitialize) {
            Log.i(TAG, "Engine switch complete; awaiting valid surface lifecycle callbacks")
            return
        }

        val initialized = try {
            newEngine.initialize(context, surface, lastWidth, lastHeight)
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to initialize ${newType.name} after switch", t)
            false
        }

        if (initialized) {
            active = newEngine
            Log.i(TAG, "Engine switch complete; active=${newEngine.engineName}")
        } else {
            activeType = previousType
            Log.e(TAG, "Engine switch failed; reverted active type to ${previousType.name}")
        }
    }
}
