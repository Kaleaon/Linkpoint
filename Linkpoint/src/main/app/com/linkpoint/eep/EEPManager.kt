package com.linkpoint.eep

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

data class EEPColor(val red: Float, val green: Float, val blue: Float) {
    init {
        require(red in 0f..1f && green in 0f..1f && blue in 0f..1f) {
            "RGB components must be within 0..1"
        }
    }

    fun lerp(other: EEPColor, alpha: Float): EEPColor {
        val clampedAlpha = alpha.coerceIn(0f, 1f)
        return EEPColor(
            red = red + ((other.red - red) * clampedAlpha),
            green = green + ((other.green - green) * clampedAlpha),
            blue = blue + ((other.blue - blue) * clampedAlpha)
        )
    }
}

data class EEPEnvironment(
    val name: String,
    val dayCyclePosition: Float,
    val skyColor: EEPColor,
    val waterColor: EEPColor,
    val sunIntensity: Float,
    val ambientLight: Float,
    val hazeDensity: Float,
    val cloudCoverage: Float,
    val gamma: Float
) {
    fun clamp(): EEPEnvironment = copy(
        dayCyclePosition = dayCyclePosition.coerceIn(0f, 1f),
        sunIntensity = sunIntensity.coerceIn(0f, 8f),
        ambientLight = ambientLight.coerceIn(0f, 2f),
        hazeDensity = hazeDensity.coerceIn(0f, 1f),
        cloudCoverage = cloudCoverage.coerceIn(0f, 1f),
        gamma = gamma.coerceIn(1.6f, 3.0f)
    )
}

/**
 * Environment Enhancement Project (EEP) runtime manager. Provides enough
 * functionality for rendering and protocol integration while remaining simple
 * enough to run inside unit tests.
 */
class EEPManager {

    private val currentEnvironment = AtomicReference<EEPEnvironment>()
    private val regionOverrides = ConcurrentHashMap<UUID, EEPEnvironment>()

    fun createDefaultEnvironment(): EEPEnvironment = EEPEnvironment(
        name = "Default",
        dayCyclePosition = 0.5f,
        skyColor = EEPColor(0.32f, 0.55f, 0.87f),
        waterColor = EEPColor(0.02f, 0.22f, 0.33f),
        sunIntensity = 1.0f,
        ambientLight = 0.35f,
        hazeDensity = 0.12f,
        cloudCoverage = 0.25f,
        gamma = 2.2f
    )

    fun setRegionEnvironment(regionId: UUID, environment: EEPEnvironment) {
        val clamped = environment.clamp()
        regionOverrides[regionId] = clamped
        currentEnvironment.set(clamped)
    }

    fun getRegionEnvironment(regionId: UUID): EEPEnvironment? =
        regionOverrides[regionId]

    fun getCurrentEnvironment(): EEPEnvironment? = currentEnvironment.get()

    fun resetToDefault() {
        val default = createDefaultEnvironment()
        currentEnvironment.set(default)
        regionOverrides.clear()
    }

    fun blendRegions(
        fromRegion: UUID,
        toRegion: UUID,
        progress: Float
    ): EEPEnvironment? {
        val from = regionOverrides[fromRegion]
        val to = regionOverrides[toRegion]
        if (from == null || to == null) return currentEnvironment.get()

        val alpha = progress.coerceIn(0f, 1f)
        return EEPEnvironment(
            name = "${from.name}→${to.name}",
            dayCyclePosition = from.dayCyclePosition + ((to.dayCyclePosition - from.dayCyclePosition) * alpha),
            skyColor = from.skyColor.lerp(to.skyColor, alpha),
            waterColor = from.waterColor.lerp(to.waterColor, alpha),
            sunIntensity = lerp(from.sunIntensity, to.sunIntensity, alpha),
            ambientLight = lerp(from.ambientLight, to.ambientLight, alpha),
            hazeDensity = lerp(from.hazeDensity, to.hazeDensity, alpha),
            cloudCoverage = lerp(from.cloudCoverage, to.cloudCoverage, alpha),
            gamma = lerp(from.gamma, to.gamma, alpha)
        ).also {
            currentEnvironment.set(it)
        }
    }

    fun cleanup() {
        regionOverrides.clear()
        currentEnvironment.set(null)
    }

    private fun lerp(start: Float, end: Float, alpha: Float): Float =
        start + ((end - start) * alpha)
}
