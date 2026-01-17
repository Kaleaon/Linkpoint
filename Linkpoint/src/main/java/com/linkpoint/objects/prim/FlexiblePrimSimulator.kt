package com.linkpoint.objects.prim

import android.util.Log
import kotlin.math.*

/**
 * Flexible Prim System - Handles flexible/flexi prims physics simulation.
 * 
 * Based on Lumiya's FlexiblePrim.java
 * 
 * Flexi prims are prims that bend and sway with physics simulation.
 * Used for: hair, tails, flags, curtains, etc.
 * 
 * Parameters:
 * - Softness: How easily the prim bends
 * - Gravity: Downward force
 * - Wind: Response to environment wind
 * - Tension: How quickly it returns to rest
 * - Drag: Resistance to movement
 */
class FlexiblePrimSimulator {
    
    companion object {
        private const val TAG = "FlexiblePrim"
        
        // Simulation constants
        const val NUM_SECTIONS = 8
        const val TIME_STEP = 1f / 60f // 60 FPS physics
        const val MAX_VELOCITY = 10f
        
        // Default parameters
        const val DEFAULT_SOFTNESS = 2
        const val DEFAULT_GRAVITY = 0.3f
        const val DEFAULT_WIND = 0f
        const val DEFAULT_TENSION = 1f
        const val DEFAULT_DRAG = 0.2f
    }
    
    // Active flexi prims
    private val flexiPrims = mutableMapOf<Int, FlexiState>()
    
    // Environment wind
    private var windX = 0f
    private var windY = 0f
    private var windZ = 0f
    
    /**
     * Register a flexible prim.
     */
    fun registerFlexiPrim(
        localId: Int,
        params: FlexiParams,
        basePosition: FloatArray,
        baseRotation: FloatArray,
        length: Float
    ) {
        val sections = Array(NUM_SECTIONS) { i ->
            val t = (i + 1).toFloat() / NUM_SECTIONS
            FlexiSection(
                position = floatArrayOf(
                    basePosition[0],
                    basePosition[1],
                    basePosition[2] + length * t
                ),
                velocity = floatArrayOf(0f, 0f, 0f),
                restPosition = floatArrayOf(
                    basePosition[0],
                    basePosition[1],
                    basePosition[2] + length * t
                )
            )
        }
        
        flexiPrims[localId] = FlexiState(
            localId = localId,
            params = params,
            basePosition = basePosition.copyOf(),
            baseRotation = baseRotation.copyOf(),
            length = length,
            sections = sections
        )
        
        Log.d(TAG, "Registered flexi prim $localId with ${params.softness} softness")
    }
    
    /**
     * Unregister a flexible prim.
     */
    fun unregisterFlexiPrim(localId: Int) {
        flexiPrims.remove(localId)
    }
    
    /**
     * Update base position (when prim moves).
     */
    fun updateBasePosition(localId: Int, position: FloatArray, rotation: FloatArray) {
        flexiPrims[localId]?.let { state ->
            state.basePosition = position.copyOf()
            state.baseRotation = rotation.copyOf()
            
            // Update rest positions based on new base
            val length = state.length
            for (i in state.sections.indices) {
                val t = (i + 1).toFloat() / NUM_SECTIONS
                val local = floatArrayOf(0f, 0f, length * t)
                val world = rotateVector(local, rotation)
                state.sections[i].restPosition = floatArrayOf(
                    position[0] + world[0],
                    position[1] + world[1],
                    position[2] + world[2]
                )
            }
        }
    }
    
    /**
     * Set environment wind.
     */
    fun setWind(x: Float, y: Float, z: Float) {
        windX = x
        windY = y
        windZ = z
    }
    
    /**
     * Simulate one frame.
     */
    fun simulate(deltaTime: Float) {
        val dt = deltaTime.coerceAtMost(TIME_STEP * 2) // Cap delta time
        
        for ((_, state) in flexiPrims) {
            simulatePrim(state, dt)
        }
    }
    
    private fun simulatePrim(state: FlexiState, dt: Float) {
        val params = state.params
        val sections = state.sections
        
        // Calculate softness factor (higher = more segments affected)
        val softnessRadius = params.softness.coerceIn(0, 3) + 1
        
        for (i in sections.indices) {
            val section = sections[i]
            val t = (i + 1).toFloat() / NUM_SECTIONS
            
            // Skip sections within softness radius of base
            if (i < (NUM_SECTIONS - softnessRadius - 1)) continue
            
            // Calculate forces
            var fx = 0f
            var fy = 0f
            var fz = 0f
            
            // Gravity
            fz -= params.gravity * 9.8f * t
            
            // Wind
            fx += windX * params.wind * t
            fy += windY * params.wind * t
            fz += windZ * params.wind * t
            
            // Tension (spring force toward rest position)
            val dx = section.restPosition[0] - section.position[0]
            val dy = section.restPosition[1] - section.position[1]
            val dz = section.restPosition[2] - section.position[2]
            
            fx += dx * params.tension * (1f - t * 0.5f)
            fy += dy * params.tension * (1f - t * 0.5f)
            fz += dz * params.tension * (1f - t * 0.5f)
            
            // Drag
            fx -= section.velocity[0] * params.drag
            fy -= section.velocity[1] * params.drag
            fz -= section.velocity[2] * params.drag
            
            // Chain constraint (pull toward previous section)
            if (i > 0) {
                val prev = sections[i - 1]
                val segmentLength = state.length / NUM_SECTIONS
                
                val cdx = prev.position[0] - section.position[0]
                val cdy = prev.position[1] - section.position[1]
                val cdz = prev.position[2] - section.position[2]
                val dist = sqrt(cdx*cdx + cdy*cdy + cdz*cdz)
                
                if (dist > segmentLength * 1.1f) {
                    val pull = (dist - segmentLength) * 0.5f
                    fx += (cdx / dist) * pull
                    fy += (cdy / dist) * pull
                    fz += (cdz / dist) * pull
                }
            } else {
                // First section constrained to base
                val cdx = state.basePosition[0] - section.position[0]
                val cdy = state.basePosition[1] - section.position[1]
                val cdz = state.basePosition[2] - section.position[2]
                val dist = sqrt(cdx*cdx + cdy*cdy + cdz*cdz)
                val segmentLength = state.length / NUM_SECTIONS
                
                if (dist > segmentLength * 1.1f) {
                    val pull = (dist - segmentLength) * 2f
                    fx += (cdx / dist) * pull
                    fy += (cdy / dist) * pull
                    fz += (cdz / dist) * pull
                }
            }
            
            // Update velocity
            section.velocity[0] += fx * dt
            section.velocity[1] += fy * dt
            section.velocity[2] += fz * dt
            
            // Clamp velocity
            val speed = sqrt(
                section.velocity[0] * section.velocity[0] +
                section.velocity[1] * section.velocity[1] +
                section.velocity[2] * section.velocity[2]
            )
            if (speed > MAX_VELOCITY) {
                val scale = MAX_VELOCITY / speed
                section.velocity[0] *= scale
                section.velocity[1] *= scale
                section.velocity[2] *= scale
            }
            
            // Update position
            section.position[0] += section.velocity[0] * dt
            section.position[1] += section.velocity[1] * dt
            section.position[2] += section.velocity[2] * dt
        }
    }
    
    private fun rotateVector(v: FloatArray, q: FloatArray): FloatArray {
        // Quaternion rotation
        val qx = q[0]
        val qy = q[1]
        val qz = q[2]
        val qw = q[3]
        
        val vx = v[0]
        val vy = v[1]
        val vz = v[2]
        
        val tx = 2 * (qy * vz - qz * vy)
        val ty = 2 * (qz * vx - qx * vz)
        val tz = 2 * (qx * vy - qy * vx)
        
        return floatArrayOf(
            vx + qw * tx + qy * tz - qz * ty,
            vy + qw * ty + qz * tx - qx * tz,
            vz + qw * tz + qx * ty - qy * tx
        )
    }
    
    /**
     * Get section positions for rendering.
     */
    fun getSectionPositions(localId: Int): List<FloatArray>? {
        return flexiPrims[localId]?.sections?.map { it.position.copyOf() }
    }
    
    /**
     * Get all flexi prim data for rendering.
     */
    fun getAllFlexiData(): Map<Int, List<FloatArray>> {
        return flexiPrims.mapValues { (_, state) ->
            listOf(state.basePosition.copyOf()) + state.sections.map { it.position.copyOf() }
        }
    }
    
    /**
     * Clear all flexi prims.
     */
    fun clear() {
        flexiPrims.clear()
    }
}

/**
 * Flexi prim parameters.
 */
data class FlexiParams(
    val softness: Int = DEFAULT_SOFTNESS,
    val gravity: Float = DEFAULT_GRAVITY,
    val wind: Float = DEFAULT_WIND,
    val tension: Float = DEFAULT_TENSION,
    val drag: Float = DEFAULT_DRAG
) {
    companion object {
        private const val DEFAULT_SOFTNESS = 2
        private const val DEFAULT_GRAVITY = 0.3f
        private const val DEFAULT_WIND = 0f
        private const val DEFAULT_TENSION = 1f
        private const val DEFAULT_DRAG = 0.2f
    }
}

/**
 * State of a flexible prim.
 */
data class FlexiState(
    val localId: Int,
    val params: FlexiParams,
    var basePosition: FloatArray,
    var baseRotation: FloatArray,
    val length: Float,
    val sections: Array<FlexiSection>
)

/**
 * A single section of a flexible prim.
 */
data class FlexiSection(
    val position: FloatArray,
    val velocity: FloatArray,
    var restPosition: FloatArray
)
