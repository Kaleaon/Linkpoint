package com.linkpoint.world.environment

import android.graphics.Color
import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Environment Manager - Handles Windlight sky and water settings.
 * 
 * Based on the reference viewer's Windlight system (WindlightDay.java, WindlightSky.java, etc.)
 * 
 * Windlight controls:
 * - Sky appearance (sun, moon, stars, clouds, atmosphere)
 * - Water appearance (color, wave patterns, reflections)
 * - Day cycle (time of day transitions)
 * 
 * Uses the ExtEnvironment capability for EEP (Extended Environment Protocol).
 */
class EnvironmentManager(
    private val capabilityManager: CapabilityManager
) {
    companion object {
        private const val TAG = "EnvironmentManager"
        
        // Default presets
        val DEFAULT_SKY = SkySettings.createDefault()
        val DEFAULT_WATER = WaterSettings.createDefault()
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Current environment settings
    private val _currentSky = MutableStateFlow(DEFAULT_SKY)
    val currentSky: StateFlow<SkySettings> = _currentSky
    
    private val _currentWater = MutableStateFlow(DEFAULT_WATER)
    val currentWater: StateFlow<WaterSettings> = _currentWater
    
    private val _dayProgress = MutableStateFlow(0.5f) // 0.0 = midnight, 0.5 = noon
    val dayProgress: StateFlow<Float> = _dayProgress
    
    // Built-in presets
    private val skyPresets = mutableMapOf<String, SkySettings>()
    private val waterPresets = mutableMapOf<String, WaterSettings>()
    
    init {
        loadBuiltInPresets()
    }
    
    /**
     * Load built-in Windlight presets.
     */
    private fun loadBuiltInPresets() {
        // Default/Midday
        skyPresets["Default"] = DEFAULT_SKY
        skyPresets["Midday"] = SkySettings.createMidDay()
        
        // Sunrise/Sunset
        skyPresets["Sunrise"] = SkySettings.createSunrise()
        skyPresets["Sunset"] = SkySettings.createSunset()
        
        // Night
        skyPresets["Midnight"] = SkySettings.createMidnight()
        
        // Cloudy
        skyPresets["Cloudy"] = SkySettings.createCloudy()
        
        // Clear
        skyPresets["Clear"] = SkySettings.createClear()
        
        // Water presets
        waterPresets["Default"] = DEFAULT_WATER
        waterPresets["Calm"] = WaterSettings.createCalm()
        waterPresets["Tropical"] = WaterSettings.createTropical()
        waterPresets["Ocean"] = WaterSettings.createOcean()
        
        Log.d(TAG, "Loaded ${skyPresets.size} sky presets and ${waterPresets.size} water presets")
    }
    
    /**
     * Fetch environment settings from the region.
     */
    suspend fun fetchRegionEnvironment() {
        try {
            val cap = capabilityManager.getCapability(CapabilityManager.CAP_EXT_ENVIRONMENT)
                ?: capabilityManager.getCapability(CapabilityManager.CAP_ENVIRONMENT)
            
            if (cap == null) {
                Log.w(TAG, "No environment capability available")
                return
            }
            
            val response = capabilityManager.request(CapabilityManager.CAP_EXT_ENVIRONMENT)
            if (response is LLSDMap) {
                parseEnvironmentResponse(response)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch region environment", e)
        }
    }
    
    /**
     * Parse environment response from capability.
     */
    private fun parseEnvironmentResponse(response: LLSDMap) {
        try {
            // Parse day cycle if present
            response.getMap("day_cycle")?.let { dayCycle ->
                // Day cycles contain tracks with keyframes
                Log.d(TAG, "Received day cycle data")
            }
            
            // Parse current sky settings
            response.getMap("sky")?.let { sky ->
                _currentSky.value = parseSkySettings(sky)
            }
            
            // Parse current water settings
            response.getMap("water")?.let { water ->
                _currentWater.value = parseWaterSettings(water)
            }
            
            // Parse day progress
            response.getReal("day_offset")?.let { offset ->
                _dayProgress.value = offset.toFloat()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing environment response", e)
        }
    }
    
    /**
     * Parse sky settings from LLSD.
     */
    private fun parseSkySettings(llsd: LLSDMap): SkySettings {
        return SkySettings(
            // Ambient
            ambientColor = parseColor4(llsd.getArray("ambient")) ?: DEFAULT_SKY.ambientColor,
            
            // Sun
            sunColor = parseColor4(llsd.getArray("sunlight_color")) ?: DEFAULT_SKY.sunColor,
            sunScale = llsd.getReal("sun_scale")?.toFloat() ?: DEFAULT_SKY.sunScale,
            sunRotation = parseQuaternion(llsd.getArray("sun_rotation")),
            
            // Moon
            moonColor = parseColor4(llsd.getArray("moon_brightness")) ?: DEFAULT_SKY.moonColor,
            moonScale = llsd.getReal("moon_scale")?.toFloat() ?: DEFAULT_SKY.moonScale,
            moonRotation = parseQuaternion(llsd.getArray("moon_rotation")),
            
            // Stars
            starBrightness = llsd.getReal("star_brightness")?.toFloat() ?: DEFAULT_SKY.starBrightness,
            
            // Atmosphere
            blueDensity = parseColor3(llsd.getArray("blue_density")) ?: DEFAULT_SKY.blueDensity,
            blueHorizon = parseColor3(llsd.getArray("blue_horizon")) ?: DEFAULT_SKY.blueHorizon,
            hazeHorizon = llsd.getReal("haze_horizon")?.toFloat() ?: DEFAULT_SKY.hazeHorizon,
            hazeDensity = llsd.getReal("haze_density")?.toFloat() ?: DEFAULT_SKY.hazeDensity,
            densityMultiplier = llsd.getReal("density_multiplier")?.toFloat() ?: DEFAULT_SKY.densityMultiplier,
            distanceMultiplier = llsd.getReal("distance_multiplier")?.toFloat() ?: DEFAULT_SKY.distanceMultiplier,
            
            // Clouds
            cloudColor = parseColor4(llsd.getArray("cloud_color")) ?: DEFAULT_SKY.cloudColor,
            cloudCoverage = llsd.getReal("cloud_shadow")?.toFloat() ?: DEFAULT_SKY.cloudCoverage,
            cloudScale = llsd.getReal("cloud_scale")?.toFloat() ?: DEFAULT_SKY.cloudScale,
            cloudScroll = parseVector2(llsd.getArray("cloud_scroll_rate")),
            
            // Glow
            glowColor = parseColor3(llsd.getArray("glow")) ?: DEFAULT_SKY.glowColor,
            gamma = llsd.getReal("gamma")?.toFloat() ?: DEFAULT_SKY.gamma,
            
            // Dome
            domeOffset = llsd.getReal("dome_offset")?.toFloat() ?: DEFAULT_SKY.domeOffset,
            domeRadius = llsd.getReal("dome_radius")?.toFloat() ?: DEFAULT_SKY.domeRadius
        )
    }
    
    /**
     * Parse water settings from LLSD.
     */
    private fun parseWaterSettings(llsd: LLSDMap): WaterSettings {
        return WaterSettings(
            // Color
            waterColor = parseColor4(llsd.getArray("water_fog_color")) ?: DEFAULT_WATER.waterColor,
            fogDensity = llsd.getReal("water_fog_density")?.toFloat() ?: DEFAULT_WATER.fogDensity,
            fogMod = llsd.getReal("modifier_fog_density")?.toFloat() ?: DEFAULT_WATER.fogMod,
            
            // Underwater
            underwaterFogMod = llsd.getReal("underwater_fog_modifier")?.toFloat() ?: DEFAULT_WATER.underwaterFogMod,
            
            // Waves
            normalScale = parseVector3(llsd.getArray("normal_scale")),
            wave1Direction = parseVector2(llsd.getArray("wave1_direction")),
            wave2Direction = parseVector2(llsd.getArray("wave2_direction")),
            
            // Fresnel
            fresnelScale = llsd.getReal("fresnel_scale")?.toFloat() ?: DEFAULT_WATER.fresnelScale,
            fresnelOffset = llsd.getReal("fresnel_offset")?.toFloat() ?: DEFAULT_WATER.fresnelOffset,
            
            // Blur
            blurMultiplier = llsd.getReal("blur_multiplier")?.toFloat() ?: DEFAULT_WATER.blurMultiplier,
            
            // Scale
            scaleAbove = llsd.getReal("scale_above")?.toFloat() ?: DEFAULT_WATER.scaleAbove,
            scaleBelow = llsd.getReal("scale_below")?.toFloat() ?: DEFAULT_WATER.scaleBelow,
            
            // Normal map
            normalMapId = llsd.getUUID("normal_map") ?: DEFAULT_WATER.normalMapId
        )
    }
    
    private fun parseColor3(array: LLSDArray?): FloatArray? {
        if (array == null || array.value.size < 3) return null
        return floatArrayOf(
            (array.value[0] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[1] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[2] as? LLSDReal)?.value?.toFloat() ?: 0f
        )
    }
    
    private fun parseColor4(array: LLSDArray?): FloatArray? {
        if (array == null || array.value.size < 4) return null
        return floatArrayOf(
            (array.value[0] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[1] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[2] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[3] as? LLSDReal)?.value?.toFloat() ?: 1f
        )
    }
    
    private fun parseVector2(array: LLSDArray?): FloatArray {
        if (array == null || array.value.size < 2) return floatArrayOf(0f, 0f)
        return floatArrayOf(
            (array.value[0] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[1] as? LLSDReal)?.value?.toFloat() ?: 0f
        )
    }
    
    private fun parseVector3(array: LLSDArray?): FloatArray {
        if (array == null || array.value.size < 3) return floatArrayOf(1f, 1f, 1f)
        return floatArrayOf(
            (array.value[0] as? LLSDReal)?.value?.toFloat() ?: 1f,
            (array.value[1] as? LLSDReal)?.value?.toFloat() ?: 1f,
            (array.value[2] as? LLSDReal)?.value?.toFloat() ?: 1f
        )
    }
    
    private fun parseQuaternion(array: LLSDArray?): FloatArray {
        if (array == null || array.value.size < 4) return floatArrayOf(0f, 0f, 0f, 1f)
        return floatArrayOf(
            (array.value[0] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[1] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[2] as? LLSDReal)?.value?.toFloat() ?: 0f,
            (array.value[3] as? LLSDReal)?.value?.toFloat() ?: 1f
        )
    }
    
    /**
     * Apply a sky preset.
     */
    fun applySkyPreset(name: String) {
        skyPresets[name]?.let { preset ->
            _currentSky.value = preset
            Log.d(TAG, "Applied sky preset: $name")
        }
    }
    
    /**
     * Apply a water preset.
     */
    fun applyWaterPreset(name: String) {
        waterPresets[name]?.let { preset ->
            _currentWater.value = preset
            Log.d(TAG, "Applied water preset: $name")
        }
    }
    
    /**
     * Set custom sky settings.
     */
    fun setSky(settings: SkySettings) {
        _currentSky.value = settings
    }
    
    /**
     * Set custom water settings.
     */
    fun setWater(settings: WaterSettings) {
        _currentWater.value = settings
    }
    
    /**
     * Set day progress (time of day).
     */
    fun setDayProgress(progress: Float) {
        _dayProgress.value = progress.coerceIn(0f, 1f)
    }
    
    /**
     * Get list of available sky presets.
     */
    fun getSkyPresetNames(): List<String> = skyPresets.keys.toList()
    
    /**
     * Get list of available water presets.
     */
    fun getWaterPresetNames(): List<String> = waterPresets.keys.toList()
    
    /**
     * Shutdown the manager.
     */
    fun shutdown() {
        scope.cancel()
    }
}

/**
 * Sky settings data class.
 */
data class SkySettings(
    // Ambient
    val ambientColor: FloatArray = floatArrayOf(0.25f, 0.25f, 0.25f, 1f),
    
    // Sun
    val sunColor: FloatArray = floatArrayOf(1f, 0.98f, 0.9f, 1f),
    val sunScale: Float = 1f,
    val sunRotation: FloatArray = floatArrayOf(0f, 0f, 0f, 1f),
    
    // Moon
    val moonColor: FloatArray = floatArrayOf(0.5f, 0.5f, 0.5f, 1f),
    val moonScale: Float = 1f,
    val moonRotation: FloatArray = floatArrayOf(0f, 0f, 0f, 1f),
    
    // Stars
    val starBrightness: Float = 0f,
    
    // Atmosphere
    val blueDensity: FloatArray = floatArrayOf(0.24f, 0.45f, 0.76f),
    val blueHorizon: FloatArray = floatArrayOf(0.25f, 0.25f, 0.25f),
    val hazeHorizon: Float = 0.19f,
    val hazeDensity: Float = 0.7f,
    val densityMultiplier: Float = 0.00001f,
    val distanceMultiplier: Float = 0.8f,
    
    // Clouds
    val cloudColor: FloatArray = floatArrayOf(0.4f, 0.4f, 0.4f, 1f),
    val cloudCoverage: Float = 0.27f,
    val cloudScale: Float = 0.42f,
    val cloudScroll: FloatArray = floatArrayOf(0.2f, 0.01f),
    
    // Glow
    val glowColor: FloatArray = floatArrayOf(5f, 0.001f, -0.48f),
    val gamma: Float = 1f,
    
    // Dome
    val domeOffset: Float = 0.96f,
    val domeRadius: Float = 15000f
) {
    companion object {
        fun createDefault() = SkySettings()
        
        fun createMidDay() = SkySettings(
            sunColor = floatArrayOf(1f, 0.98f, 0.9f, 1f),
            ambientColor = floatArrayOf(0.3f, 0.3f, 0.35f, 1f),
            blueDensity = floatArrayOf(0.24f, 0.45f, 0.76f),
            hazeHorizon = 0.19f,
            starBrightness = 0f
        )
        
        fun createSunrise() = SkySettings(
            sunColor = floatArrayOf(1f, 0.7f, 0.4f, 1f),
            ambientColor = floatArrayOf(0.35f, 0.25f, 0.2f, 1f),
            blueDensity = floatArrayOf(0.3f, 0.35f, 0.5f),
            blueHorizon = floatArrayOf(0.5f, 0.35f, 0.25f),
            hazeHorizon = 0.4f,
            glowColor = floatArrayOf(10f, 0.01f, -0.3f),
            starBrightness = 0.1f
        )
        
        fun createSunset() = SkySettings(
            sunColor = floatArrayOf(1f, 0.5f, 0.2f, 1f),
            ambientColor = floatArrayOf(0.4f, 0.2f, 0.15f, 1f),
            blueDensity = floatArrayOf(0.2f, 0.25f, 0.4f),
            blueHorizon = floatArrayOf(0.6f, 0.3f, 0.15f),
            hazeHorizon = 0.5f,
            glowColor = floatArrayOf(15f, 0.02f, -0.2f),
            starBrightness = 0.2f
        )
        
        fun createMidnight() = SkySettings(
            sunColor = floatArrayOf(0.1f, 0.1f, 0.15f, 1f),
            ambientColor = floatArrayOf(0.05f, 0.05f, 0.1f, 1f),
            blueDensity = floatArrayOf(0.05f, 0.05f, 0.1f),
            blueHorizon = floatArrayOf(0.02f, 0.02f, 0.05f),
            hazeHorizon = 0.05f,
            starBrightness = 2f,
            moonColor = floatArrayOf(0.5f, 0.5f, 0.6f, 1f)
        )
        
        fun createCloudy() = SkySettings(
            sunColor = floatArrayOf(0.6f, 0.6f, 0.65f, 1f),
            ambientColor = floatArrayOf(0.3f, 0.3f, 0.35f, 1f),
            cloudColor = floatArrayOf(0.5f, 0.5f, 0.55f, 1f),
            cloudCoverage = 0.6f,
            cloudScale = 0.5f,
            hazeHorizon = 0.6f,
            hazeDensity = 1.2f
        )
        
        fun createClear() = SkySettings(
            sunColor = floatArrayOf(1f, 1f, 0.95f, 1f),
            ambientColor = floatArrayOf(0.25f, 0.3f, 0.4f, 1f),
            cloudCoverage = 0f,
            hazeHorizon = 0.1f,
            hazeDensity = 0.3f,
            blueDensity = floatArrayOf(0.3f, 0.5f, 0.9f)
        )
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SkySettings) return false
        return ambientColor.contentEquals(other.ambientColor) &&
               sunColor.contentEquals(other.sunColor) &&
               sunScale == other.sunScale
    }
    
    override fun hashCode(): Int {
        var result = ambientColor.contentHashCode()
        result = 31 * result + sunColor.contentHashCode()
        result = 31 * result + sunScale.hashCode()
        return result
    }
}

/**
 * Water settings data class.
 */
data class WaterSettings(
    // Color
    val waterColor: FloatArray = floatArrayOf(0.0f, 0.15f, 0.25f, 1f),
    val fogDensity: Float = 16f,
    val fogMod: Float = 0.25f,
    
    // Underwater
    val underwaterFogMod: Float = 0.25f,
    
    // Waves
    val normalScale: FloatArray = floatArrayOf(2f, 2f, 2f),
    val wave1Direction: FloatArray = floatArrayOf(1.04f, -0.42f),
    val wave2Direction: FloatArray = floatArrayOf(1.11f, -1.16f),
    
    // Fresnel
    val fresnelScale: Float = 0.4f,
    val fresnelOffset: Float = 0.5f,
    
    // Blur
    val blurMultiplier: Float = 0.04f,
    
    // Scale
    val scaleAbove: Float = 0.025f,
    val scaleBelow: Float = 0.2f,
    
    // Normal map texture
    val normalMapId: UUID = UUID.fromString("822ded49-9a6c-f61c-cb89-6df54f42cdf4")
) {
    companion object {
        fun createDefault() = WaterSettings()
        
        fun createCalm() = WaterSettings(
            waterColor = floatArrayOf(0.0f, 0.2f, 0.3f, 1f),
            wave1Direction = floatArrayOf(0.5f, -0.2f),
            wave2Direction = floatArrayOf(0.5f, -0.5f),
            normalScale = floatArrayOf(1f, 1f, 1f),
            fresnelScale = 0.3f
        )
        
        fun createTropical() = WaterSettings(
            waterColor = floatArrayOf(0.0f, 0.3f, 0.35f, 1f),
            fogDensity = 8f,
            fresnelScale = 0.5f,
            wave1Direction = floatArrayOf(1.2f, -0.3f)
        )
        
        fun createOcean() = WaterSettings(
            waterColor = floatArrayOf(0.0f, 0.1f, 0.2f, 1f),
            fogDensity = 24f,
            normalScale = floatArrayOf(3f, 3f, 3f),
            wave1Direction = floatArrayOf(2f, -1f),
            wave2Direction = floatArrayOf(2f, -2f),
            fresnelScale = 0.5f
        )
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WaterSettings) return false
        return waterColor.contentEquals(other.waterColor) &&
               fogDensity == other.fogDensity
    }
    
    override fun hashCode(): Int {
        var result = waterColor.contentHashCode()
        result = 31 * result + fogDensity.hashCode()
        return result
    }
}
