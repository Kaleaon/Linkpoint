package com.linkpoint.eep

import android.util.Log
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * EEP Manager
 * Manages Enhanced Environment settings and day cycles
 */
class EEPManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    companion object {
        private const val TAG = "EEPManager"
        private const val UPDATE_INTERVAL_MS = 100L // Update every 100ms
    }
    
    // Current environments
    private val regionEnvironment = ConcurrentHashMap<UUID, EEPEnvironment>()
    private val parcelEnvironments = ConcurrentHashMap<UUID, EEPParcelEnvironment>()
    private val dayCycles = ConcurrentHashMap<UUID, EEPDayCycle>()
    
    // Current active environment
    private var currentEnvironment: EEPEnvironment? = null
    private var currentDayCycle: EEPDayCycle? = null
    private var currentDayTime: Float = 0.5f // Noon
    
    // Update job
    private var updateJob: Job? = null
    private var isRunning = false
    
    // Callbacks
    private val environmentChangeCallbacks = mutableListOf<(EEPEnvironment) -> Unit>()
    
    /**
     * Start EEP manager
     */
    fun start() {
        if (isRunning) return
        
        isRunning = true
        updateJob = scope.launch {
            while (isActive && isRunning) {
                updateDayCycle()
                delay(UPDATE_INTERVAL_MS)
            }
        }
        
        Log.i(TAG, "EEP manager started")
    }
    
    /**
     * Stop EEP manager
     */
    fun stop() {
        isRunning = false
        updateJob?.cancel()
        updateJob = null
        Log.i(TAG, "EEP manager stopped")
    }
    
    /**
     * Update day cycle
     */
    private fun updateDayCycle() {
        val dayCycle = currentDayCycle ?: return
        
        // Advance time
        val timeIncrement = UPDATE_INTERVAL_MS / (dayCycle.dayLength * 1000f)
        currentDayTime = (currentDayTime + timeIncrement) % 1.0f
        
        // Interpolate environment based on current time
        val interpolated = interpolateEnvironment(dayCycle, currentDayTime)
        
        if (interpolated != null && interpolated != currentEnvironment) {
            setCurrentEnvironment(interpolated)
        }
    }
    
    /**
     * Interpolate environment from day cycle
     */
    private fun interpolateEnvironment(dayCycle: EEPDayCycle, time: Float): EEPEnvironment? {
        // Find surrounding keyframes for each track
        val skyTrack = dayCycle.tracks.find { it.trackType == EEPTrack.TrackType.SKY }
        val waterTrack = dayCycle.tracks.find { it.trackType == EEPTrack.TrackType.WATER }
        
        val skyEnv = interpolateTrack(skyTrack, time)
        val waterEnv = interpolateTrack(waterTrack, time)
        
        return when {
            skyEnv != null && waterEnv != null -> {
                // Merge sky and water settings
                skyEnv.copy(
                    water = waterEnv.water
                )
            }
            skyEnv != null -> skyEnv
            waterEnv != null -> waterEnv
            else -> null
        }
    }
    
    /**
     * Interpolate environment from track
     */
    private fun interpolateTrack(track: EEPTrack?, time: Float): EEPEnvironment? {
        if (track == null || track.keyframes.isEmpty()) return null
        
        val sortedKeyframes = track.keyframes.sortedBy { it.time }
        
        // Find surrounding keyframes
        val nextIndex = sortedKeyframes.indexOfFirst { it.time > time }
        
        if (nextIndex == -1) {
            // Past last keyframe, loop to first
            return interpolateEnvironments(
                sortedKeyframes.last().environment,
                sortedKeyframes.first().environment,
                (time - sortedKeyframes.last().time) / (1.0f - sortedKeyframes.last().time)
            )
        }
        
        if (nextIndex == 0) {
            // Before first keyframe, use first
            return sortedKeyframes.first().environment
        }
        
        val prev = sortedKeyframes[nextIndex - 1]
        val next = sortedKeyframes[nextIndex]
        
        val t = (time - prev.time) / (next.time - prev.time)
        
        return interpolateEnvironments(prev.environment, next.environment, t)
    }
    
    /**
     * Interpolate between two environments
     */
    private fun interpolateEnvironments(a: EEPEnvironment, b: EEPEnvironment, t: Float): EEPEnvironment {
        return EEPEnvironment(
            environmentId = a.environmentId,
            name = a.name,
            sky = interpolateSky(a.sky, b.sky, t),
            water = interpolateWater(a.water, b.water, t),
            flags = a.flags,
            version = a.version
        )
    }
    
    /**
     * Interpolate sky settings
     */
    private fun interpolateSky(a: EEPSkySettings, b: EEPSkySettings, t: Float): EEPSkySettings {
        return EEPSkySettings(
            sunAzimuth = lerp(a.sunAzimuth, b.sunAzimuth, t),
            sunElevation = lerp(a.sunElevation, b.sunElevation, t),
            moonAzimuth = lerp(a.moonAzimuth, b.moonAzimuth, t),
            moonElevation = lerp(a.moonElevation, b.moonElevation, t),
            sunlightColor = Color3.lerp(a.sunlightColor, b.sunlightColor, t),
            moonlightColor = Color3.lerp(a.moonlightColor, b.moonlightColor, t),
            ambientColor = Color3.lerp(a.ambientColor, b.ambientColor, t),
            blueDensity = Color3.lerp(a.blueDensity, b.blueDensity, t),
            blueHorizon = Color3.lerp(a.blueHorizon, b.blueHorizon, t),
            hazeDensity = lerp(a.hazeDensity, b.hazeDensity, t),
            hazeHorizon = lerp(a.hazeHorizon, b.hazeHorizon, t),
            densityMultiplier = lerp(a.densityMultiplier, b.densityMultiplier, t),
            distanceMultiplier = lerp(a.distanceMultiplier, b.distanceMultiplier, t),
            cloudColor = Color3.lerp(a.cloudColor, b.cloudColor, t),
            cloudScale = lerp(a.cloudScale, b.cloudScale, t),
            cloudShadow = lerp(a.cloudShadow, b.cloudShadow, t),
            starBrightness = lerp(a.starBrightness, b.starBrightness, t)
        )
    }
    
    /**
     * Interpolate water settings
     */
    private fun interpolateWater(a: EEPWaterSettings, b: EEPWaterSettings, t: Float): EEPWaterSettings {
        return EEPWaterSettings(
            waterFogColor = Color3.lerp(a.waterFogColor, b.waterFogColor, t),
            waterFogDensity = lerp(a.waterFogDensity, b.waterFogDensity, t),
            underwaterFogMod = lerp(a.underwaterFogMod, b.underwaterFogMod, t),
            fresnelScale = lerp(a.fresnelScale, b.fresnelScale, t),
            fresnelOffset = lerp(a.fresnelOffset, b.fresnelOffset, t),
            refractScaleAbove = lerp(a.refractScaleAbove, b.refractScaleAbove, t),
            refractScaleBelow = lerp(a.refractScaleBelow, b.refractScaleBelow, t),
            blurMultiplier = lerp(a.blurMultiplier, b.blurMultiplier, t)
        )
    }
    
    /**
     * Linear interpolation
     */
    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * t
    }
    
    /**
     * Set region environment
     */
    fun setRegionEnvironment(regionId: UUID, environment: EEPEnvironment) {
        regionEnvironment[regionId] = environment
        Log.i(TAG, "Set region environment: $regionId")
        
        // If no day cycle active, use this environment
        if (currentDayCycle == null) {
            setCurrentEnvironment(environment)
        }
    }
    
    /**
     * Set parcel environment
     */
    fun setParcelEnvironment(parcel: EEPParcelEnvironment) {
        parcelEnvironments[parcel.parcelId] = parcel
        Log.i(TAG, "Set parcel environment: ${parcel.parcelId}")
    }
    
    /**
     * Set day cycle
     */
    fun setDayCycle(dayCycle: EEPDayCycle) {
        dayCycles[dayCycle.dayCycleId] = dayCycle
        currentDayCycle = dayCycle
        Log.i(TAG, "Set day cycle: ${dayCycle.name}")
    }
    
    /**
     * Set current environment
     */
    private fun setCurrentEnvironment(environment: EEPEnvironment) {
        currentEnvironment = environment
        
        // Notify callbacks
        environmentChangeCallbacks.forEach { it(environment) }
    }
    
    /**
     * Get current environment
     */
    fun getCurrentEnvironment(): EEPEnvironment? = currentEnvironment
    
    /**
     * Get current day time
     */
    fun getCurrentDayTime(): Float = currentDayTime
    
    /**
     * Set day time manually
     */
    fun setDayTime(time: Float) {
        currentDayTime = time.coerceIn(0f, 1f)
    }
    
    /**
     * Add environment change callback
     */
    fun addEnvironmentChangeCallback(callback: (EEPEnvironment) -> Unit) {
        environmentChangeCallbacks.add(callback)
    }
    
    /**
     * Remove environment change callback
     */
    fun removeEnvironmentChangeCallback(callback: (EEPEnvironment) -> Unit) {
        environmentChangeCallbacks.remove(callback)
    }
    
    /**
     * Create default environment
     */
    fun createDefaultEnvironment(): EEPEnvironment {
        return EEPEnvironment(
            environmentId = UUID.randomUUID(),
            name = "Default",
            sky = EEPSkySettings(),
            water = EEPWaterSettings()
        )
    }
    
    /**
     * Cleanup
     */
    fun cleanup() {
        stop()
        regionEnvironment.clear()
        parcelEnvironments.clear()
        dayCycles.clear()
        environmentChangeCallbacks.clear()
        currentEnvironment = null
        currentDayCycle = null
        scope.cancel()
        Log.i(TAG, "EEP manager cleaned up")
    }
}