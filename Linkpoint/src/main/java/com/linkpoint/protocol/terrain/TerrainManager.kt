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
        const val MAX_CACHED_TILES = 16 // Mobile memory safety limit
    }

    // Region dimensions in meters (default 256x256, up to 4096x4096 for varregions)
    var regionSizeX: Int = REGION_SIZE
        private set
    var regionSizeY: Int = REGION_SIZE
        private set

    val regionWidth: Int
        get() = regionSizeX

    val regionHeight: Int
        get() = regionSizeY

    // Currently active 256m virtual tile
    var activeTileIndex: VirtualTileIndex = VirtualTileIndex(0, 0)
        private set

    // Virtual tile heightmaps cache: VirtualTileIndex -> FloatArray(256x256)
    private val virtualTileCache = ConcurrentHashMap<VirtualTileIndex, FloatArray>()

    // Full heightmap allocated as (RegionSizeX + 1) * (RegionSizeY + 1)
    private var heightMap = FloatArray((REGION_SIZE + 1) * (REGION_SIZE + 1))

    // Track which patches are valid
    private val validPatches = ConcurrentHashMap<Int, Boolean>()

    // Water height from RegionHandshake
    var waterHeight: Float = DEFAULT_WATER_HEIGHT
        private set

    // Terrain renderer reference (set when rendering is ready)
    private var terrainRenderer: TerrainRenderer? = null

    // Count of valid patches received across active virtual tile/region
    var validPatchCount: Int = 0
        private set

    /**
     * Configure active region dimensions and reallocate heightmap.
     */
    fun setRegionSize(width: Int, height: Int) {
        val clampedW = width.coerceIn(REGION_SIZE, VirtualRegionMapper.MAX_REGION_SIZE)
        val clampedH = height.coerceIn(REGION_SIZE, VirtualRegionMapper.MAX_REGION_SIZE)
        if (regionSizeX != clampedW || regionSizeY != clampedH) {
            regionSizeX = clampedW
            regionSizeY = clampedH
            heightMap = FloatArray((regionSizeX + 1) * (regionSizeY + 1))
            validPatches.clear()
            virtualTileCache.clear()
            validPatchCount = 0
            Log.i(TAG, "Configured region dimensions: ${regionSizeX}x${regionSizeY}")
            terrainRenderer?.setRegionSize(regionSizeX.toFloat(), regionSizeY.toFloat())
        }
    }

    /**
     * Set active virtual 256m tile for rendering and interaction.
     */
    fun setActiveTile(tileX: Int, tileY: Int) {
        val newIndex = VirtualTileIndex(tileX, tileY)
        if (activeTileIndex != newIndex) {
            activeTileIndex = newIndex
            Log.d(TAG, "Active virtual tile changed to (${tileX}, ${tileY})")
            updateRendererHeightmap()
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

        val virtualTiles = VirtualRegionMapper.splitTerrainPatchesToVirtualTiles(
            result.patches,
            regionWidth,
            regionHeight
        )

        for ((tileIndex, tilePacket) in virtualTiles) {
            // Evict LRU/distant tile if cache size exceeds limit
            if (virtualTileCache.size >= MAX_CACHED_TILES && !virtualTileCache.containsKey(tileIndex)) {
                val tileToRemove = virtualTileCache.keys.maxByOrNull {
                    kotlin.math.abs(it.x - activeTileIndex.x) + kotlin.math.abs(it.y - activeTileIndex.y)
                }
                if (tileToRemove != null && tileToRemove != activeTileIndex) {
                    virtualTileCache.remove(tileToRemove)
                }
            }

            val tileHeights = virtualTileCache.getOrPut(tileIndex) { FloatArray(REGION_SIZE * REGION_SIZE) }

            for (reindexedPatch in tilePacket.patches) {
                applyPatchToBuffer(reindexedPatch, tileHeights)
            }
        }

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
     * Apply a single patch to the full region heightmap.
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
     * Apply a re-indexed patch (0..15 range) to a target 256x256 heightmap buffer.
     */
    private fun applyPatchToBuffer(patch: TerrainPatch, buffer: FloatArray) {
        val baseX = patch.x * PATCH_SIZE
        val baseY = patch.y * PATCH_SIZE

        for (y in 0 until PATCH_SIZE) {
            val localY = baseY + y
            if (localY >= REGION_SIZE) continue

            for (x in 0 until PATCH_SIZE) {
                val localX = baseX + x
                if (localX >= REGION_SIZE) continue

                val patchIdx = y * PATCH_SIZE + x
                val bufferIdx = localY * REGION_SIZE + localX

                buffer[bufferIdx] = patch.heightMap[patchIdx]
            }
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
        regionSizeX = REGION_SIZE
        regionSizeY = REGION_SIZE
        heightMap = FloatArray((REGION_SIZE + 1) * (REGION_SIZE + 1))
        validPatches.clear()
        virtualTileCache.clear()
        activeTileIndex = VirtualTileIndex(0, 0)
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
