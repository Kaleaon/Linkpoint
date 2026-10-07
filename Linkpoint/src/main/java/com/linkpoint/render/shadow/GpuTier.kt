package com.linkpoint.render.shadow

/**
 * Performance tiers for target Android GPU hardware.
 *
 * TIER_1: Flagship GPUs (e.g. Adreno 7xx/8xx, Mali-G7xx/G9xx, Xclipse, Apple, Tegra X1+).
 * TIER_2: Mid-range GPUs (e.g. Adreno 6xx, Mali-G5xx, PowerVR GM9xxx).
 * TIER_3: Entry-level / budget GPUs (e.g. Adreno 3xx/5xx/610, Mali-400/T8xx/G31/G51/G52, PowerVR GE8xxx).
 */
enum class GpuTier {
    TIER_1,
    TIER_2,
    TIER_3;

    /**
     * Get default [ShadowConfig] associated with this GPU tier.
     */
    val defaultConfig: ShadowConfig
        get() = when (this) {
            TIER_1 -> ShadowConfig(
                tier = TIER_1,
                mapSize = 1024,
                cascadeCount = 2,
                shadowDistance = 35.0f,
                enableDynamicShadowMaps = true,
                screenSpaceContactShadows = true,
                contactShadowStepCount = 16,
                contactShadowTimeBudgetMs = 0.5f,
                minScreenAreaPercent = 2.0f
            )
            TIER_2 -> ShadowConfig(
                tier = TIER_2,
                mapSize = 512,
                cascadeCount = 1,
                shadowDistance = 15.0f,
                enableDynamicShadowMaps = true,
                screenSpaceContactShadows = true,
                contactShadowStepCount = 8,
                contactShadowTimeBudgetMs = 0.5f,
                minScreenAreaPercent = 2.0f
            )
            TIER_3 -> ShadowConfig(
                tier = TIER_3,
                mapSize = 0,
                cascadeCount = 0,
                shadowDistance = 0.0f,
                enableDynamicShadowMaps = false,
                screenSpaceContactShadows = true,
                contactShadowStepCount = 4,
                contactShadowTimeBudgetMs = 0.5f,
                minScreenAreaPercent = 2.0f
            )
        }
}
