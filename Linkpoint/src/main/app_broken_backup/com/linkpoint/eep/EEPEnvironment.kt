package com.linkpoint.eep

import java.util.UUID

/**
 * EEP (Enhanced Environment) System
 * Implements Second Life Enhanced Environment (2020+)
 * 
 * EEP provides custom per-parcel sky, water, and lighting settings,
 * replacing the old Windlight system.
 */

/**
 * EEP Environment Settings
 * Complete environment configuration
 */
data class EEPEnvironment(
    val environmentId: UUID,
    val name: String,
    val sky: EEPSkySettings,
    val water: EEPWaterSettings,
    val flags: Int = 0,
    val version: Int = 1
)

/**
 * EEP Sky Settings
 * Modern sky rendering configuration
 */
data class EEPSkySettings(
    // Sun/Moon
    val sunAzimuth: Float = 0f,          // Sun direction (0-360)
    val sunElevation: Float = 45f,       // Sun height (-90 to 90)
    val moonAzimuth: Float = 180f,       // Moon direction
    val moonElevation: Float = 45f,      // Moon height
    val sunlightColor: Color3 = Color3(1f, 1f, 1f),
    val moonlightColor: Color3 = Color3(0.7f, 0.7f, 0.8f),
    
    // Atmosphere
    val ambientColor: Color3 = Color3(0.25f, 0.25f, 0.25f),
    val blueDensity: Color3 = Color3(0.2447f, 0.4487f, 0.7599f),
    val blueHorizon: Color3 = Color3(0.4954f, 0.4954f, 0.6399f),
    val hazeDensity: Float = 0.7f,
    val hazeHorizon: Float = 0.19f,
    val densityMultiplier: Float = 0.0001f,
    val distanceMultiplier: Float = 0.8f,
    val maxY: Float = 1605f,
    
    // Clouds
    val cloudColor: Color3 = Color3(1f, 1f, 1f),
    val cloudPosDensity1: Color4 = Color4(1f, 0.526f, 1f, 0f),
    val cloudPosDensity2: Color4 = Color4(1f, 0.526f, 1f, 0f),
    val cloudScale: Float = 0.42f,
    val cloudScrollX: Float = 0.2f,
    val cloudScrollY: Float = 0.01f,
    val cloudShadow: Float = 0.27f,
    val cloudVariance: Float = 0f,
    
    // Dome
    val domeOffset: Float = 0.96f,
    val domeRadius: Float = 15000f,
    
    // Glow
    val glow: Color4 = Color4(5f, 0.001f, -0.48f, 1f),
    
    // Star brightness
    val starBrightness: Float = 0f,
    
    // Sun/Moon textures
    val sunTextureId: UUID = UUID(0, 0),
    val moonTextureId: UUID = UUID(0, 0),
    val cloudTextureId: UUID = UUID(0, 0),
    
    // Bloom
    val bloomTextureId: UUID = UUID(0, 0),
    
    // Rainbow/Halo
    val rainbowTextureId: UUID = UUID(0, 0),
    val haloTextureId: UUID = UUID(0, 0)
)

/**
 * EEP Water Settings
 * Modern water rendering configuration
 */
data class EEPWaterSettings(
    // Water color/transparency
    val waterFogColor: Color3 = Color3(0.0156f, 0.1490f, 0.2509f),
    val waterFogDensity: Float = 2f,
    
    // Underwater fog
    val underwaterFogMod: Float = 0.25f,
    
    // Fresnel
    val fresnelScale: Float = 0.4f,
    val fresnelOffset: Float = 0.5f,
    
    // Refraction
    val refractScaleAbove: Float = 0.03f,
    val refractScaleBelow: Float = 0.03f,
    
    // Blur
    val blurMultiplier: Float = 0.04f,
    
    // Waves
    val wave1Direction: Pair<Float, Float> = Pair(1.05f, -0.42f),
    val wave2Direction: Pair<Float, Float> = Pair(1.1f, -1.16f),
    
    // Normal maps
    val normalMapTextureId: UUID = UUID(0, 0),
    val normalScale: Triple<Float, Float, Float> = Triple(2f, 2f, 2f),
    
    // Transparency
    val transparentTextureId: UUID = UUID(0, 0)
)

/**
 * EEP Day Cycle
 * Time-based environment transitions
 */
data class EEPDayCycle(
    val dayCycleId: UUID,
    val name: String,
    val tracks: List<EEPTrack>,
    val dayLength: Float = 14400f // 4 hours default
)

/**
 * EEP Track
 * Keyframed environment changes
 */
data class EEPTrack(
    val trackType: TrackType,
    val keyframes: List<EEPKeyframe>
) {
    enum class TrackType {
        SKY,
        WATER
    }
}

/**
 * EEP Keyframe
 * Point in day cycle with environment settings
 */
data class EEPKeyframe(
    val time: Float,  // 0.0 to 1.0 (fraction of day)
    val environment: EEPEnvironment
)

/**
 * EEP Parcel Environment
 * Per-parcel environment override
 */
data class EEPParcelEnvironment(
    val parcelId: UUID,
    val environmentId: UUID,
    val dayCycleId: UUID?,
    val useRegionDefault: Boolean = false
)

/**
 * RGB Color
 */
data class Color3(
    val r: Float,
    val g: Float,
    val b: Float
) {
    fun toFloatArray() = floatArrayOf(r, g, b)
    
    companion object {
        fun lerp(a: Color3, b: Color3, t: Float): Color3 {
            return Color3(
                a.r + (b.r - a.r) * t,
                a.g + (b.g - a.g) * t,
                a.b + (b.b - a.b) * t
            )
        }
    }
}

/**
 * RGBA Color
 */
data class Color4(
    val r: Float,
    val g: Float,
    val b: Float,
    val a: Float
) {
    fun toFloatArray() = floatArrayOf(r, g, b, a)
    
    companion object {
        fun lerp(a: Color4, b: Color4, t: Float): Color4 {
            return Color4(
                a.r + (b.r - a.r) * t,
                a.g + (b.g - a.g) * t,
                a.b + (b.b - a.b) * t,
                a.a + (b.a - a.a) * t
            )
        }
    }
}