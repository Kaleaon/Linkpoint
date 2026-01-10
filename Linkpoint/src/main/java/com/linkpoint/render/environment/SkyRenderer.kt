package com.linkpoint.render.environment

import android.content.Context
import android.util.Log
import com.google.android.filament.*
import com.linkpoint.protocol.types.LLColor4
import com.linkpoint.protocol.types.LLVector3
import java.nio.ByteBuffer

/**
 * Renders sky and atmosphere effects
 * Supports both legacy Windlight and EEP (Extended Environment Project)
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
     * Apply EEP settings from server
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
    val horizonColor: LLColor4 = LLColor4(0.5f, 0.6f, 0.8f, 1f),
    val hazeColor: LLColor4 = LLColor4(0.2f, 0.3f, 0.5f, 1f),
    val cloudColor: LLColor4 = LLColor4.white(),
    val cloudCoverage: Float = 0.5f
) {
    companion object {
        val DEFAULT = SkyPreset(
            name = "Default Midday",
            sunDirection = LLVector3(-0.5f, -1f, -0.5f).normalize(),
            sunColor = LLColor4(1f, 0.95f, 0.9f, 1f),
            sunIntensity = 110_000f,
            ambientIntensity = 30_000f
        )
        
        val SUNSET = SkyPreset(
            name = "Sunset",
            sunDirection = LLVector3(-0.9f, -0.2f, -0.4f).normalize(),
            sunColor = LLColor4(1f, 0.6f, 0.3f, 1f),
            sunIntensity = 80_000f,
            ambientIntensity = 15_000f,
            horizonColor = LLColor4(1f, 0.5f, 0.2f, 1f)
        )
        
        val MIDNIGHT = SkyPreset(
            name = "Midnight",
            sunDirection = LLVector3(0f, 1f, 0f).normalize(),
            sunColor = LLColor4(0.2f, 0.2f, 0.3f, 1f),
            sunIntensity = 1_000f,
            ambientIntensity = 500f,
            horizonColor = LLColor4(0.1f, 0.1f, 0.2f, 1f)
        )
    }
}

/**
 * EEP (Extended Environment Project) settings from server
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
)
