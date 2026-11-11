package com.linkpoint.protocol.terrain

import kotlin.math.cos

/**
 * Pre-allocated scratch buffers for terrain patch decompression.
 * Reused across patch decoding calls to avoid temporary array allocations.
 */
class DecompressScratchBuffers(
    val patches: IntArray = IntArray(256),
    val block: FloatArray = FloatArray(256),
    val temp: FloatArray = FloatArray(256)
)

/**
 * Terrain patch decoder for Second Life heightmap data.
 *
 * Based on the reference viewer's TerrainPatch implementation. Decodes DCT-compressed
 * terrain patches from LayerData messages.
 */
class TerrainPatch(
    val x: Int,
    val y: Int,
    val heightMap: FloatArray
) {
    companion object {
        const val END_OF_PATCHES = 97
        const val PATCH_SIZE = 16
        const val PATCHES_PER_SIDE = 16

        // Pre-computed tables for DCT decompression
        private val dequantizeTable16 = FloatArray(256)
        private val cosineTable16 = FloatArray(256)
        private val copyMatrix16 = IntArray(256)

        // DCT constants
        private const val OO_SQRT2 = 0.70710677f
        private const val PI_OVER_32 = 0.09817477f  // π/32 for cosine table
        private const val IDCT_NORMALIZATION = 0.125f  // 1/8 for IDCT normalization

        init {
            buildDequantizeTable16()
            setupCosines16()
            buildCopyMatrix16()
        }

        private fun buildDequantizeTable16() {
            for (i in 0 until 16) {
                for (j in 0 until 16) {
                    dequantizeTable16[i * 16 + j] = (i + j) * 2.0f + 1.0f
                }
            }
        }

        private fun setupCosines16() {
            for (i in 0 until 16) {
                for (j in 0 until 16) {
                    cosineTable16[i * 16 + j] = cos((j * 2.0f + 1.0f) * i * PI_OVER_32).toFloat()
                }
            }
        }

        private fun buildCopyMatrix16() {
            var n = 0
            var x = 0
            var y = 0
            var goingUpRight = true
            var inDiagonal = false

            while (x in 0..15 && y in 0..15) {
                copyMatrix16[y * 16 + x] = n++

                if (inDiagonal) {
                    if (goingUpRight) {
                        // Moving up-right diagonal
                        x++
                        y--
                        if (x == 15 || y == 0) {
                            inDiagonal = false
                        }
                    } else {
                        // Moving down-left diagonal
                        x--
                        y++
                        if (y == 15 || x == 0) {
                            inDiagonal = false
                        }
                    }
                } else {
                    // Horizontal/vertical step to start next diagonal
                    if (goingUpRight) {
                        if (x < 15) x++ else y++
                        goingUpRight = false
                    } else {
                        if (y < 15) y++ else x++
                        goingUpRight = true
                    }
                    inDiagonal = true
                }
            }

            require(n == 256) { "Zigzag matrix must fill exactly 256 elements, but filled $n" }
        }

        /**
         * Decompress a terrain patch from the bit buffer.
         * Returns null if end-of-patches marker is found.
         * @param maxPatches Maximum number of patches per side (e.g. 16 for 256m, 32 for 512m, 128 for 2048m).
         */
        fun decompressPatch(
            buffer: BitBuffer,
            patchSize: Int,
            scratch: DecompressScratchBuffers
        ): TerrainPatch? = decompressPatch(buffer, patchSize, 32, scratch)

        fun decompressPatch(
            buffer: BitBuffer,
            patchSize: Int,
            maxPatches: Int = 32,
            scratch: DecompressScratchBuffers = DecompressScratchBuffers()
        ): TerrainPatch? {
            val quantWBits = buffer.getBits(8)

            if (quantWBits == END_OF_PATCHES) {
                return null
            }

            val dcOffset = buffer.getFloat()
            val range = buffer.getBits(16)
            val patchBits = if (maxPatches > 32) 14 else 10
            val patchIds = buffer.getBits(patchBits)
            val wordBits = (quantWBits and 15) + 2

            val totalSize = patchSize * patchSize
            val patches = if (scratch.patches.size >= totalSize) scratch.patches else IntArray(totalSize)
            val block = if (scratch.block.size >= totalSize) scratch.block else FloatArray(totalSize)
            val temp = if (scratch.temp.size >= totalSize) scratch.temp else FloatArray(totalSize)

            var i = 0
            while (i < totalSize) {
                if (buffer.getBits(1) == 0) {
                    // Zero coefficient
                    patches[i] = 0
                } else if (buffer.getBits(1) == 0) {
                    // End-of-block: rest are zeros
                    while (i < totalSize) {
                        patches[i] = 0
                        i++
                    }
                    break
                } else if (buffer.getBits(1) != 0) {
                    // Negative coefficient
                    patches[i] = -buffer.getBits(wordBits)
                } else {
                    // Positive coefficient
                    patches[i] = buffer.getBits(wordBits)
                }
                i++
            }

            // Apply inverse DCT
            val output = FloatArray(totalSize)

            val quantBits = (quantWBits shr 4) + 2
            val mult = (1.0f / (1 shl quantBits)) * range
            val addval = dcOffset + ((1 shl (quantBits - 1)) * mult)

            if (patchSize == 16) {
                // Dequantize and reorder
                for (j in 0 until 256) {
                    block[j] = patches[copyMatrix16[j]] * dequantizeTable16[j]
                }

                // IDCT columns
                for (col in 0 until 16) {
                    idctColumn16(block, temp, col)
                }

                // IDCT rows
                for (row in 0 until 16) {
                    idctLine16(temp, block, row)
                }
            }

            // Scale and offset
            for (j in 0 until totalSize) {
                output[j] = block[j] * mult + addval
            }

            val bitsPerAxis = patchBits / 2
            val mask = (1 shl bitsPerAxis) - 1
            val patchX = patchIds shr bitsPerAxis
            val patchY = patchIds and mask

            return TerrainPatch(patchX, patchY, output)
        }

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

        private fun idctLine16(input: FloatArray, output: FloatArray, row: Int) {
            val rowIdx = row * 16
            for (col in 0 until 16) {
                var sum = input[rowIdx] * OO_SQRT2
                for (k in 1 until 16) {
                    sum += input[rowIdx + k] * cosineTable16[k * 16 + col]
                }
                output[rowIdx + col] = sum * IDCT_NORMALIZATION
            }
        }
    }
}
