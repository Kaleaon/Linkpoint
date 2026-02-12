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
 *   switcher.registerEngine(EngineType.FILAMENT, filamentProvider)
 *   switcher.registerEngine(EngineType.LUMIYA, LumiyaRenderer())
 *   switcher.setActiveEngine(EngineType.LUMIYA)
 *   switcher.initialize(surface, width, height)
 *   // In render loop:
 *   switcher.renderFrame()
 * ```
 */
class RenderEngineSwitcher(private val context: Context) {

    companion object {
        private const val TAG = "RenderEngineSwitcher"
    }

    enum class EngineType {
        /** Google Filament PBR engine (default). */
        FILAMENT,
        /** Lumiya-based GL ES 3.2 engine. */
        LUMIYA
    }

    private val engines = mutableMapOf<EngineType, RenderEngineProvider>()
    private var activeType: EngineType = EngineType.FILAMENT
    private var active: RenderEngineProvider? = null
    private var pendingSwitch: EngineType? = null

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
        active?.onSurfaceChanged(width, height)
    }

    fun onSurfaceDestroyed() {
        active?.onSurfaceDestroyed()
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

        Log.i(TAG, "Performing engine switch: ${activeType.name} → ${newType.name}")

        // Shutdown old engine
        active?.shutdown()

        // Initialise new engine with current surface dimensions
        val width = active?.viewportWidth ?: 0
        val height = active?.viewportHeight ?: 0
        active = null
        activeType = newType

        if (width > 0 && height > 0) {
            // Re-initialisation will happen on next surface callback
            Log.i(TAG, "Engine switch complete; new engine needs surface re-init")
        }
    }
}
