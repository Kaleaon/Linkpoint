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

    var regionSizeX: Int = REGION_SIZE
        private set
    var regionSizeY: Int = REGION_SIZE
        private set

    // Full heightmap allocated as (RegionSizeX + 1) * (RegionSizeY + 1)
    private var heightMap = FloatArray((REGION_SIZE + 1) * (REGION_SIZE + 1))

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
     * Configure active region dimensions and reallocate heightmap.
     */
    fun setRegionSize(width: Int, height: Int) {
        val clampedW = width.coerceIn(256, 2048)
        val clampedH = height.coerceIn(256, 2048)
        if (regionSizeX != clampedW || regionSizeY != clampedH) {
            regionSizeX = clampedW
            regionSizeY = clampedH
            heightMap = FloatArray((regionSizeX + 1) * (regionSizeY + 1))
            validPatches.clear()
            validPatchCount = 0
            Log.i(TAG, "Configured region dimensions: ${regionSizeX}x${regionSizeY}")
            terrainRenderer?.setRegionSize(regionSizeX.toFloat(), regionSizeY.toFloat())
        }
    }

    /**
     * Set the terrain renderer for visualization.
     */
    fun setTerrainRenderer(renderer: TerrainRenderer) {
        this.terrainRenderer = renderer
        renderer.setRegionSize(regionSizeX.toFloat(), regionSizeY.toFloat())
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

        val patchesPerSideX = regionSizeX / PATCH_SIZE
        val patchesPerSideY = regionSizeY / PATCH_SIZE

        var patchesUpdated = 0

        for (patch in result.patches) {
            if (patch.x < patchesPerSideX && patch.y < patchesPerSideY) {
                applyPatch(patch)
                patchesUpdated++
            }
        }

        if (patchesUpdated > 0) {
            val totalPatches = patchesPerSideX * patchesPerSideY
            Log.d(TAG, "Applied $patchesUpdated terrain patches, total valid: $validPatchCount/$totalPatches")
            updateRendererHeightmap()
        }
    }

    /**
     * Apply a single patch to the heightmap.
     */
    private fun applyPatch(patch: TerrainPatch) {
        val baseX = patch.x * PATCH_SIZE
        val baseY = patch.y * PATCH_SIZE
        val stride = regionSizeX + 1
        val patchesPerSideX = regionSizeX / PATCH_SIZE
        val patchesPerSideY = regionSizeY / PATCH_SIZE

        for (y in 0 until PATCH_SIZE) {
            val globalY = baseY + y
            if (globalY >= regionSizeY) continue

            for (x in 0 until PATCH_SIZE) {
                val globalX = baseX + x
                if (globalX >= regionSizeX) continue

                val patchIdx = y * PATCH_SIZE + x
                val globalIdx = globalY * stride + globalX

                heightMap[globalIdx] = patch.heightMap[patchIdx]
            }
        }

        // Fill boundary edge +1 vertices for adjacent rendering quads
        if (patch.x == patchesPerSideX - 1) {
            for (y in 0 until PATCH_SIZE) {
                val globalY = baseY + y
                if (globalY < regionSizeY) {
                    val patchIdx = y * PATCH_SIZE + (PATCH_SIZE - 1)
                    val globalIdx = globalY * stride + regionSizeX
                    heightMap[globalIdx] = patch.heightMap[patchIdx]
                }
            }
        }

        if (patch.y == patchesPerSideY - 1) {
            for (x in 0 until PATCH_SIZE) {
                val globalX = baseX + x
                if (globalX < regionSizeX) {
                    val patchIdx = (PATCH_SIZE - 1) * PATCH_SIZE + x
                    val globalIdx = regionSizeY * stride + globalX
                    heightMap[globalIdx] = patch.heightMap[patchIdx]
                }
            }
        }

        if (patch.x == patchesPerSideX - 1 && patch.y == patchesPerSideY - 1) {
            val patchIdx = (PATCH_SIZE - 1) * PATCH_SIZE + (PATCH_SIZE - 1)
            val globalIdx = regionSizeY * stride + regionSizeX
            heightMap[globalIdx] = patch.heightMap[patchIdx]
        }

        // Mark patch as valid
        val patchKey = patch.y * patchesPerSideX + patch.x
        if (validPatches.put(patchKey, true) == null) {
            validPatchCount++
        }
    }

    /**
     * Push heightmap to renderer.
     */
    private fun updateRendererHeightmap() {
        terrainRenderer?.let { renderer ->
            renderer.setHeightmap(heightMap, regionSizeX, regionSizeY)
        }
    }

    /**
     * Get height at specific coordinates.
     */
    fun getHeightAt(x: Int, y: Int): Float {
        val clampedX = x.coerceIn(0, regionSizeX)
        val clampedY = y.coerceIn(0, regionSizeY)
        val stride = regionSizeX + 1
        return heightMap[clampedY * stride + clampedX]
    }

    /**
     * Check if terrain is fully loaded.
     */
    fun isFullyLoaded(): Boolean {
        val totalPatches = (regionSizeX / PATCH_SIZE) * (regionSizeY / PATCH_SIZE)
        return validPatchCount >= totalPatches
    }

    /**
     * Get load percentage.
     */
    fun getLoadPercentage(): Float {
        val totalPatches = (regionSizeX / PATCH_SIZE) * (regionSizeY / PATCH_SIZE)
        if (totalPatches == 0) return 0f
        return (validPatchCount.toFloat() / totalPatches) * 100f
    }

    /**
     * Reset terrain data (e.g., when changing regions).
     */
    fun reset() {
        heightMap.fill(0f)
        validPatches.clear()
        validPatchCount = 0
        waterHeight = DEFAULT_WATER_HEIGHT
        Log.i(TAG, "Terrain data reset (${regionSizeX}x${regionSizeY})")
    }

    /**
     * Get debug info string.
     */
    fun getDebugInfo(): String {
        val totalPatches = (regionSizeX / PATCH_SIZE) * (regionSizeY / PATCH_SIZE)
        return "Terrain: ${validPatchCount}/${totalPatches} patches (${getLoadPercentage().toInt()}%), water=$waterHeight"
    }
}
