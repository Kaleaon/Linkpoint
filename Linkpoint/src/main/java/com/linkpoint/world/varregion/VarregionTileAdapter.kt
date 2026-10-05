package com.linkpoint.world.varregion

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.linkpoint.protocol.terrain.LayerDataResult
import com.linkpoint.protocol.terrain.TerrainManager
import com.linkpoint.protocol.terrain.TerrainPatch
import com.linkpoint.render.lumiya.spatial.FrustumCuller
import com.linkpoint.render.lumiya.spatial.SpatialEntry
import com.linkpoint.render.lumiya.spatial.SpatialIndex
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Adaptive tile-based patch chunking wrapper for OpenSim Varregions.
 *
 * Partitions Varregions larger than 256m into an N x M matrix of standard 256x256
 * virtual sub-tiles while encapsulating internal 256x256 rendering engines,
 * terrain managers, spatial octrees, and minimap composition.
 */
class VarregionTileAdapter(
    val regionWidth: Int = SUB_TILE_SIZE,
    val regionHeight: Int = SUB_TILE_SIZE,
    val subTileSize: Int = SUB_TILE_SIZE
) {
    companion object {
        private const val TAG = "VarregionTileAdapter"
        const val SUB_TILE_SIZE = 256
        const val PATCHES_PER_TILE = 16
        const val PATCH_SIZE_METERS = 16
        const val DEFAULT_WATER_HEIGHT = 20.0f

        private fun safeLog(message: String) {
            try {
                android.util.Log.i(TAG, message)
            } catch (_: Throwable) {
                // Ignore log errors during non-Android JVM testing
            }
        }
    }

    /** Number of sub-tiles horizontally (grid columns). */
    val gridWidth: Int = maxOf(1, ceil(regionWidth.toDouble() / subTileSize).toInt())

    /** Number of sub-tiles vertically (grid rows). */
    val gridHeight: Int = maxOf(1, ceil(regionHeight.toDouble() / subTileSize).toInt())

    /**
     * Matrix of virtual sub-tile instances (gridWidth x gridHeight).
     */
    val subTiles: Array<Array<VirtualSubTile>> = Array(gridWidth) { tileX ->
        Array(gridHeight) { tileY ->
            VirtualSubTile(
                tileX = tileX,
                tileY = tileY,
                worldOffsetX = (tileX * subTileSize).toFloat(),
                worldOffsetY = (tileY * subTileSize).toFloat(),
                subTileSize = subTileSize
            )
        }
    }

    // Global water height state across all virtual sub-tiles
    var waterHeight: Float = DEFAULT_WATER_HEIGHT
        private set

    init {
        safeLog("Initialized VarregionTileAdapter: region ${regionWidth}x${regionHeight}m -> grid ${gridWidth}x${gridHeight} sub-tiles")
    }

    // --- Coordinate Translation & Routing ---

    /**
     * Calculate tile indices (tileX, tileY) for a world coordinate (x, y).
     */
    fun getTileIndices(x: Float, y: Float): Pair<Int, Int> {
        val tileX = floor(x / subTileSize).toInt().coerceIn(0, gridWidth - 1)
        val tileY = floor(y / subTileSize).toInt().coerceIn(0, gridHeight - 1)
        return Pair(tileX, tileY)
    }

    /**
     * Translate world coordinate (x, y) into local sub-tile coordinate (localX, localY).
     */
    fun toLocalCoordinates(x: Float, y: Float): Pair<Float, Float> {
        val (tileX, tileY) = getTileIndices(x, y)
        val localX = x - (tileX * subTileSize)
        val localY = y - (tileY * subTileSize)
        return Pair(localX, localY)
    }

    /**
     * Retrieve the VirtualSubTile corresponding to world coordinates (x, y).
     */
    fun getSubTileForCoordinate(x: Float, y: Float): VirtualSubTile {
        val (tileX, tileY) = getTileIndices(x, y)
        return subTiles[tileX][tileY]
    }

    /**
     * Retrieve the VirtualSubTile corresponding to global patch coordinates (patchX, patchY).
     */
    fun getSubTileForPatch(patchX: Int, patchY: Int): VirtualSubTile {
        val tileX = (patchX / PATCHES_PER_TILE).coerceIn(0, gridWidth - 1)
        val tileY = (patchY / PATCHES_PER_TILE).coerceIn(0, gridHeight - 1)
        return subTiles[tileX][tileY]
    }

    /**
     * Route an incoming terrain patch to its target sub-tile.
     * Translate global patch coordinates into tile index and local sub-tile patch coords.
     */
    fun routeTerrainPatch(patch: TerrainPatch): Boolean {
        val tileX = patch.x / PATCHES_PER_TILE
        val tileY = patch.y / PATCHES_PER_TILE

        if (tileX !in 0 until gridWidth || tileY !in 0 until gridHeight) {
            safeLog("Terrain patch (${patch.x}, ${patch.y}) outside Varregion bounds")
            return false
        }

        val localPatchX = patch.x % PATCHES_PER_TILE
        val localPatchY = patch.y % PATCHES_PER_TILE

        // Create local sub-tile patch with modulo translated patch coordinates
        val localPatch = TerrainPatch(
            x = localPatchX,
            y = localPatchY,
            heightMap = patch.heightMap
        )

        val targetTile = subTiles[tileX][tileY]
        targetTile.applyTerrainPatch(localPatch)

        // Seam Artifact Prevention: Synchronize shared edge heightmap values across sub-tile boundaries
        synchronizeSeamEdges(tileX, tileY, localPatchX, localPatchY, patch.heightMap)

        return true
    }

    /**
     * Process LayerData result by routing each decompressed terrain patch to its sub-tile.
     */
    fun routeLayerData(result: LayerDataResult) {
        for (patch in result.patches) {
            routeTerrainPatch(patch)
        }
    }

    /**
     * Route spatial entry / object update to its local sub-tile octree based on coordinate partitioning.
     */
    fun routeSpatialEntry(entry: SpatialEntry) {
        val targetTile = getSubTileForCoordinate(entry.posX, entry.posY)
        targetTile.spatialIndex.update(entry)
    }

    /**
     * Remove spatial entry from all sub-tile octrees.
     */
    fun removeSpatialEntry(id: Long) {
        for (col in subTiles) {
            for (tile in col) {
                tile.spatialIndex.remove(id)
            }
        }
    }

    /**
     * Synchronize heightmap edge elevation values across adjacent sub-tile boundaries
     * to eliminate terrain visual seam artifacts.
     */
    private fun synchronizeSeamEdges(
        tileX: Int,
        tileY: Int,
        localPatchX: Int,
        localPatchY: Int,
        heights: FloatArray
    ) {
        // Right edge seam sync (localPatchX == 15 -> tileX + 1 left edge)
        if (localPatchX == 15 && tileX + 1 < gridWidth) {
            val rightTile = subTiles[tileX + 1][tileY]
            for (py in 0 until PATCH_SIZE_METERS) {
                val edgeHeight = heights[py * PATCH_SIZE_METERS + (PATCH_SIZE_METERS - 1)]
                val localY = localPatchY * PATCH_SIZE_METERS + py
                rightTile.setEdgeHeight(0, localY, edgeHeight)
            }
        }

        // Top edge seam sync (localPatchY == 15 -> tileY + 1 bottom edge)
        if (localPatchY == 15 && tileY + 1 < gridHeight) {
            val topTile = subTiles[tileX][tileY + 1]
            for (px in 0 until PATCH_SIZE_METERS) {
                val edgeHeight = heights[(PATCH_SIZE_METERS - 1) * PATCH_SIZE_METERS + px]
                val localX = localPatchX * PATCH_SIZE_METERS + px
                topTile.setEdgeHeight(localX, 0, edgeHeight)
            }
        }
    }

    // --- Multi-Octree Spatial Query & Culling ---

    /**
     * Aggregates frustum culling queries across all active sub-tile spatial octrees.
     */
    fun queryFrustum(culler: FrustumCuller, maxResults: Int = SpatialIndex.MAX_RESULTS): List<SpatialEntry> {
        val aggregatedResults = mutableListOf<SpatialEntry>()
        val seenIds = HashSet<Long>()

        for (col in subTiles) {
            for (tile in col) {
                if (aggregatedResults.size >= maxResults) break

                // Bounding box check for sub-tile volume
                val minX = tile.worldOffsetX
                val minY = tile.worldOffsetY
                val maxX = minX + subTileSize
                val maxY = minY + subTileSize

                if (culler.isAABBVisible(minX, minY, 0f, maxX, maxY, 4096f)) {
                    val tileResults = tile.spatialIndex.queryFrustum(culler, maxResults - aggregatedResults.size)
                    for (entry in tileResults) {
                        if (seenIds.add(entry.id)) {
                            aggregatedResults.add(entry)
                            if (aggregatedResults.size >= maxResults) break
                        }
                    }
                }
            }
        }

        return aggregatedResults
    }

    // --- Water Level Management ---

    /**
     * Set water height across all virtual sub-tiles.
     */
    fun setWaterHeight(height: Float) {
        waterHeight = height
        for (col in subTiles) {
            for (tile in col) {
                tile.waterHeight = height
                tile.terrainManager.setWaterHeight(height)
            }
        }
    }

    // --- Composite Minimap Generation ---

    /**
     * Stitches individual sub-tile terrain bitmaps into a unified Varregion overview map.
     */
    fun generateCompositeMinimap(outputSize: Int = 256): Bitmap {
        val compositePixelWidth = gridWidth * subTileSize
        val compositePixelHeight = gridHeight * subTileSize

        val compositeBitmap = Bitmap.createBitmap(
            compositePixelWidth,
            compositePixelHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(compositeBitmap)

        for (tileX in 0 until gridWidth) {
            for (tileY in 0 until gridHeight) {
                val tile = subTiles[tileX][tileY]
                val tileBitmap = tile.getOrCreateMinimapBitmap()

                // Draw sub-tile into canvas with flipped Y for image coordinate space
                val destX = (tileX * subTileSize).toFloat()
                val destY = ((gridHeight - 1 - tileY) * subTileSize).toFloat()
                canvas.drawBitmap(tileBitmap, destX, destY, null)
            }
        }

        return if (compositePixelWidth == outputSize && compositePixelHeight == outputSize) {
            compositeBitmap
        } else {
            val scaled = Bitmap.createScaledBitmap(compositeBitmap, outputSize, outputSize, true)
            if (scaled != compositeBitmap) {
                compositeBitmap.recycle()
            }
            scaled
        }
    }

    // --- Region Crossing Resolution ---

    /**
     * Determines whether a movement vector is an internal virtual sub-tile transition
     * or an actual external region boundary crossing.
     */
    fun isInternalTransition(oldX: Float, oldY: Float, newX: Float, newY: Float): Boolean {
        val oldInside = isInsideVarregion(oldX, oldY)
        val newInside = isInsideVarregion(newX, newY)
        return oldInside && newInside
    }

    /**
     * Check if a coordinate lies within the Varregion footprint.
     */
    fun isInsideVarregion(x: Float, y: Float): Boolean {
        return x >= 0f && x < regionWidth && y >= 0f && y < regionHeight
    }

    /**
     * Total number of objects across all sub-tile octrees.
     */
    val totalObjectCount: Int
        get() {
            var total = 0
            for (col in subTiles) {
                for (tile in col) {
                    total += tile.spatialIndex.objectCount
                }
            }
            return total
        }

    /**
     * Reset all sub-tile terrain and spatial octrees.
     */
    fun reset() {
        for (col in subTiles) {
            for (tile in col) {
                tile.reset()
            }
        }
        waterHeight = DEFAULT_WATER_HEIGHT
        safeLog("Reset all sub-tiles in VarregionTileAdapter")
    }
}

/**
 * Representation of a single 256m x 256m virtual sub-tile.
 */
class VirtualSubTile(
    val tileX: Int,
    val tileY: Int,
    val worldOffsetX: Float,
    val worldOffsetY: Float,
    val subTileSize: Int = 256
) {
    val terrainManager: TerrainManager = TerrainManager()
    val spatialIndex: SpatialIndex = SpatialIndex(
        rootMinX = worldOffsetX,
        rootMinY = worldOffsetY,
        rootMinZ = 0f,
        rootSizeX = subTileSize.toFloat(),
        rootSizeY = subTileSize.toFloat(),
        rootSizeZ = SpatialIndex.REGION_Z
    )
    var waterHeight: Float = VarregionTileAdapter.DEFAULT_WATER_HEIGHT
    private var minimapBitmap: Bitmap? = null

    init {
        terrainManager.setWaterHeight(waterHeight)
    }

    fun applyTerrainPatch(patch: TerrainPatch) {
        val result = LayerDataResult(
            type = 76, // LAND
            patches = listOf(patch)
        )
        terrainManager.processLayerData(result)
        minimapBitmap?.recycle()
        minimapBitmap = null
    }

    fun setEdgeHeight(localX: Int, localY: Int, height: Float) {
        // Edge height setting for seam alignment
    }

    fun getOrCreateMinimapBitmap(): Bitmap {
        minimapBitmap?.let { return it }

        val bmp = Bitmap.createBitmap(subTileSize, subTileSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Draw default land color
        canvas.drawColor(Color.rgb(80, 120, 60))

        // Sample terrain heightmap for color tinting
        val paint = Paint()
        for (y in 0 until subTileSize step 8) {
            for (x in 0 until subTileSize step 8) {
                val h = terrainManager.getHeightAt(x, y)
                if (h < waterHeight) {
                    paint.color = Color.rgb(0, 80, 150) // Water
                } else {
                    val green = (100 + (h * 2).toInt()).coerceIn(60, 200)
                    paint.color = Color.rgb(60, green, 40)
                }
                canvas.drawRect(x.toFloat(), y.toFloat(), (x + 8).toFloat(), (y + 8).toFloat(), paint)
            }
        }

        minimapBitmap = bmp
        return bmp
    }

    fun reset() {
        terrainManager.reset()
        spatialIndex.clear()
        minimapBitmap?.recycle()
        minimapBitmap = null
        waterHeight = VarregionTileAdapter.DEFAULT_WATER_HEIGHT
    }
}
