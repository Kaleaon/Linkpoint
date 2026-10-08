package com.linkpoint.render.shadow

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Service that monitors frame render times and dynamically scales Filament shadow options.
 *
 * Requirements:
 * - Dynamic auto-scaling adjusts shadow settings within 3 frames when GPU render times exceed target frame budget.
 * - Dynamic transitions run asynchronously between frame renders to avoid main thread jank or pipeline rebuilds.
 * - Application settings allow manual user tier overrides (Auto / Tier 1 / Tier 2 / Tier 3).
 */
class AdaptiveShadowController(
    val bootTier: GpuTier = GpuTier.TIER_2,
    targetFps: Int = 30,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    companion object {
        private const val TAG = "AdaptiveShadowController"
        const val SPIKE_THRESHOLD_FRAMES: Int = 3
        const val RECOVERY_THRESHOLD_FRAMES: Int = 60
    }

    /** Target frame budget in milliseconds (e.g. ~33.33ms for 30 FPS target). */
    val targetFrameBudgetMs: Float = 1000.0f / targetFps.coerceIn(15, 120).toFloat()

    /** User manual tier override from app settings (null = AUTO mode). */
    @Volatile
    var userTierOverride: GpuTier? = null
        set(value) {
            field = value
            applyEffectiveTier("user_override_changed")
        }

    /** Active GPU tier currently driving rendering. */
    private var activeTier: GpuTier = bootTier

    private val _shadowConfigFlow = MutableStateFlow(activeTier.defaultConfig)
    val shadowConfigFlow: StateFlow<ShadowConfig> = _shadowConfigFlow

    /** Current shadow configuration. */
    val currentConfig: ShadowConfig
        get() = _shadowConfigFlow.value

    /** Listener callback for asynchronous shadow options updates on the render thread. */
    @Volatile
    var onConfigChanged: ((ShadowConfig) -> Unit)? = null

    // Frame timing tracking counters
    private var consecutiveSpikeCount: Int = 0
    private var consecutiveSmoothCount: Int = 0
    private val transitionPending = AtomicBoolean(false)

    init {
        applyEffectiveTier("init")
    }

    /**
     * Record a completed frame's render time in nanoseconds.
     * Evaluates frame budget and triggers dynamic downscaling within 3 frame cycles when budget is exceeded.
     */
    fun recordFrameTime(renderTimeNanos: Long) {
        if (userTierOverride != null) {
            // Manual tier override active — skip dynamic auto-scaling
            return
        }

        val renderTimeMs = renderTimeNanos / 1_000_000.0f

        if (renderTimeMs > targetFrameBudgetMs) {
            consecutiveSpikeCount++
            consecutiveSmoothCount = 0

            if (consecutiveSpikeCount >= SPIKE_THRESHOLD_FRAMES) {
                consecutiveSpikeCount = 0
                downscaleShadowTierAsync("frame_time_spike_${renderTimeMs.toInt()}ms_exceeded_budget_${targetFrameBudgetMs.toInt()}ms")
            }
        } else {
            consecutiveSmoothCount++
            consecutiveSpikeCount = 0

            if (consecutiveSmoothCount >= RECOVERY_THRESHOLD_FRAMES) {
                consecutiveSmoothCount = 0
                recoverShadowTierAsync("sustained_performance_${targetFrameBudgetMs.toInt()}ms_budget")
            }
        }
    }

    /**
     * Triggers asynchronous downscaling of shadow options to a lower GPU tier.
     */
    private fun downscaleShadowTierAsync(reason: String) {
        val nextTier = when (activeTier) {
            GpuTier.TIER_1 -> GpuTier.TIER_2
            GpuTier.TIER_2 -> GpuTier.TIER_3
            GpuTier.TIER_3 -> GpuTier.TIER_3 // Floor
        }

        if (nextTier == activeTier) return
        updateTierAsync(nextTier, reason)
    }

    /**
     * Triggers asynchronous recovery of shadow options to a higher GPU tier.
     */
    private fun recoverShadowTierAsync(reason: String) {
        val nextTier = when (activeTier) {
            GpuTier.TIER_3 -> GpuTier.TIER_2
            GpuTier.TIER_2 -> if (bootTier == GpuTier.TIER_1) GpuTier.TIER_1 else GpuTier.TIER_2
            GpuTier.TIER_1 -> GpuTier.TIER_1 // Ceiling
        }

        if (nextTier == activeTier) return
        updateTierAsync(nextTier, reason)
    }

    private fun applyEffectiveTier(source: String) {
        val effectiveTier = userTierOverride ?: bootTier
        updateTierAsync(effectiveTier, source)
    }

    private fun updateTierAsync(newTier: GpuTier, reason: String) {
        if (newTier == activeTier && _shadowConfigFlow.value.tier == newTier) {
            return
        }

        if (!transitionPending.compareAndSet(false, true)) {
            return
        }

        scope.launch {
            try {
                activeTier = newTier
                val newConfig = newTier.defaultConfig
                _shadowConfigFlow.value = newConfig
                Log.i(TAG, "Dynamic shadow transition -> Tier: $newTier (Reason: $reason) mapSize=${newConfig.mapSize} cascades=${newConfig.cascadeCount} distance=${newConfig.shadowDistance}m")
                onConfigChanged?.invoke(newConfig)
            } finally {
                transitionPending.set(false)
            }
        }
    }

    /**
     * Gets diagnostic state summary.
     */
    fun getDiagnostics(): String {
        val cfg = currentConfig
        return "AdaptiveShadowController(bootTier=$bootTier, activeTier=$activeTier, override=$userTierOverride, mapSize=${cfg.mapSize}, cascades=${cfg.cascadeCount}, contactShadows=${cfg.screenSpaceContactShadows}, targetMs=${targetFrameBudgetMs}ms)"
    }
}
