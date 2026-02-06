package com.linkpoint.render.environment

import android.content.Context
import android.util.Log
import com.google.android.filament.*
import com.linkpoint.protocol.llsd.LLSDArray
import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.protocol.llsd.LLSDReal
import com.linkpoint.protocol.llsd.LLSDValue
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLVector3

/**
 * Renders sky and atmosphere effects
 * Supports both legacy Windlight and EEP (Environmental Enhancement Project)
 */
class SkyRenderer(
    private val context: Context,
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "SkyRenderer"
    }
    
    // Current sky settings
    private var currentPreset: SkyPreset = SkyPreset.DEFAULT
    
    // Filament skybox
    private var skybox: Skybox? = null
    private var indirectLight: IndirectLight? = null
    
    // Sun entity
    private var sunEntity: Int = 0
    
    init {
        createSunLight()
        applyPreset(SkyPreset.DEFAULT)
    }
    
    private fun createSunLight() {
        sunEntity = EntityManager.get().create()
        
        LightManager.Builder(LightManager.Type.SUN)
            .color(1.0f, 0.95f, 0.9f)
            .intensity(110_000f)
            .direction(-0.5f, -1.0f, -0.5f)
            .castShadows(true)
            .sunAngularRadius(0.545f)
            .sunHaloSize(10.0f)
            .sunHaloFalloff(80.0f)
            .build(engine, sunEntity)
        
        scene.addEntity(sunEntity)
    }
    
    /**
     * Apply a sky preset
     */
    fun applyPreset(preset: SkyPreset) {
        currentPreset = preset
        
        // Update sun position and color
        val lightManager = engine.lightManager
        val instance = lightManager.getInstance(sunEntity)
        if (instance != 0) {
            lightManager.setDirection(instance, 
                preset.sunDirection.x,
                preset.sunDirection.y,
                preset.sunDirection.z
            )
            lightManager.setColor(instance,
                preset.sunColor.r,
                preset.sunColor.g,
                preset.sunColor.b
            )
            lightManager.setIntensity(instance, preset.sunIntensity)
        }
        
        // Update ambient light
        indirectLight?.let { engine.destroyIndirectLight(it) }
        indirectLight = IndirectLight.Builder()
            .intensity(preset.ambientIntensity)
            .build(engine)
        scene.indirectLight = indirectLight
        
        Log.d(TAG, "Applied sky preset: ${preset.name}")
    }
    
    /**
     * Apply EEP settings from server (EnvironmentSettings/ExtEnvironment).
     */
    fun applyEEP(settings: EEPSettings) {
        // Convert EEP settings to sky preset
        val preset = SkyPreset(
            name = settings.name,
            sunDirection = settings.sunDirection,
            sunColor = settings.sunlightColor,
            sunIntensity = settings.sunScale * 100000f,
            ambientIntensity = settings.ambientScale * 30000f,
            horizonColor = settings.blueHorizon,
            hazeColor = settings.blueDensity,
            cloudColor = settings.cloudColor,
            cloudCoverage = settings.cloudCoverage
        )
        applyPreset(preset)
    }
    
    /**
     * Load Windlight preset from assets
     */
    fun loadWindlightPreset(presetName: String) {
        // Load from assets/windlight/*.xml
        try {
            val inputStream = context.assets.open("windlight/$presetName.xml")
            val xml = inputStream.bufferedReader().readText()
            inputStream.close()
            
            val preset = parseWindlightXml(xml, presetName)
            applyPreset(preset)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load Windlight preset: $presetName", e)
        }
    }
    
    private fun parseWindlightXml(xml: String, name: String): SkyPreset {
        // Parse Windlight XML format
        // This is a simplified parser - full implementation would handle all parameters
        
        fun extractFloat(key: String): Float {
            val regex = """<key>$key</key>\s*<real>([\d.eE+-]+)</real>""".toRegex()
            return regex.find(xml)?.groupValues?.get(1)?.toFloatOrNull() ?: 0f
        }
        
        fun extractVector(key: String): LLVector3 {
            val regex = """<key>$key</key>\s*<array>\s*<real>([\d.eE+-]+)</real>\s*<real>([\d.eE+-]+)</real>\s*<real>([\d.eE+-]+)</real>""".toRegex()
            val match = regex.find(xml)
            return if (match != null) {
                LLVector3(
                    match.groupValues[1].toFloatOrNull() ?: 0f,
                    match.groupValues[2].toFloatOrNull() ?: 0f,
                    match.groupValues[3].toFloatOrNull() ?: 0f
                )
            } else LLVector3.zero()
        }
        
        fun extractColor(key: String): LLColor4 {
            val v = extractVector(key)
            return LLColor4(v.x, v.y, v.z, 1f)
        }
        
        val sunAngle = extractFloat("sun_angle")
        val eastAngle = extractFloat("east_angle")
        
        // Calculate sun direction from angles
        val sunY = kotlin.math.sin(sunAngle)
        val sunXZ = kotlin.math.cos(sunAngle)
        val sunX = sunXZ * kotlin.math.sin(eastAngle)
        val sunZ = sunXZ * kotlin.math.cos(eastAngle)
        
        return SkyPreset(
            name = name,
            sunDirection = LLVector3(sunX, sunY, sunZ).normalize(),
            sunColor = extractColor("sunlight_color"),
            sunIntensity = extractFloat("sun_intensity") * 100000f,
            ambientIntensity = extractFloat("ambient_intensity") * 30000f,
            horizonColor = extractColor("blue_horizon"),
            hazeColor = extractColor("blue_density"),
            cloudColor = extractColor("cloud_color"),
            cloudCoverage = extractFloat("cloud_coverage")
        )
    }
    
    /**
     * Update sun position based on time
     */
    fun updateTimeOfDay(time: Float) {
        // Time is 0-24 hours
        val normalizedTime = time / 24f
        val angle = normalizedTime * kotlin.math.PI.toFloat() * 2f - kotlin.math.PI.toFloat() / 2f
        
        val sunY = kotlin.math.sin(angle)
        val sunXZ = kotlin.math.cos(angle)
        
        val sunDirection = LLVector3(0.3f * sunXZ, sunY, sunXZ).normalize()
        
        val lightManager = engine.lightManager
        val instance = lightManager.getInstance(sunEntity)
        if (instance != 0) {
            lightManager.setDirection(instance, sunDirection.x, sunDirection.y, sunDirection.z)
            
            // Adjust intensity based on sun height
            val intensity = (sunY + 0.2f).coerceIn(0f, 1f) * currentPreset.sunIntensity
            lightManager.setIntensity(instance, intensity)
            
            // Warm color at sunrise/sunset
            val warmth = (1f - kotlin.math.abs(sunY)).coerceIn(0f, 1f) * 0.3f
            lightManager.setColor(instance, 
                1f,
                0.95f - warmth * 0.2f,
                0.9f - warmth * 0.4f
            )
        }
    }
    
    fun destroy() {
        skybox?.let { engine.destroySkybox(it) }
        indirectLight?.let { engine.destroyIndirectLight(it) }
        scene.removeEntity(sunEntity)
        EntityManager.get().destroy(sunEntity)
    }
}

/**
 * Sky preset configuration
 */
data class SkyPreset(
    val name: String,
    val sunDirection: LLVector3,
    val sunColor: LLColor4,
    val sunIntensity: Float,
    val ambientIntensity: Float,
    val horizonColor: LLColor4 = SLDefaultEnvironment.DEFAULT_BLUE_HORIZON,
    val hazeColor: LLColor4 = SLDefaultEnvironment.DEFAULT_BLUE_DENSITY,
    val cloudColor: LLColor4 = SLDefaultEnvironment.DEFAULT_CLOUD_COLOR,
    val cloudCoverage: Float = SLDefaultEnvironment.DEFAULT_CLOUD_COVERAGE
) {
    companion object {
        /**
         * Default preset using SL standard midday settings.
         */
        val DEFAULT = SLDefaultEnvironment.createDefaultSkyPreset()
        
        /**
         * Sunset preset using SL-like sunset settings.
         */
        val SUNSET = SLDefaultEnvironment.TimeOfDayPresets.SUNSET
        
        /**
         * Midnight preset using SL-like night settings.
         */
        val MIDNIGHT = SLDefaultEnvironment.TimeOfDayPresets.MIDNIGHT
        
        /**
         * Sunrise preset using SL-like sunrise settings.
         */
        val SUNRISE = SLDefaultEnvironment.TimeOfDayPresets.SUNRISE
    }
}

/**
 * Environmental Enhancement Project (EEP) settings from LLSD/OSD data.
 */
data class EEPSettings(
    val name: String,
    val sunDirection: LLVector3,
    val moonDirection: LLVector3,
    val sunlightColor: LLColor4,
    val ambientColor: LLColor4,
    val blueHorizon: LLColor4,
    val blueDensity: LLColor4,
    val hazeHorizon: Float,
    val hazeDensity: Float,
    val cloudColor: LLColor4,
    val cloudCoverage: Float,
    val sunScale: Float,
    val ambientScale: Float,
    val moonScale: Float
) {
    companion object {
        const val KEY_NAME = "name"
        const val KEY_SUN_DIRECTION = "sun_direction"
        const val KEY_SUN_DIR = "sun_dir"
        const val KEY_MOON_DIRECTION = "moon_direction"
        const val KEY_MOON_DIR = "moon_dir"
        const val KEY_SUNLIGHT_COLOR = "sunlight_color"
        const val KEY_AMBIENT = "ambient"
        const val KEY_AMBIENT_COLOR = "ambient_color"
        const val KEY_BLUE_HORIZON = "blue_horizon"
        const val KEY_BLUE_DENSITY = "blue_density"
        const val KEY_HAZE_HORIZON = "haze_horizon"
        const val KEY_HAZE_DENSITY = "haze_density"
        const val KEY_CLOUD_COLOR = "cloud_color"
        const val KEY_CLOUD_COVERAGE = "cloud_coverage"
        const val KEY_SUN_SCALE = "sun_scale"
        const val KEY_AMBIENT_SCALE = "ambient_scale"
        const val KEY_MOON_SCALE = "moon_scale"
        
        // Use SL defaults for fallback values
        private val DEFAULT_AMBIENT_COLOR = SLDefaultEnvironment.DEFAULT_AMBIENT_COLOR
        private val DEFAULT_BLUE_DENSITY = SLDefaultEnvironment.DEFAULT_BLUE_DENSITY

        fun fromEepOsd(map: LLSDMap): EEPSettings {
            val defaults = SkyPreset.DEFAULT
            return EEPSettings(
                name = map.getString(KEY_NAME) ?: "EEP",
                sunDirection = map.getVector3(KEY_SUN_DIRECTION, KEY_SUN_DIR) ?: defaults.sunDirection,
                moonDirection = map.getVector3(KEY_MOON_DIRECTION, KEY_MOON_DIR) ?: SLDefaultEnvironment.DEFAULT_MOON_DIRECTION,
                sunlightColor = map.getColor4(KEY_SUNLIGHT_COLOR) ?: defaults.sunColor,
                ambientColor = map.getColor4(KEY_AMBIENT, KEY_AMBIENT_COLOR) ?: DEFAULT_AMBIENT_COLOR,
                blueHorizon = map.getColor4(KEY_BLUE_HORIZON) ?: defaults.horizonColor,
                blueDensity = map.getColor4(KEY_BLUE_DENSITY) ?: DEFAULT_BLUE_DENSITY,
                hazeHorizon = map.getFloatValue(KEY_HAZE_HORIZON) ?: SLDefaultEnvironment.DEFAULT_HAZE_HORIZON,
                hazeDensity = map.getFloatValue(KEY_HAZE_DENSITY) ?: SLDefaultEnvironment.DEFAULT_HAZE_DENSITY,
                cloudColor = map.getColor4(KEY_CLOUD_COLOR) ?: defaults.cloudColor,
                cloudCoverage = map.getFloatValue(KEY_CLOUD_COVERAGE) ?: defaults.cloudCoverage,
                sunScale = map.getFloatValue(KEY_SUN_SCALE) ?: 1f,
                ambientScale = map.getFloatValue(KEY_AMBIENT_SCALE) ?: 1f,
                moonScale = map.getFloatValue(KEY_MOON_SCALE) ?: 1f
            )
        }

        private fun LLSDMap.getVector3(vararg keys: String): LLVector3? {
            for (key in keys) {
                val vector = getArray(key)?.toVector3()
                if (vector != null) return vector
            }
            return null
        }

        private fun LLSDMap.getColor4(vararg keys: String): LLColor4? {
            for (key in keys) {
                val color = getArray(key)?.toColor4()
                if (color != null) return color
            }
            return null
        }

        private fun LLSDMap.getFloatValue(vararg keys: String): Float? {
            for (key in keys) {
                val realValue = getReal(key) ?: getInt(key)?.toDouble()
                if (realValue != null) return realValue.toFloat()
            }
            return null
        }

        private fun LLSDArray.toVector3(): LLVector3? {
            val x = getFloat(0) ?: return null
            val y = getFloat(1) ?: return null
            val z = getFloat(2) ?: return null
            return LLVector3(x, y, z)
        }

        private fun LLSDArray.toColor4(): LLColor4? {
            val r = getFloat(0) ?: return null
            val g = getFloat(1) ?: return null
            val b = getFloat(2) ?: return null
            val a = getFloat(3) ?: 1f
            return LLColor4(r, g, b, a)
        }

        private fun LLSDArray.getFloat(index: Int): Float? {
            if (index < 0 || index >= size) return null
            return this[index].asFloat()
        }

        private fun LLSDValue?.asFloat(): Float? {
            return when (this) {
                is LLSDReal -> value.toFloat()
                is LLSDInteger -> value.toFloat()
                else -> null
            }
        }
    }
}
