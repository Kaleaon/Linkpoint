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
    
    /**
     * Parse a LayerData message payload.
     * 
     * @param data Raw message payload (after message ID)
     * @return Parsed layer data result, or null if parsing fails
     */
    fun parse(data: ByteArray): LayerDataResult? {
        if (data.isEmpty()) {
            Log.w(TAG, "Empty LayerData payload")
            return null
        }
        
        try {
            // First byte is layer type
            val type = data[0].toInt() and 0xFF
            
            if (data.size < 3) {
                Log.w(TAG, "LayerData payload too short: ${data.size} bytes; generating default patch fallback")
                return LayerDataResult(type, createDefaultPatches())
            }
            
            // Read 2-byte length
            val dataLen = ((data[1].toInt() and 0xFF) or 
                          ((data[2].toInt() and 0xFF) shl 8))
            
            if (data.size < 3 + dataLen) {
                Log.w(TAG, "LayerData data length mismatch: expected ${3 + dataLen}, got ${data.size}; falling back to default terrain patches")
                return LayerDataResult(type, createDefaultPatches())
            }
            
            val layerData = data.copyOfRange(3, 3 + dataLen)
            
            // Only process terrain data (type 76 = 'L')
            if (type != LayerType.LAND && type != LayerType.LAND_EXTENDED) {
                Log.d(TAG, "Ignoring non-terrain LayerData type: $type")
                return LayerDataResult(type, emptyList())
            }
            
            val patches = decompressPatches(layerData)
            if (patches.isEmpty()) {
                Log.w(TAG, "No patches decompressed from terrain payload; supplying default region heightmap fallback")
                return LayerDataResult(type, createDefaultPatches())
            }

            Log.d(TAG, "LayerData type=$type, decompressed ${patches.size} patches")
            return LayerDataResult(type, patches)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse LayerData; returning fallback region terrain", e)
            return LayerDataResult(LayerType.LAND, createDefaultPatches())
        }
    }
    
    /**
     * Decompress terrain patches from compressed data.
     */
    private fun decompressPatches(data: ByteArray): List<TerrainPatch> {
        val patches = mutableListOf<TerrainPatch>()
        
        try {
            val buffer = BitBuffer(data)
            
            // Read header
            val stride = buffer.getBits(16)  // Expected 264 (0x108)
            val patchSize = buffer.getBits(8)  // Expected 16
            val layerType = buffer.getBits(8)  // Layer type
            
            Log.d(TAG, "Terrain header: stride=0x${stride.toString(16)}, patchSize=$patchSize, type=$layerType")
            
            // Decompress patches until end marker
            while (!buffer.isEOF()) {
                val patch = TerrainPatch.decompressPatch(buffer, patchSize) ?: break
                
                if (patch.x < TerrainPatch.PATCHES_PER_SIDE && patch.y < TerrainPatch.PATCHES_PER_SIDE) {
                    patches.add(patch)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error decompressing terrain patches", e)
        }
        
        return patches
    }

    private fun createDefaultPatches(): List<TerrainPatch> {
        val defaultPatches = mutableListOf<TerrainPatch>()
        for (x in 0 until TerrainPatch.PATCHES_PER_SIDE) {
            for (y in 0 until TerrainPatch.PATCHES_PER_SIDE) {
                val heights = FloatArray(TerrainPatch.DEFAULT_PATCH_SIZE * TerrainPatch.DEFAULT_PATCH_SIZE) { DEFAULT_REGION_HEIGHT }
                defaultPatches.add(
                    TerrainPatch(
                        x = x,
                        y = y,
                        size = TerrainPatch.DEFAULT_PATCH_SIZE,
                        heights = heights
                    )
                )
            }
        }
        return defaultPatches
    }
}
