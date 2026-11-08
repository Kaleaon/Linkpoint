package com.linkpoint.render.shadow

/**
 * Configuration options for Filament dynamic shadow pipeline and contact shadows.
 *
 * @property tier Hardware GPU tier this configuration corresponds to.
 * @property mapSize Resolution of directional shadow map (e.g. 1024, 512, or 0 for disabled).
 * @property cascadeCount Number of shadow cascades (2 for Tier 1, 1 for Tier 2, 0 for Tier 3).
 * @property shadowDistance Maximum rendering distance in meters for directional shadows (35m for Tier 1, 15m for Tier 2).
 * @property enableDynamicShadowMaps Whether dynamic shadow map rendering passes are enabled.
 * @property screenSpaceContactShadows Whether screen-space contact shadow pass is enabled.
 * @property contactShadowStepCount Raymarch step count for contact shadows (lower step count keeps pass within budget).
 * @property contactShadowTimeBudgetMs Maximum allowed execution budget in ms for contact shadow pass (fixed 0.5ms).
 * @property minScreenAreaPercent Minimum screen bounding area percentage required for an object to cast/receive shadows (2.0%).
 */
data class ShadowConfig(
    val tier: GpuTier,
    val mapSize: Int,
    val cascadeCount: Int,
    val shadowDistance: Float,
    val enableDynamicShadowMaps: Boolean,
    val screenSpaceContactShadows: Boolean,
    val contactShadowStepCount: Int,
    val contactShadowTimeBudgetMs: Float = 0.5f,
    val minScreenAreaPercent: Float = 2.0f
) {
    /**
     * Returns true if dynamic shadow map draw calls are issued.
     * Tier 3 devices use blob or contact shadows with zero dynamic shadow map draw calls.
     */
    val hasDynamicShadowMapDrawCalls: Boolean
        get() = enableDynamicShadowMaps && mapSize > 0 && cascadeCount > 0
}
