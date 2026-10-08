package com.linkpoint.protocol.terrain

import android.util.Log

/**
 * LayerData message types.
 * Type 76 (0x4C = 'L') is terrain heightmap data.
 */
object LayerType {
    const val LAND = 76        // 'L' - Terrain heightmap
    const val WIND = 87        // 'W' - Wind data
    const val CLOUD = 67       // 'C' - Cloud data
    const val LAND_EXTENDED = 77  // 'M' - Extended terrain (varsim)
}

/**
 * Parsed LayerData result containing decoded terrain patches.
 */
data class LayerDataResult(
    val type: Int,
    val patches: List<TerrainPatch>
)

/**
 * Parser for LayerData messages from the Second Life protocol.
 *
 * LayerData contains terrain heightmap data compressed using a
 * Discrete Cosine Transform (DCT) algorithm.
 *
 * Message format:
 * - LayerID block:
 *   - Type: U8 (layer type)
 * - LayerDataData block:
 *   - Data: Variable (2-byte length prefix)
 */
object LayerDataParser {
    private const val TAG = "LayerDataParser"
    private const val DEFAULT_REGION_HEIGHT = 20.0f
    private const val MAX_REGION_SIZE = 4096 // Guardrail limit to prevent out-of-memory on mobile

    /**
     * Parse a LayerData message payload.
     *
     * @param data Raw message payload (after message ID)
     * @param regionSizeX Active region width in meters (defaults to 2048 for full patch extraction)
     * @param regionSizeY Active region height in meters (defaults to 2048 for full patch extraction)
     * @return Parsed layer data result, or null if parsing fails
     */
    fun parse(data: ByteArray, regionSizeX: Int = 2048, regionSizeY: Int = 2048): LayerDataResult? {
        if (data.isEmpty()) {
            Log.w(TAG, "Empty LayerData payload")
            return null
        }

        val validSizeX = regionSizeX.coerceIn(256, MAX_REGION_SIZE)
        val validSizeY = regionSizeY.coerceIn(256, MAX_REGION_SIZE)
        val maxPatchesX = validSizeX / TerrainPatch.PATCH_SIZE
        val maxPatchesY = validSizeY / TerrainPatch.PATCH_SIZE

        try {
            // First byte is layer type
            val type = data[0].toInt() and 0xFF

            if (data.size < 3) {
                Log.w(TAG, "LayerData payload too short: ${data.size} bytes; generating default patch fallback")
                return LayerDataResult(type, createDefaultPatches(validSizeX, validSizeY))
            }

            // Read 2-byte length
            val dataLen = ((data[1].toInt() and 0xFF) or
                          ((data[2].toInt() and 0xFF) shl 8))

            if (data.size < 3 + dataLen) {
                Log.w(TAG, "LayerData data length mismatch: expected ${3 + dataLen}, got ${data.size}; falling back to default terrain patches")
                return LayerDataResult(type, createDefaultPatches(validSizeX, validSizeY))
            }

            val layerData = data.copyOfRange(3, 3 + dataLen)

            // Only process terrain data (type 76 = 'L', type 77 = 'M')
            if (type != LayerType.LAND && type != LayerType.LAND_EXTENDED) {
                Log.d(TAG, "Ignoring non-terrain LayerData type: $type")
                return LayerDataResult(type, emptyList())
            }

            val patches = decompressPatches(layerData, maxPatchesX, maxPatchesY)
            if (patches.isEmpty()) {
                Log.w(TAG, "No patches decompressed from terrain payload; supplying default region heightmap fallback")
                return LayerDataResult(type, createDefaultPatches(validSizeX, validSizeY))
            }

            Log.d(TAG, "LayerData type=$type, decompressed ${patches.size} patches for ${validSizeX}x${validSizeY} region")
            return LayerDataResult(type, patches)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse LayerData; returning fallback region terrain", e)
            return LayerDataResult(LayerType.LAND, createDefaultPatches(validSizeX, validSizeY))
        }
    }

    /**
     * Decompress terrain patches from compressed data.
     */
    private fun decompressPatches(
        data: ByteArray,
        maxPatchesX: Int = TerrainPatch.PATCHES_PER_SIDE,
        maxPatchesY: Int = TerrainPatch.PATCHES_PER_SIDE
    ): List<TerrainPatch> {
        val patches = mutableListOf<TerrainPatch>()

        try {
            val buffer = BitBuffer(data)

            // Read header
            val stride = buffer.getBits(16)  // Expected 264 (0x108)
            val patchSize = buffer.getBits(8)  // Expected 16
            val layerType = buffer.getBits(8)  // Layer type

            Log.d(TAG, "Terrain header: stride=0x${stride.toString(16)}, patchSize=$patchSize, type=$layerType, bounds=${maxPatchesX}x${maxPatchesY}")
            val scratch = DecompressScratchBuffers()

            val maxPatches = maxOf(maxPatchesX, maxPatchesY)

            // Decompress patches until end marker (supporting Varregions up to 4096m)
            while (!buffer.isEOF()) {
                val patch = TerrainPatch.decompressPatch(buffer, patchSize, maxPatches, scratch) ?: break

                if (patch.x in 0 until maxPatchesX && patch.y in 0 until maxPatchesY) {
                    patches.add(patch)
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error decompressing terrain patches", e)
        }

        return patches
    }

    /**
     * Parse LayerData message and split/re-index terrain patches into virtual 256m tiles.
     * All patches in the returned VirtualTilePackets are re-indexed to standard 0 to 15 coordinate ranges.
     */
    fun parseVirtualTiles(
        data: ByteArray,
        regionWidth: Int = VirtualRegionMapper.STANDARD_TILE_SIZE,
        regionHeight: Int = VirtualRegionMapper.STANDARD_TILE_SIZE
    ): Map<VirtualTileIndex, VirtualTilePacket> {
        val result = parse(data) ?: return emptyMap()
        if (result.type != LayerType.LAND && result.type != LayerType.LAND_EXTENDED) {
            return emptyMap()
        }
        return VirtualRegionMapper.splitTerrainPatchesToVirtualTiles(result.patches, regionWidth, regionHeight)
    }

    internal fun createDefaultPatches(regionSizeX: Int = 256, regionSizeY: Int = 256): List<TerrainPatch> {
        val defaultPatches = mutableListOf<TerrainPatch>()
        val maxPatchesX = (regionSizeX.coerceIn(256, MAX_REGION_SIZE)) / TerrainPatch.PATCH_SIZE
        val maxPatchesY = (regionSizeY.coerceIn(256, MAX_REGION_SIZE)) / TerrainPatch.PATCH_SIZE
        for (x in 0 until maxPatchesX) {
            for (y in 0 until maxPatchesY) {
                val heights = FloatArray(TerrainPatch.PATCH_SIZE * TerrainPatch.PATCH_SIZE) { DEFAULT_REGION_HEIGHT }
                defaultPatches.add(
                    TerrainPatch(
                        x = x,
                        y = y,
                        heightMap = heights
                    )
                )
            }
        }
        return defaultPatches
    }
}
