package com.linkpoint.protocol.terrain

import android.util.Log
import com.linkpoint.protocol.types.LLVector3

/**
 * Virtual 256m tile index grid coordinate.
 */
data class VirtualTileIndex(
    val x: Int,
    val y: Int
) {
    /**
     * Unique key identifier for caching.
     */
    val key: String get() = "${x}_${y}"
}

/**
 * Represents a 3D position mapped into active virtual 256m tile space.
 */
data class VirtualTilePosition(
    val tileIndex: VirtualTileIndex,
    val localX: Float,
    val localY: Float,
    val localZ: Float
) {
    /**
     * Converts local position back to global region space coordinates.
     */
    fun toGlobalVector(): LLVector3 {
        return VirtualRegionMapper.virtualTileToGlobal(tileIndex, localX, localY, localZ)
    }
}

/**
 * A collection of re-indexed terrain patches corresponding to a specific virtual 256m tile.
 */
data class VirtualTilePacket(
    val tileIndex: VirtualTileIndex,
    val patches: List<TerrainPatch>
)

/**
 * Client-side Virtual Quad-Grid Mapper for OpenSim Varregions.
 *
 * Translates spatial coordinates, terrain heightmaps, and avatar position vectors
 * between global OpenSim region space (up to 4096m x 4096m) and legacy 256m virtual tile space.
 */
object VirtualRegionMapper {
    private const val TAG = "VirtualRegionMapper"

    const val STANDARD_TILE_SIZE = 256
    const val STANDARD_PATCH_SIZE = 16
    const val PATCHES_PER_TILE_SIDE = 16
    const val MAX_REGION_SIZE = 4096
    const val MAX_PATCHES_PER_SIDE = MAX_REGION_SIZE / STANDARD_PATCH_SIZE // 256 patches

    /**
     * Maps global OpenSim region 3D coordinates into virtual 256m tile index and tile-local relative coordinates.
     *
     * @param globalX Global X position in meters (0 to regionWidth)
     * @param globalY Global Y position in meters (0 to regionHeight)
     * @param globalZ Global Z position in meters (altitude)
     * @param regionWidth Total region width in meters (e.g. 256, 512, 1024, 2048, 4096)
     * @param regionHeight Total region height in meters
     * @return VirtualTilePosition with active tile index and relative coordinates in [0, 256)
     */
    fun globalToVirtualTile(
        globalX: Float,
        globalY: Float,
        globalZ: Float,
        regionWidth: Int = STANDARD_TILE_SIZE,
        regionHeight: Int = STANDARD_TILE_SIZE
    ): VirtualTilePosition {
        val maxTilesX = (regionWidth / STANDARD_TILE_SIZE).coerceAtLeast(1)
        val maxTilesY = (regionHeight / STANDARD_TILE_SIZE).coerceAtLeast(1)

        val tileX = (globalX / STANDARD_TILE_SIZE).toInt().coerceIn(0, maxTilesX - 1)
        val tileY = (globalY / STANDARD_TILE_SIZE).toInt().coerceIn(0, maxTilesY - 1)

        val localX = globalX - (tileX * STANDARD_TILE_SIZE)
        val localY = globalY - (tileY * STANDARD_TILE_SIZE)

        return VirtualTilePosition(
            tileIndex = VirtualTileIndex(tileX, tileY),
            localX = localX,
            localY = localY,
            localZ = globalZ
        )
    }

    /**
     * Converts a tile-local 3D coordinate back to global OpenSim region space.
     *
     * @param tileIndex Virtual tile index
     * @param localX Local tile X position in meters [0, 256)
     * @param localY Local tile Y position in meters [0, 256)
     * @param localZ Altitude in meters
     * @return Global 3D vector
     */
    fun virtualTileToGlobal(
        tileIndex: VirtualTileIndex,
        localX: Float,
        localY: Float,
        localZ: Float
    ): LLVector3 {
        val globalX = (tileIndex.x * STANDARD_TILE_SIZE) + localX
        val globalY = (tileIndex.y * STANDARD_TILE_SIZE) + localY
        return LLVector3(globalX, globalY, localZ)
    }

    /**
     * Calculates the global origin offset in meters for a given virtual tile.
     */
    fun getActiveTileOffset(tileIndex: VirtualTileIndex): Pair<Float, Float> {
        return Pair(
            (tileIndex.x * STANDARD_TILE_SIZE).toFloat(),
            (tileIndex.y * STANDARD_TILE_SIZE).toFloat()
        )
    }

    /**
     * Re-indexes an individual terrain patch into a virtual 256m tile index and
     * a local terrain patch within standard 0..15 coordinate ranges.
     *
     * @param patch Raw terrain patch with global region patch coordinates
     * @return Pair containing VirtualTileIndex and re-indexed TerrainPatch
     */
    fun mapPatchToVirtualTile(patch: TerrainPatch): Pair<VirtualTileIndex, TerrainPatch> {
        val tileX = patch.x / PATCHES_PER_TILE_SIDE
        val tileY = patch.y / PATCHES_PER_TILE_SIDE

        val localPatchX = patch.x % PATCHES_PER_TILE_SIDE
        val localPatchY = patch.y % PATCHES_PER_TILE_SIDE

        val reindexedPatch = TerrainPatch(
            x = localPatchX,
            y = localPatchY,
            heightMap = patch.heightMap
        )

        return Pair(VirtualTileIndex(tileX, tileY), reindexedPatch)
    }

    /**
     * Splits and re-indexes a list of terrain patches across extended region dimensions (up to 4096m)
     * into virtual 256m tile packets without dropping out-of-bounds coordinates.
     *
     * @param patches Incoming decoded terrain patches
     * @param regionWidth Total region width in meters
     * @param regionHeight Total region height in meters
     * @return Map of VirtualTileIndex to VirtualTilePacket containing patches in standard 0..15 range
     */
    fun splitTerrainPatchesToVirtualTiles(
        patches: List<TerrainPatch>,
        regionWidth: Int = STANDARD_TILE_SIZE,
        regionHeight: Int = STANDARD_TILE_SIZE
    ): Map<VirtualTileIndex, VirtualTilePacket> {
        val maxPatchesX = (regionWidth / STANDARD_PATCH_SIZE).coerceIn(1, MAX_PATCHES_PER_SIDE)
        val maxPatchesY = (regionHeight / STANDARD_PATCH_SIZE).coerceIn(1, MAX_PATCHES_PER_SIDE)

        val groupedPatches = mutableMapOf<VirtualTileIndex, MutableList<TerrainPatch>>()

        for (patch in patches) {
            if (patch.x < 0 || patch.y < 0 || patch.x >= maxPatchesX || patch.y >= maxPatchesY) {
                Log.w(TAG, "Filtering corrupted terrain patch out-of-bounds: x=${patch.x}, y=${patch.y} (max $maxPatchesX x $maxPatchesY)")
                continue
            }

            val (tileIndex, reindexedPatch) = mapPatchToVirtualTile(patch)
            groupedPatches.getOrPut(tileIndex) { mutableListOf() }.add(reindexedPatch)
        }

        return groupedPatches.mapValues { (tileIndex, patchList) ->
            VirtualTilePacket(tileIndex = tileIndex, patches = patchList)
        }
    }
}
