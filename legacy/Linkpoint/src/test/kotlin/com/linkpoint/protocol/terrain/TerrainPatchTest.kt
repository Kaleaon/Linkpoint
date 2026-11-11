package com.linkpoint.protocol.terrain

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TerrainPatchTest {

    @Test
    fun `TerrainPatch class initializes without ArrayIndexOutOfBoundsException`() {
        // This test verifies that the static initialization of TerrainPatch
        // completes successfully without throwing ArrayIndexOutOfBoundsException.
        // The crash was caused by buildCopyMatrix16() having incorrect zigzag
        // traversal logic that could produce negative indices.

        // Simply accessing PATCH_SIZE forces the companion object initialization
        val patchSize = TerrainPatch.PATCH_SIZE
        assertEquals(16, patchSize)
    }

    @Test
    fun `TerrainPatch companion object constants are correct`() {
        // Verify expected constants
        assertEquals(97, TerrainPatch.END_OF_PATCHES)
        assertEquals(16, TerrainPatch.PATCH_SIZE)
        assertEquals(16, TerrainPatch.PATCHES_PER_SIDE)
    }

    @Test
    fun `TerrainPatch can be instantiated with valid data`() {
        val heightMap = FloatArray(256) { it.toFloat() }
        val patch = TerrainPatch(5, 10, heightMap)

        assertEquals(5, patch.x)
        assertEquals(10, patch.y)
        assertEquals(256, patch.heightMap.size)
    }

    @Test
    fun `DecompressScratchBuffers initializes pre-allocated 256-element arrays`() {
        val scratch = DecompressScratchBuffers()
        assertEquals(256, scratch.patches.size)
        assertEquals(256, scratch.block.size)
        assertEquals(256, scratch.temp.size)
    }

    @Test
    fun `decompressPatch produces identical results with default vs explicit scratch buffers`() {
        val data1 = createSamplePatchData(patchX = 2, patchY = 5, dcOffset = 15.0f)
        val data2 = createSamplePatchData(patchX = 2, patchY = 5, dcOffset = 15.0f)

        val patchDefault = TerrainPatch.decompressPatch(BitBuffer(data1), 16)
        val customScratch = DecompressScratchBuffers()
        val patchExplicit = TerrainPatch.decompressPatch(BitBuffer(data2), 16, customScratch)

        assertNotNull(patchDefault)
        assertNotNull(patchExplicit)
        assertEquals(patchDefault!!.x, patchExplicit!!.x)
        assertEquals(patchDefault.y, patchExplicit.y)
        assertArrayEquals(patchDefault.heightMap, patchExplicit.heightMap, 0.0001f)
    }

    @Test
    fun `decompressPatch produces identical results when reusing single scratch buffer across calls`() {
        val data1 = createSamplePatchData(patchX = 1, patchY = 3, dcOffset = 12.5f)
        val data2 = createSamplePatchData(patchX = 4, patchY = 7, dcOffset = 25.0f)

        // Decode using fresh scratch buffers
        val patch1Fresh = TerrainPatch.decompressPatch(BitBuffer(data1.copyOf()), 16, DecompressScratchBuffers())
        val patch2Fresh = TerrainPatch.decompressPatch(BitBuffer(data2.copyOf()), 16, DecompressScratchBuffers())

        // Decode sequentially using the SAME reused scratch buffer
        val reusedScratch = DecompressScratchBuffers()
        val patch1Reused = TerrainPatch.decompressPatch(BitBuffer(data1.copyOf()), 16, reusedScratch)
        val patch2Reused = TerrainPatch.decompressPatch(BitBuffer(data2.copyOf()), 16, reusedScratch)

        assertNotNull(patch1Fresh)
        assertNotNull(patch2Fresh)
        assertNotNull(patch1Reused)
        assertNotNull(patch2Reused)

        assertEquals(patch1Fresh!!.x, patch1Reused!!.x)
        assertEquals(patch1Fresh.y, patch1Reused.y)
        assertArrayEquals(patch1Fresh.heightMap, patch1Reused.heightMap, 0.0001f)

        assertEquals(patch2Fresh!!.x, patch2Reused!!.x)
        assertEquals(patch2Fresh.y, patch2Reused.y)
        assertArrayEquals(patch2Fresh.heightMap, patch2Reused.heightMap, 0.0001f)
    }

    @Test
    fun `LayerDataParser parse decompresses multiple terrain patches correctly`() {
        val bits = BitStreamBuilder()
        // Header
        bits.writeBits(0x08, 8) // LSB of stride 264
        bits.writeBits(0x01, 8) // MSB of stride 264
        bits.writeBits(16, 8)   // patchSize = 16
        bits.writeBits(76, 8)   // layerType = 76

        // Patch 1 (x=0, y=0)
        bits.writeBits(0x10, 8)
        bits.writeFloat(10.0f)
        bits.writeBits(100, 16)
        bits.writeBits(0, 10)
        bits.writeBits(1, 1)
        bits.writeBits(0, 1)

        // Patch 2 (x=0, y=2 -> patchIds=2 -> write 8 for 10-bit getBits)
        bits.writeBits(0x10, 8)
        bits.writeFloat(20.0f)
        bits.writeBits(100, 16)
        bits.writeBits(8, 10)
        bits.writeBits(1, 1)
        bits.writeBits(0, 1)

        // End marker (97)
        bits.writeBits(TerrainPatch.END_OF_PATCHES, 8)

        val layerDataBytes = bits.toByteArray()
        val dataLen = layerDataBytes.size

        val fullMessage = ByteArray(3 + dataLen)
        fullMessage[0] = LayerType.LAND.toByte()
        fullMessage[1] = (dataLen and 0xFF).toByte()
        fullMessage[2] = ((dataLen shr 8) and 0xFF).toByte()
        System.arraycopy(layerDataBytes, 0, fullMessage, 3, dataLen)

        val result = LayerDataParser.parse(fullMessage, 256, 256)
        assertNotNull(result)
        assertEquals(LayerType.LAND, result!!.type)
        assertEquals(2, result.patches.size)

        assertEquals(0, result.patches[0].x)
        assertEquals(0, result.patches[0].y)

        assertEquals(0, result.patches[1].x)
        assertEquals(2, result.patches[1].y)
    }

    private fun createSamplePatchData(patchX: Int, patchY: Int, dcOffset: Float): ByteArray {
        // Construct a bitstream for a minimal valid patch:
        // quantWBits: 0x10 (8 bits)
        // dcOffset: float (32 bits)
        // range: 100 (16 bits)
        // patchIds: (patchX shl 5) or patchY (10 bits)
        // coefficients: end-of-block marker (bits: 1, 0)
        val bits = BitStreamBuilder()
        bits.writeBits(0x10, 8)
        bits.writeFloat(dcOffset)
        bits.writeBits(100, 16)
        val patchIds = (patchX shl 5) or (patchY and 31)
        bits.writeBits(patchIds, 10)
        // Coefficients: 1 then 0 -> end-of-block
        bits.writeBits(1, 1)
        bits.writeBits(0, 1)
        return bits.toByteArray()
    }

    private class BitStreamBuilder {
        private var currentByte = 0
        private var bitCount = 0
        private val bytes = mutableListOf<Byte>()

        fun writeBits(value: Int, numBits: Int) {
            for (i in numBits - 1 downTo 0) {
                val bit = (value shr i) and 1
                currentByte = (currentByte shl 1) or bit
                bitCount++
                if (bitCount == 8) {
                    bytes.add(currentByte.toByte())
                    currentByte = 0
                    bitCount = 0
                }
            }
        }

        fun writeFloat(value: Float) {
            val bits = java.lang.Float.floatToIntBits(value)
            writeBits(bits, 32)
        }

        fun toByteArray(): ByteArray {
            if (bitCount > 0) {
                val paddedByte = currentByte shl (8 - bitCount)
                bytes.add(paddedByte.toByte())
            }
            return bytes.toByteArray()
        }
    }
}
