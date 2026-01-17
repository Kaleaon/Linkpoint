package com.linkpoint.render

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Draw Distance Manager - Controls object visibility based on distance.
 * 
 * Based on Lumiya's SLDrawDistance.java
 * 
 * Features:
 * - Dynamic draw distance based on scene complexity
 * - LOD (Level of Detail) management
 * - Object culling optimization
 * - Memory-based automatic adjustment
 * - Performance-based scaling
 */
class DrawDistanceManager {
    
    companion object {
        private const val TAG = "DrawDistanceManager"
        
        // Draw distance limits
        const val MIN_DRAW_DISTANCE = 32f
        const val MAX_DRAW_DISTANCE = 512f
        const val DEFAULT_DRAW_DISTANCE = 128f
        
        // LOD distances (relative to draw distance)
        const val LOD_HIGH_FACTOR = 0.25f      // High detail within 25%
        const val LOD_MEDIUM_FACTOR = 0.5f     // Medium detail 25-50%
        const val LOD_LOW_FACTOR = 0.75f       // Low detail 50-75%
        // Beyond 75% is lowest/billboard
        
        // Avatar draw distance multiplier (avatars visible further)
        const val AVATAR_DISTANCE_MULTIPLIER = 1.5f
        
        // Particle draw distance (shorter than objects)
        const val PARTICLE_DISTANCE_FACTOR = 0.5f
        
        // Performance thresholds
        const val TARGET_FPS = 30f
        const val FPS_TOLERANCE = 5f
    }
    
    // Current draw distance
    private val _drawDistance = MutableStateFlow(DEFAULT_DRAW_DISTANCE)
    val drawDistance: StateFlow<Float> = _drawDistance
    
    // LOD distances
    private val _lodDistances = MutableStateFlow(calculateLodDistances(DEFAULT_DRAW_DISTANCE))
    val lodDistances: StateFlow<LodDistances> = _lodDistances
    
    // Auto-adjust enabled
    var autoAdjustEnabled = true
    
    // Performance tracking
    private var lastFpsValues = ArrayDeque<Float>(10)
    private var lastMemoryValues = ArrayDeque<Long>(10)
    
    // Object counts for statistics
    private var visibleObjectCount = 0
    private var totalObjectCount = 0
    private var culledObjectCount = 0
    
    /**
     * Set draw distance manually.
     */
    fun setDrawDistance(distance: Float) {
        val clamped = distance.coerceIn(MIN_DRAW_DISTANCE, MAX_DRAW_DISTANCE)
        _drawDistance.value = clamped
        _lodDistances.value = calculateLodDistances(clamped)
        Log.d(TAG, "Draw distance set to $clamped")
    }
    
    /**
     * Get effective draw distance for avatars.
     */
    fun getAvatarDrawDistance(): Float {
        return (_drawDistance.value * AVATAR_DISTANCE_MULTIPLIER).coerceAtMost(MAX_DRAW_DISTANCE)
    }
    
    /**
     * Get effective draw distance for particles.
     */
    fun getParticleDrawDistance(): Float {
        return _drawDistance.value * PARTICLE_DISTANCE_FACTOR
    }
    
    /**
     * Check if an object should be visible.
     */
    fun isVisible(distanceSquared: Float): Boolean {
        val drawDist = _drawDistance.value
        return distanceSquared <= drawDist * drawDist
    }
    
    /**
     * Check if an avatar should be visible.
     */
    fun isAvatarVisible(distanceSquared: Float): Boolean {
        val avatarDist = getAvatarDrawDistance()
        return distanceSquared <= avatarDist * avatarDist
    }
    
    /**
     * Get LOD level for distance.
     */
    fun getLodLevel(distanceSquared: Float): LodLevel {
        val distance = kotlin.math.sqrt(distanceSquared)
        val lod = _lodDistances.value
        
        return when {
            distance <= lod.highDetail -> LodLevel.HIGH
            distance <= lod.mediumDetail -> LodLevel.MEDIUM
            distance <= lod.lowDetail -> LodLevel.LOW
            else -> LodLevel.LOWEST
        }
    }
    
    /**
     * Report frame performance for auto-adjustment.
     */
    fun reportFramePerformance(fps: Float, memoryUsedMb: Long) {
        if (!autoAdjustEnabled) return
        
        // Track recent values
        lastFpsValues.addLast(fps)
        if (lastFpsValues.size > 10) lastFpsValues.removeFirst()
        
        lastMemoryValues.addLast(memoryUsedMb)
        if (lastMemoryValues.size > 10) lastMemoryValues.removeFirst()
        
        // Only adjust after we have enough samples
        if (lastFpsValues.size < 10) return
        
        val avgFps = lastFpsValues.average().toFloat()
        val avgMemory = lastMemoryValues.average().toLong()
        
        adjustDrawDistance(avgFps, avgMemory)
    }
    
    private fun adjustDrawDistance(avgFps: Float, avgMemoryMb: Long) {
        val currentDistance = _drawDistance.value
        var newDistance = currentDistance
        
        // Reduce draw distance if FPS is too low
        if (avgFps < TARGET_FPS - FPS_TOLERANCE) {
            newDistance = (currentDistance * 0.9f).coerceAtLeast(MIN_DRAW_DISTANCE)
            Log.d(TAG, "Reducing draw distance due to low FPS ($avgFps)")
        }
        // Increase draw distance if FPS is good
        else if (avgFps > TARGET_FPS + FPS_TOLERANCE && currentDistance < MAX_DRAW_DISTANCE) {
            newDistance = (currentDistance * 1.05f).coerceAtMost(MAX_DRAW_DISTANCE)
        }
        
        // Also consider memory (reduce if using too much)
        val maxMemoryMb = Runtime.getRuntime().maxMemory() / (1024 * 1024)
        if (avgMemoryMb > maxMemoryMb * 0.8) {
            newDistance = (newDistance * 0.95f).coerceAtLeast(MIN_DRAW_DISTANCE)
            Log.d(TAG, "Reducing draw distance due to high memory ($avgMemoryMb MB)")
        }
        
        if (newDistance != currentDistance) {
            setDrawDistance(newDistance)
        }
    }
    
    /**
     * Report visibility statistics.
     */
    fun reportVisibilityStats(visible: Int, total: Int, culled: Int) {
        visibleObjectCount = visible
        totalObjectCount = total
        culledObjectCount = culled
    }
    
    /**
     * Get current statistics.
     */
    fun getStats(): DrawDistanceStats {
        return DrawDistanceStats(
            drawDistance = _drawDistance.value,
            avatarDistance = getAvatarDrawDistance(),
            particleDistance = getParticleDrawDistance(),
            visibleObjects = visibleObjectCount,
            totalObjects = totalObjectCount,
            culledObjects = culledObjectCount,
            lodDistances = _lodDistances.value
        )
    }
    
    private fun calculateLodDistances(drawDistance: Float): LodDistances {
        return LodDistances(
            highDetail = drawDistance * LOD_HIGH_FACTOR,
            mediumDetail = drawDistance * LOD_MEDIUM_FACTOR,
            lowDetail = drawDistance * LOD_LOW_FACTOR
        )
    }
}

/**
 * LOD distance thresholds.
 */
data class LodDistances(
    val highDetail: Float,
    val mediumDetail: Float,
    val lowDetail: Float
)

/**
 * LOD levels.
 */
enum class LodLevel {
    HIGH,
    MEDIUM,
    LOW,
    LOWEST
}

/**
 * Draw distance statistics.
 */
data class DrawDistanceStats(
    val drawDistance: Float,
    val avatarDistance: Float,
    val particleDistance: Float,
    val visibleObjects: Int,
    val totalObjects: Int,
    val culledObjects: Int,
    val lodDistances: LodDistances
)
