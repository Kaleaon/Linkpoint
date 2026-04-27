package com.linkpoint.slproto.terrain

import com.linkpoint.utils.BitBuffer
import kotlin.math.cos
import kotlin.math.PI

/**
 * TerrainPatch - Handles Second Life terrain patch decompression
 * 
 * Second Life terrain uses DCT (Discrete Cosine Transform) compression
 * Similar to JPEG compression but for height maps.
 * 
 * Based on Firestorm's terrain patch decoding
 */
class TerrainPatch {
    
    companion object {
        // Constants for patch processing
        const val END_OF_PATCHES: Int = 97
        private const val OO_SQRT2: Float = 0.70710677f  // 1/sqrt(2)
        
        // Lookup tables for performance (computed once)
        private val copyMatrix16 = IntArray(256)
        private val copyMatrix32 = IntArray(256)
        private val cosineTable16 = FloatArray(256)
        private val dequantizeTable16 = FloatArray(256)
        private val dequantizeTable32 = FloatArray(256)
        private val quantizeTable16 = FloatArray(256)
        
        // Initialize lookup tables on class load
        init {
            buildDequantizeTable16()
            setupCosines16()
            buildCopyMatrix16()
            buildQuantizeTable16()
        }
        
        /**
         * Build copy matrix for zigzag traversal
         * This reorders DCT coefficients for better compression
         */
        private fun buildCopyMatrix16() {
            var idx = 0
            var row = 0
            var col = 0
            var diagonal = true
            var increasing = false
            
            while (col < 16 && row < 16) {
                copyMatrix16[row * 16 + col] = idx
                idx++
                
                if (!increasing) {
                    if (diagonal) {
                        col = if (col < 15) col + 1 else col
                        if (col >= 15) row++
                        diagonal = false
                        increasing = true
                    } else {
                        row = if (row < 15) row + 1 else row
                        if (row >= 15) col++
                        diagonal = true
                        increasing = true
                    }
                } else if (diagonal) {
                    col++
                    row--
                    if (col == 15 || row == 0) {
                        increasing = false
                    }
                } else {
                    col--
                    row++
                    if (row == 15 || col == 0) {
                        increasing = false
                    }
                }
            }
        }
        
        /**
         * Build dequantization table for 16x16 patches
         * Maps quantized values back to DCT coefficients
         */
        private fun buildDequantizeTable16() {
            for (i in 0 until 16) {
                for (j in 0 until 16) {
                    dequantizeTable16[i * 16 + j] = ((j + i).toFloat() * 2.0f) + 1.0f
                }
            }
        }
        
        /**
         * Build quantization table (inverse of dequantization)
         */
        private fun buildQuantizeTable16() {
            for (i in 0 until 16) {
                for (j in 0 until 16) {
                    quantizeTable16[i * 16 + j] = 1.0f / (((j.toFloat() + i.toFloat()) * 2.0f) + 1.0f)
                }
            }
        }
        
        /**
         * Setup cosine lookup table for IDCT
         * Precompute cos values for inverse DCT
         */
        private fun setupCosines16() {
            for (i in 0 until 16) {
                for (j in 0 until 16) {
                    val angle = ((j.toFloat() * 2.0f + 1.0f) * i.toFloat() * PI / 32.0).toFloat()
                    cosineTable16[i * 16 + j] = cos(angle.toDouble()).toFloat()
                }
            }
        }
        
        /**
         * Decompress a terrain patch from bit buffer
         * 
         * @param bitBuffer Buffer containing compressed patch data
         * @param patchSize Size of patch (typically 16 or 32)
         * @return Decompressed terrain patch or null if end marker
         */
        fun decompressPatch(bitBuffer: BitBuffer, patchSize: Int): TerrainPatch? {
            // Read quantization bits
            val quantWBits = bitBuffer.getBits(8)
            
            // Check for end of patches marker
            if (quantWBits == END_OF_PATCHES) {
                return null
            }
            
            // Create new patch
            val patch = TerrainPatch()
            patch.quantWBits = quantWBits
            patch.dcOffset = bitBuffer.getFloat()
            patch.range = bitBuffer.getBits(16)
            patch.patchIDs = bitBuffer.getBits(10)
            patch.wordBits = (quantWBits and 15) + 2
            
            // Allocate patches array
            val patchCount = patchSize * patchSize
            patch.patches = IntArray(patchCount)
            
            // Decode patch coefficients
            var i = 0
            while (i < patchCount) {
                when {
                    bitBuffer.getBits(1) == 0 -> {
                        // Zero coefficient
                        patch.patches[i] = 0
                    }
                    bitBuffer.getBits(1) == 0 -> {
                        // All remaining coefficients are zero
                        while (i < patchCount) {
                            patch.patches[i] = 0
                            i++
                        }
                        break
                    }
                    bitBuffer.getBits(1) != 0 -> {
                        // Negative coefficient
                        patch.patches[i] = bitBuffer.getBits(patch.wordBits) * -1
                    }
                    else -> {
                        // Positive coefficient
                        patch.patches[i] = bitBuffer.getBits(patch.wordBits)
                    }
                }
                i++
            }
            
            // Perform inverse DCT to get height map
            val tempBuffer = FloatArray(patchCount)
            val heightMap = FloatArray(patchCount)
            
            // Calculate dequantization parameters
            val quantBits = (patch.quantWBits shr 4) + 2
            val dequantScale = (1.0f / (1 shl quantBits).toFloat()) * patch.range.toFloat()
            val dequantOffset = patch.dcOffset + ((1 shl (quantBits - 1)).toFloat() * dequantScale)
            
            if (patchSize == 16) {
                // Dequantize coefficients
                for (idx in 0 until 256) {
                    tempBuffer[idx] = patch.patches[copyMatrix16[idx]].toFloat() * dequantizeTable16[idx]
                }
                
                // Inverse DCT - column transform
                val columnBuffer = FloatArray(256)
                for (col in 0 until 16) {
                    idctColumn16(tempBuffer, columnBuffer, col)
                }
                
                // Inverse DCT - row transform
                for (row in 0 until 16) {
                    idctLine16(columnBuffer, tempBuffer, row)
                }
            } else if (patchSize == 32) {
                // 32x32 patches (less common, simplified handling)
                for (idx in 0 until 1024) {
                    tempBuffer[idx] = patch.patches[copyMatrix32[idx]].toFloat() * dequantizeTable32[idx]
                }
                // Note: Full 32x32 IDCT implementation would go here
            }
            
            // Apply scale and offset to get final heights
            for (idx in tempBuffer.indices) {
                heightMap[idx] = (tempBuffer[idx] * dequantScale) + dequantOffset
            }
            
            patch.heightMap = heightMap
            return patch
        }
        
        /**
         * Inverse DCT for a column
         */
        private fun idctColumn16(input: FloatArray, output: FloatArray, col: Int) {
            for (row in 0 until 16) {
                var sum = input[col] * OO_SQRT2
                for (k in 1 until 16) {
                    val idx = k * 16
                    sum += cosineTable16[idx + row] * input[idx + col]
                }
                output[row * 16 + col] = sum
            }
        }
        
        /**
         * Inverse DCT for a row
         */
        private fun idctLine16(input: FloatArray, output: FloatArray, row: Int) {
            val rowOffset = row * 16
            for (col in 0 until 16) {
                var sum = input[rowOffset] * OO_SQRT2
                for (k in 1 until 16) {
                    sum += input[rowOffset + k] * cosineTable16[k * 16 + col]
                }
                output[rowOffset + col] = sum * 0.125f
            }
        }
    }
    
    // Instance variables
    var dcOffset: Float = 0f
    var patchIDs: Int = 0
    var quantWBits: Int = 0
    var range: Int = 0
    var wordBits: Int = 0
    var heightMap: FloatArray? = null
    var patches: IntArray? = null
    
    /**
     * Get X coordinate of patch in region
     */
    val x: Int
        get() = patchIDs shr 5
    
    /**
     * Get Y coordinate of patch in region
     */
    val y: Int
        get() = patchIDs and 31
}
