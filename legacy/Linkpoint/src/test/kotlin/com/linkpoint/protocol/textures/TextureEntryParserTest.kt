package com.linkpoint.protocol.textures

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class TextureEntryParserTest {

    @Test
    fun testExtractDefaultTextureAndMaterialIds() {
        val defaultTextureId = UUID.randomUUID()
        val buffer = ByteBuffer.allocate(17)

        // Write default texture UUID (big endian)
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putLong(defaultTextureId.mostSignificantBits)
        buffer.putLong(defaultTextureId.leastSignificantBits)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        // End bitfield marker 0
        buffer.put(0.toByte())

        val data = buffer.array()
        val extractedTextures = TextureEntryParser.extractTextureIds(data)
        assertEquals(1, extractedTextures.size)
        assertTrue(extractedTextures.contains(defaultTextureId))

        val extractedMaterials = TextureEntryParser.extractMaterialIds(data)
        assertTrue(extractedMaterials.isEmpty())
    }
}
