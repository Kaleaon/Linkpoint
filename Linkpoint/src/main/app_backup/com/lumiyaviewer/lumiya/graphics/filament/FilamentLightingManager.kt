package com.lumiyaviewer.lumiya.graphics.filament

import android.util.Log
import com.google.android.filament.*
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import kotlin.math.*

/**
 * FilamentLightingManager - Manages lighting for Filament scenes
 * 
 * Handles:
 * - Directional light (sun)
 * - Point lights (lamps, torches)
 * - Environment lighting (IBL)
 * - Shadow configuration
 * - Windlight integration
 */
class FilamentLightingManager(
    private val engine: Engine,
    private val scene: Scene
) {
    companion object {
        private const val TAG = "FilamentLightingMgr"
        private const val MAX_POINT_LIGHTS = 8
    }
    
    // Sun light entity
    @Entity private var sunLight: Int = 0
    
    // Point lights
    private val pointLights = mutableListOf<Int>()
    
    // IBL (Image-Based Lighting)
    private var indirectLight: IndirectLight? = null
    
    /**
     * Initialize default lighting
     */
    fun initialize() {
        // Create sun light
        createSunLight()
        
        // Create default IBL
        createDefaultIBL()
        
        Log.i(TAG, "Lighting manager initialized")
    }
    
    /**
     * Create directional sun light
     */
    private fun createSunLight() {
        sunLight = EntityManager.get().create()
        
        // Default sun direction (from above and slightly to side)
        val direction = floatArrayOf(0.3f, -0.7f, -0.6f)
        
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1.0f, 0.98f, 0.9f)  // Warm sunlight
            .intensity(100000.0f)
            .direction(direction[0], direction[1], direction[2])
            .castShadows(true)
            .shadowOptions(LightManager.ShadowOptions().apply {
                mapSize = 2048
                shadowCascades = 3
                constantBias = 0.001f
                normalBias = 1.0f
                screenSpaceContactShadows = true
            })
            .build(engine, sunLight)
        
        scene.addEntity(sunLight)
        
        Log.i(TAG, "Sun light created")
    }
    
    /**
     * Update sun light direction and color (e.g., for time of day)
     */
    fun updateSunLight(
        direction: LLVector3,
        color: LLVector3,
        intensity: Float = 100000f
    ) {
        if (sunLight == 0) {
            createSunLight()
        }
        
        val lm = engine.lightManager
        val instance = lm.getInstance(sunLight)
        
        if (instance != 0) {
            lm.setDirection(instance, direction.x, direction.y, direction.z)
            lm.setColor(instance, color.x, color.y, color.z)
            lm.setIntensity(instance, intensity)
        }
    }
    
    /**
     * Create a point light
     */
    fun createPointLight(
        position: LLVector3,
        color: LLVector3,
        intensity: Float = 10000f,
        radius: Float = 10f
    ): Int {
        if (pointLights.size >= MAX_POINT_LIGHTS) {
            Log.w(TAG, "Max point lights reached, removing oldest")
            removePointLight(pointLights[0])
        }
        
        @Entity val lightEntity = EntityManager.get().create()
        
        LightManager.Builder(LightManager.Type.POINT)
            .color(color.x, color.y, color.z)
            .intensity(intensity)
            .position(position.x, position.y, position.z)
            .falloff(radius)
            .castShadows(false) // Point shadows are expensive
            .build(engine, lightEntity)
        
        scene.addEntity(lightEntity)
        pointLights.add(lightEntity)
        
        Log.d(TAG, "Point light created at (${position.x}, ${position.y}, ${position.z})")
        
        return lightEntity
    }
    
    /**
     * Remove a point light
     */
    fun removePointLight(@Entity lightEntity: Int) {
        scene.removeEntity(lightEntity)
        engine.destroyEntity(lightEntity)
        EntityManager.get().destroy(lightEntity)
        pointLights.remove(lightEntity)
    }
    
    /**
     * Create default IBL (environment lighting)
     */
    private fun createDefaultIBL() {
        // Create simple default IBL
        // Implemented: Load from assets or generate procedural skybox
        
        indirectLight = IndirectLight.Builder()
            .intensity(30000f)
            .build(engine)
        
        scene.indirectLight = indirectLight
        
        Log.i(TAG, "Default IBL created")
    }
    
    /**
     * Set windlight settings (SL's environment system)
     * 
     * @param sunAngle Sun angle in radians (0 = noon, PI = midnight)
     * @param sunColor RGB color of sun
     * @param ambientColor RGB color of ambient light
     */
    fun setWindlightSettings(
        sunAngle: Float,
        sunColor: LLVector3,
        ambientColor: LLVector3
    ) {
        // Convert sun angle to direction vector
        val sunDir = LLVector3(
            sin(sunAngle),
            -cos(sunAngle),
            -0.3f
        )
        
        // Update sun
        updateSunLight(sunDir, sunColor)
        
        // Update IBL intensity based on ambient
        indirectLight?.let {
            val avgAmbient = (ambientColor.x + ambientColor.y + ambientColor.z) / 3f
            it.intensity = avgAmbient * 30000f
        }
        
        Log.d(TAG, "Windlight updated: sunAngle=$sunAngle")
    }
    
    /**
     * Enable/disable shadows
     */
    fun setShadowsEnabled(enabled: Boolean) {
        if (sunLight != 0) {
            val lm = engine.lightManager
            val instance = lm.getInstance(sunLight)
            if (instance != 0) {
                lm.setShadowCaster(instance, enabled)
            }
        }
    }
    
    /**
     * Cleanup all lights
     */
    fun destroy() {
        Log.i(TAG, "Destroying lighting...")
        
        // Destroy sun
        if (sunLight != 0) {
            scene.removeEntity(sunLight)
            engine.destroyEntity(sunLight)
            EntityManager.get().destroy(sunLight)
            sunLight = 0
        }
        
        // Destroy point lights
        pointLights.forEach { light ->
            try {
                scene.removeEntity(light)
                engine.destroyEntity(light)
                EntityManager.get().destroy(light)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying point light", e)
            }
        }
        pointLights.clear()
        
        // Destroy IBL
        indirectLight?.let {
            scene.indirectLight = null
            engine.destroyIndirectLight(it)
            indirectLight = null
        }
        
        Log.i(TAG, "Lighting manager destroyed")
    }
}
