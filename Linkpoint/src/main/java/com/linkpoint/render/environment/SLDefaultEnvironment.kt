package com.linkpoint.render.environment

import android.util.Log
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLVector3
import java.util.UUID

/**
 * Second Life Default Environment Settings
 * 
 * Provides standard SL defaults for sky, terrain, water, and lighting
 * when no specific Windlight or region environment settings are available.
 * This ensures the renderer shows a recognizable SL-like world instead of
 * a black screen.
 * 
 * Based on default values from:
 * - Linden Lab's default "Midday" windlight preset
 * - Standard SL terrain texture UUIDs
 * - Default water/sea level settings
 * 
 * These defaults match what a new SL user would see on a fresh install.
 */
object SLDefaultEnvironment {
    
    private const val TAG = "SLDefaultEnv"
    
    // ==================================================================================
    // DEFAULT SKY SETTINGS (SL "Midday" equivalent)
    // ==================================================================================
    
    /**
     * Default sun direction for midday (sun overhead, slightly to the west).
     * In SL coordinate system: X=East, Y=North, Z=Up
     */
    val DEFAULT_SUN_DIRECTION = LLVector3(-0.4f, -0.9f, -0.3f).normalize()
    
    /**
     * Default moon direction (opposite sun for default).
     */
    val DEFAULT_MOON_DIRECTION = LLVector3(0.4f, 0.9f, 0.3f).normalize()
    
    /**
     * Default sun color - warm white/yellow.
     */
    val DEFAULT_SUN_COLOR = LLColor4(1.0f, 0.98f, 0.95f, 1.0f)
    
    /**
     * Default ambient color - slight blue tint for sky reflection.
     */
    val DEFAULT_AMBIENT_COLOR = LLColor4(0.35f, 0.35f, 0.4f, 1.0f)
    
    /**
     * Blue horizon color for atmospheric scattering.
     */
    val DEFAULT_BLUE_HORIZON = LLColor4(0.4f, 0.5f, 0.7f, 1.0f)
    
    /**
     * Blue density for sky gradient.
     */
    val DEFAULT_BLUE_DENSITY = LLColor4(0.2f, 0.3f, 0.5f, 1.0f)
    
    /**
     * Default haze settings.
     */
    const val DEFAULT_HAZE_HORIZON = 0.19f
    const val DEFAULT_HAZE_DENSITY = 0.7f
    
    /**
     * Default sun intensity (Filament units).
     */
    const val DEFAULT_SUN_INTENSITY = 110_000f
    
    /**
     * Default ambient intensity (Filament units).
     */
    const val DEFAULT_AMBIENT_INTENSITY = 30_000f
    
    /**
     * Default cloud settings.
     */
    val DEFAULT_CLOUD_COLOR = LLColor4(1.0f, 1.0f, 1.0f, 1.0f)
    const val DEFAULT_CLOUD_COVERAGE = 0.27f
    const val DEFAULT_CLOUD_SCALE = 0.42f
    
    // ==================================================================================
    // DEFAULT TERRAIN TEXTURE UUIDs
    // ==================================================================================
    
    /**
     * Standard SL terrain textures (the classic green grass, dirt, rock, snow).
     * These are the default texture UUIDs used by Linden Lab for new regions.
     */
    object TerrainTextures {
        /** Grass texture (green, used at low elevations) */
        val TERRAIN_GRASS = UUID.fromString("b8d3965a-ad78-bf43-699b-bff8eca6c975")
        
        /** Dirt/mud texture (brown, used at low-mid elevations) */
        val TERRAIN_DIRT = UUID.fromString("abb783e6-3e93-26c0-248a-247666855da3")
        
        /** Rock texture (gray, used at mid-high elevations) */
        val TERRAIN_ROCK = UUID.fromString("179cdabd-398a-9b6b-1391-4dc333ba321f")
        
        /** Snow/mountain texture (white, used at high elevations) */
        val TERRAIN_SNOW = UUID.fromString("beb169c7-11ea-fff2-efe5-0f24dc881df2")
        
        /**
         * Get default terrain textures as a list (grass, dirt, rock, snow).
         */
        fun getDefaultTextures(): List<UUID> = listOf(
            TERRAIN_GRASS,
            TERRAIN_DIRT,
            TERRAIN_ROCK,
            TERRAIN_SNOW
        )
        
        /**
         * Get default terrain texture for a given index (0-3).
         */
        fun getTexture(index: Int): UUID = when (index) {
            0 -> TERRAIN_GRASS
            1 -> TERRAIN_DIRT
            2 -> TERRAIN_ROCK
            3 -> TERRAIN_SNOW
            else -> TERRAIN_GRASS
        }
    }
    
    /**
     * Default terrain elevation ranges (in meters) for texture blending.
     * These define at what heights each texture is primarily visible.
     */
    object TerrainElevation {
        const val GRASS_MIN = 0f
        const val GRASS_MAX = 20f
        const val DIRT_MIN = 15f
        const val DIRT_MAX = 40f
        const val ROCK_MIN = 35f
        const val ROCK_MAX = 60f
        const val SNOW_MIN = 55f
        const val SNOW_MAX = 100f
    }
    
    // ==================================================================================
    // DEFAULT WATER SETTINGS
    // ==================================================================================
    
    object Water {
        /** Default water level (sea level) in meters */
        const val DEFAULT_WATER_HEIGHT = 20.0f
        
        /** Default water color (blue-green) */
        val DEFAULT_WATER_COLOR = LLColor4(0.1f, 0.2f, 0.3f, 0.8f)
        
        /** Default water fog color */
        val DEFAULT_FOG_COLOR = LLColor4(0.15f, 0.25f, 0.35f, 1.0f)
        
        /** Default water fog density */
        const val DEFAULT_FOG_DENSITY = 4.0f
        
        /** Default wave settings */
        const val DEFAULT_WAVE_SCALE = 2.0f
        const val DEFAULT_WAVE_SPEED = 0.5f
        
        /** Default normal map scale */
        const val DEFAULT_NORMAL_SCALE = 1.0f
        
        /** Default water transparency */
        const val DEFAULT_TRANSPARENCY = 0.7f
        
        /** Default fresnel scale */
        const val DEFAULT_FRESNEL_SCALE = 0.4f
        
        /** Default blur multiplier */
        const val DEFAULT_BLUR_MULTIPLIER = 0.04f
    }
    
    // ==================================================================================
    // DEFAULT RENDER SETTINGS
    // ==================================================================================
    
    object Render {
        /** Default draw distance in meters */
        const val DEFAULT_DRAW_DISTANCE = 128f
        
        /** Minimum draw distance */
        const val MIN_DRAW_DISTANCE = 32f
        
        /** Maximum draw distance */
        const val MAX_DRAW_DISTANCE = 512f
        
        /** Default LOD factor (1.0 = standard) */
        const val DEFAULT_LOD_FACTOR = 1.0f
        
        /** Default near clip plane */
        const val DEFAULT_NEAR_CLIP = 0.1f
        
        /** Default far clip plane */
        const val DEFAULT_FAR_CLIP = 1000f
        
        /** Default field of view in degrees */
        const val DEFAULT_FOV = 60f
        
        /** Default maximum number of particles */
        const val DEFAULT_MAX_PARTICLES = 4096
        
        /** Default texture memory limit in MB */
        const val DEFAULT_TEXTURE_MEMORY_MB = 512
    }
    
    // ==================================================================================
    // FACTORY METHODS
    // ==================================================================================
    
    /**
     * Create the default sky preset matching SL's standard midday settings.
     */
    fun createDefaultSkyPreset(): SkyPreset {
        return SkyPreset(
            name = "Second Life Default (Midday)",
            sunDirection = DEFAULT_SUN_DIRECTION,
            sunColor = DEFAULT_SUN_COLOR,
            sunIntensity = DEFAULT_SUN_INTENSITY,
            ambientIntensity = DEFAULT_AMBIENT_INTENSITY,
            horizonColor = DEFAULT_BLUE_HORIZON,
            hazeColor = DEFAULT_BLUE_DENSITY,
            cloudColor = DEFAULT_CLOUD_COLOR,
            cloudCoverage = DEFAULT_CLOUD_COVERAGE
        )
    }
    
    /**
     * Create default EEP settings matching SL's standard environment.
     */
    fun createDefaultEEPSettings(): EEPSettings {
        return EEPSettings(
            name = "Second Life Default",
            sunDirection = DEFAULT_SUN_DIRECTION,
            moonDirection = DEFAULT_MOON_DIRECTION,
            sunlightColor = DEFAULT_SUN_COLOR,
            ambientColor = DEFAULT_AMBIENT_COLOR,
            blueHorizon = DEFAULT_BLUE_HORIZON,
            blueDensity = DEFAULT_BLUE_DENSITY,
            hazeHorizon = DEFAULT_HAZE_HORIZON,
            hazeDensity = DEFAULT_HAZE_DENSITY,
            cloudColor = DEFAULT_CLOUD_COLOR,
            cloudCoverage = DEFAULT_CLOUD_COVERAGE,
            sunScale = 1.0f,
            ambientScale = 1.0f,
            moonScale = 1.0f
        )
    }
    
    /**
     * Log the default environment settings being applied.
     */
    fun logDefaults() {
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ APPLYING SECOND LIFE DEFAULT ENVIRONMENT")
        Log.i(TAG, "╠══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ Sky: Midday preset (warm sun, blue sky)")
        Log.i(TAG, "║ Sun Intensity: $DEFAULT_SUN_INTENSITY lux")
        Log.i(TAG, "║ Ambient Intensity: $DEFAULT_AMBIENT_INTENSITY lux")
        Log.i(TAG, "║ Water Level: ${Water.DEFAULT_WATER_HEIGHT}m")
        Log.i(TAG, "║ Draw Distance: ${Render.DEFAULT_DRAW_DISTANCE}m")
        Log.i(TAG, "║ Terrain: Standard grass/dirt/rock/snow textures")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }
    
    // ==================================================================================
    // PREDEFINED TIME-OF-DAY PRESETS
    // ==================================================================================
    
    /**
     * Predefined time-of-day presets matching classic SL Windlight settings.
     */
    object TimeOfDayPresets {
        
        /** Sunrise - warm orange/pink sky */
        val SUNRISE = SkyPreset(
            name = "Sunrise",
            sunDirection = LLVector3(-0.95f, -0.15f, -0.3f).normalize(),
            sunColor = LLColor4(1.0f, 0.7f, 0.4f, 1.0f),
            sunIntensity = 60_000f,
            ambientIntensity = 10_000f,
            horizonColor = LLColor4(1.0f, 0.6f, 0.4f, 1.0f),
            hazeColor = LLColor4(0.8f, 0.5f, 0.3f, 1.0f),
            cloudColor = LLColor4(1.0f, 0.8f, 0.6f, 1.0f),
            cloudCoverage = 0.3f
        )
        
        /** Midday - bright blue sky with sun overhead */
        val MIDDAY = createDefaultSkyPreset()
        
        /** Sunset - warm red/orange sky */
        val SUNSET = SkyPreset(
            name = "Sunset",
            sunDirection = LLVector3(0.95f, -0.15f, -0.3f).normalize(),
            sunColor = LLColor4(1.0f, 0.5f, 0.2f, 1.0f),
            sunIntensity = 50_000f,
            ambientIntensity = 8_000f,
            horizonColor = LLColor4(1.0f, 0.4f, 0.2f, 1.0f),
            hazeColor = LLColor4(0.9f, 0.4f, 0.2f, 1.0f),
            cloudColor = LLColor4(1.0f, 0.6f, 0.4f, 1.0f),
            cloudCoverage = 0.35f
        )
        
        /** Midnight - dark blue sky with stars */
        val MIDNIGHT = SkyPreset(
            name = "Midnight",
            sunDirection = LLVector3(0f, 1f, 0f).normalize(),
            sunColor = LLColor4(0.2f, 0.2f, 0.3f, 1.0f),
            sunIntensity = 500f,
            ambientIntensity = 300f,
            horizonColor = LLColor4(0.05f, 0.05f, 0.15f, 1.0f),
            hazeColor = LLColor4(0.02f, 0.02f, 0.05f, 1.0f),
            cloudColor = LLColor4(0.3f, 0.3f, 0.4f, 1.0f),
            cloudCoverage = 0.2f
        )
        
        /**
         * Get preset for a specific time of day (0-24 hours).
         */
        fun getPresetForTime(hour: Float): SkyPreset {
            return when {
                hour < 5 || hour >= 21 -> MIDNIGHT
                hour < 7 -> SUNRISE
                hour < 17 -> MIDDAY
                hour < 19 -> SUNSET
                else -> MIDNIGHT
            }
        }
    }
    
    // ==================================================================================
    // GROUND PLANE / FALLBACK RENDERING
    // ==================================================================================
    
    /**
     * Default ground plane settings for when terrain data isn't loaded.
     * This ensures something is visible instead of falling through the void.
     */
    object GroundPlane {
        /** Default ground plane color (grass green) */
        val DEFAULT_COLOR = LLColor4(0.3f, 0.5f, 0.2f, 1.0f)
        
        /** Default ground plane size (covers standard region) */
        const val DEFAULT_SIZE = 256f
        
        /** Default ground plane height */
        const val DEFAULT_HEIGHT = 0f
        
        /** Whether to show grid lines on ground */
        const val SHOW_GRID = false
        
        /** Grid line spacing in meters */
        const val GRID_SPACING = 10f
    }
    
    // ==================================================================================
    // FOG / ATMOSPHERE SETTINGS
    // ==================================================================================
    
    object Atmosphere {
        /** Default fog start distance */
        const val FOG_START = 96f
        
        /** Default fog end distance (matches draw distance) */
        const val FOG_END = 128f
        
        /** Default fog color (matches horizon) */
        val FOG_COLOR = LLColor4(0.5f, 0.6f, 0.8f, 1.0f)
        
        /** Default fog density */
        const val FOG_DENSITY = 0.02f
    }
}
