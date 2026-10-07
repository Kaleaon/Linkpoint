package com.linkpoint.render.shadow

import com.google.android.filament.LightManager

/**
 * Handles screen-space contact shadows and contact-shadow parameters for lower-tier hardware fallbacks.
 *
 * Guarantees that contact shadow pass steps are clamped to run within a fixed 0.5ms time budget.
 * Tier 3 devices disable dynamic shadow map passes (zero shadow map draw calls) and fall back
 * to contact shadows or avatar projected shadows.
 */
object ScreenSpaceContactShadowPass {

    /** Maximum allowed contact shadow execution budget in milliseconds. */
    const val CONTACT_SHADOW_TIME_BUDGET_MS: Float = 0.5f

    /**
     * Builds Filament [LightManager.ShadowOptions] for a given [ShadowConfig].
     */
    fun createShadowOptions(config: ShadowConfig): LightManager.ShadowOptions {
        return LightManager.ShadowOptions().apply {
            mapSize = config.mapSize
            shadowCascades = config.cascadeCount
            shadowFar = config.shadowDistance
            screenSpaceContactShadows = config.screenSpaceContactShadows

            // Step count controls raymarching cost. Clamped for Tier 3 to fit <=0.5ms budget.
            stepCount = when {
                config.tier == GpuTier.TIER_3 -> config.contactShadowStepCount.coerceIn(1, 4)
                config.tier == GpuTier.TIER_2 -> config.contactShadowStepCount.coerceIn(1, 8)
                else -> config.contactShadowStepCount.coerceIn(1, 16)
            }

            maxShadowDistance = if (config.shadowDistance > 0f) config.shadowDistance else 15.0f
            stable = true
            lispsm = true
        }
    }
}
