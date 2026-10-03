package com.linkpoint.protocol.terrain

import android.util.Log
import com.linkpoint.render.terrain.TerrainRenderer
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages terrain data from the Second Life protocol.
 * 
 * Receives LayerData terrain patches and coordinates updates
 * to the TerrainRenderer.
 */
class TerrainManager {
    
    companion object {
        private const val TAG = "TerrainManager"
        
        const val REGION_SIZE = 256
        const val PATCH_SIZE = 16
        const val PATCHES_PER_SIDE = 16
        const val DEFAULT_WATER_HEIGHT = 20.0f
    }
    
    // Full heightmap (256x256)
    private val heightMap = FloatArray(REGION_SIZE * REGION_SIZE)
    
    // Track which patches are valid
    private val validPatches = ConcurrentHashMap<Int, Boolean>()
    
    // Water height from RegionHandshake
    var waterHeight: Float = DEFAULT_WATER_HEIGHT
        private set
    
    // Terrain renderer reference (set when rendering is ready)
    private var terrainRenderer: TerrainRenderer? = null
    
    // Count of valid patches received
    var validPatchCount: Int = 0
        private set
    
    /**
     * Set the terrain renderer for visualization.
     */
    fun setTerrainRenderer(renderer: TerrainRenderer) {
        this.terrainRenderer = renderer
        Log.i(TAG, "TerrainRenderer connected")
        
        // Push any existing terrain data to renderer
        if (validPatchCount > 0) {
            updateRendererHeightmap()
        }
    }
    
    /**
     * Update water height from RegionHandshake.
     */
    fun setWaterHeight(height: Float) {
        if (waterHeight != height) {
            waterHeight = height
            Log.d(TAG, "Water height set to: $height")
        }
    }
    
    /**
     * Process LayerData containing terrain patches.
     */
    fun processLayerData(result: LayerDataResult) {
        if (result.type != LayerType.LAND && result.type != LayerType.LAND_EXTENDED) {
            return
        }
        
        var patchesUpdated = 0
        
        for (patch in result.patches) {
            if (patch.x < PATCHES_PER_SIDE && patch.y < PATCHES_PER_SIDE) {
                applyPatch(patch)
                patchesUpdated++
            }
        }
        
        if (patchesUpdated > 0) {
            Log.d(TAG, "Applied $patchesUpdated terrain patches, total valid: $validPatchCount")
            updateRendererHeightmap()
        }
    }
    
    /**
     * Apply a single patch to the heightmap.
     */
    private fun applyPatch(patch: TerrainPatch) {
        val baseX = patch.x * PATCH_SIZE
        val baseY = patch.y * PATCH_SIZE
        
        for (y in 0 until PATCH_SIZE) {
            val globalY = baseY + y
            if (globalY >= REGION_SIZE) continue
            
            for (x in 0 until PATCH_SIZE) {
                val globalX = baseX + x
                if (globalX >= REGION_SIZE) continue
                
                val patchIdx = y * PATCH_SIZE + x
                val globalIdx = globalY * REGION_SIZE + globalX
                
                heightMap[globalIdx] = patch.heightMap[patchIdx]
            }
        }
        
        // Mark patch as valid
        val patchKey = patch.y * PATCHES_PER_SIDE + patch.x
        if (validPatches.put(patchKey, true) == null) {
            validPatchCount++
        }
    }
    
    /**
     * Push heightmap to renderer.
     */
    private fun updateRendererHeightmap() {
        terrainRenderer?.let { renderer ->
            // Create 257x257 heightmap for renderer (includes edge vertices)
            val rendererHeights = FloatArray(257 * 257)
            
            for (y in 0..256) {
                for (x in 0..256) {
                    val srcX = x.coerceIn(0, 255)
                    val srcY = y.coerceIn(0, 255)
                    rendererHeights[y * 257 + x] = heightMap[srcY * 256 + srcX]
                }
            }
            
            renderer.setHeightmap(rendererHeights)
        }
    }
    
    /**
     * Get height at specific coordinates.
     */
    fun getHeightAt(x: Int, y: Int): Float {
        val clampedX = x.coerceIn(0, REGION_SIZE - 1)
        val clampedY = y.coerceIn(0, REGION_SIZE - 1)
        return heightMap[clampedY * REGION_SIZE + clampedX]
    }
    
    /**
     * Check if terrain is fully loaded.
     */
    fun isFullyLoaded(): Boolean {
        return validPatchCount >= PATCHES_PER_SIDE * PATCHES_PER_SIDE
    }
    
    /**
     * Get load percentage.
     */
    fun getLoadPercentage(): Float {
        return (validPatchCount.toFloat() / (PATCHES_PER_SIDE * PATCHES_PER_SIDE)) * 100f
    }
    
    /**
     * Reset terrain data (e.g., when changing regions).
     */
    fun reset() {
        heightMap.fill(0f)
        validPatches.clear()
        validPatchCount = 0
        waterHeight = DEFAULT_WATER_HEIGHT
        Log.i(TAG, "Terrain data reset")
    }
    
    /**
     * Get debug info string.
     */
    fun getDebugInfo(): String {
        return "Terrain: ${validPatchCount}/${PATCHES_PER_SIDE * PATCHES_PER_SIDE} patches (${getLoadPercentage().toInt()}%), water=$waterHeight"
    }
}
