package com.linkpoint.assets.mesh

import com.linkpoint.protocol.llsd.LLSDInteger
import com.linkpoint.protocol.llsd.LLSDMap
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MeshHeaderDecoderTest {

    private lateinit var decoder: MeshHeaderDecoder

    @Before
    fun setUp() {
        decoder = MeshHeaderDecoder()
    }

    @Test
    fun testDecodeHeaderValidMapWithoutMagicHeader() {
        val mapBytes = buildBinaryLlsdMap(
            "high_lod" to mapOf("offset" to 0, "size" to 100),
            "medium_lod" to mapOf("offset" to 100, "size" to 50)
        )

        val result = decoder.decodeHeader(mapBytes)
        assertNotNull(result)
        assertEquals(mapBytes.size, result!!.headerEnd)

        val map = result.map
        assertTrue(map.getMap("high_lod") != null)
        assertEquals(0, map.getMap("high_lod")?.getInt("offset"))
        assertEquals(100, map.getMap("high_lod")?.getInt("size"))
        assertEquals(1L, decoder.getParseSuccessCount())
        assertEquals(0L, decoder.getParseFailureCount())
    }

    @Test
    fun testDecodeHeaderValidMapWithMagicHeaderLF() {
        val mapBytes = buildBinaryLlsdMap("high_lod" to mapOf("offset" to 0, "size" to 100))
        val magic = "<?llsd/binary?>\n".toByteArray(Charsets.US_ASCII)
        val fullPayload = magic + mapBytes

        val result = decoder.decodeHeader(fullPayload)
        assertNotNull(result)
        assertEquals(fullPayload.size, result!!.headerEnd)
        assertEquals(magic.size + mapBytes.size, result.headerEnd)

        val map = result.map
        assertTrue(map.getMap("high_lod") != null)
        assertEquals(1L, decoder.getParseSuccessCount())
        assertEquals(0L, decoder.getParseFailureCount())
    }

    @Test
    fun testDecodeHeaderValidMapWithMagicHeaderCRLF() {
        val mapBytes = buildBinaryLlsdMap("high_lod" to mapOf("offset" to 10, "size" to 20))
        val magic = "<?llsd/binary?>\r\n".toByteArray(Charsets.US_ASCII)
        val fullPayload = magic + mapBytes

        val result = decoder.decodeHeader(fullPayload)
        assertNotNull(result)
        assertEquals(fullPayload.size, result!!.headerEnd)
        assertEquals(magic.size + mapBytes.size, result.headerEnd)
        assertEquals(1L, decoder.getParseSuccessCount())
        assertEquals(0L, decoder.getParseFailureCount())
    }

    @Test
    fun testDecodeHeaderWithTrailingPayloadData() {
        val mapBytes = buildBinaryLlsdMap("high_lod" to mapOf("offset" to 0, "size" to 10))
        val trailingData = "TRAILING_MOCK_COMPRESSED_DATA_BLOB".toByteArray(Charsets.UTF_8)
        val fullData = mapBytes + trailingData

        val result = decoder.decodeHeader(fullData)
        assertNotNull(result)
        assertEquals(mapBytes.size, result!!.headerEnd)
        assertEquals('T'.code.toByte(), fullData[result.headerEnd])
    }

    @Test
    fun testDecodeHeaderEmptyDataReturnsNull() {
        val result = decoder.decodeHeader(byteArrayOf())
        assertNull(result)
        assertEquals(1L, decoder.getParseFailureCount())
        assertEquals(0L, decoder.getParseSuccessCount())
    }

    @Test
    fun testDecodeHeaderMalformedBinaryReturnsNull() {
        val badData = byteArrayOf('{'.code.toByte(), 0x00, 0x00, 0x00, 0x05, 'x'.code.toByte())
        val result = decoder.decodeHeader(badData)
        assertNull(result)
        assertEquals(1L, decoder.getParseFailureCount())
        assertEquals(0L, decoder.getParseSuccessCount())
    }

    @Test
    fun testDecodeHeaderNotALlsdMapReturnsNull() {
        // Binary integer value ('i' + 4-byte int)
        val intValueBytes = byteArrayOf('i'.code.toByte(), 0x00, 0x00, 0x00, 0x2A)
        val result = decoder.decodeHeader(intValueBytes)
        assertNull(result)
        assertEquals(1L, decoder.getParseFailureCount())
    }

    private fun buildBinaryLlsdMap(vararg entries: Pair<String, Any>): ByteArray {
        val bos = ByteArrayOutputStream()
        bos.write('{'.code)
        val countBuf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(entries.size).array()
        bos.write(countBuf)
        for ((key, value) in entries) {
            bos.write('k'.code)
            val keyBytes = key.toByteArray(Charsets.UTF_8)
            val keyLenBuf = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(keyBytes.size).array()
            bos.write(keyLenBuf)
            bos.write(keyBytes)
            writeLlsdValue(bos, value)
        }
        bos.write('}'.code)
        return bos.toByteArray()
    }

    private fun writeLlsdValue(bos: ByteArrayOutputStream, value: Any) {
        when (value) {
            is Int -> {
                bos.write('i'.code)
                bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array())
            }
            is String -> {
                bos.write('s'.code)
                val bytes = value.toByteArray(Charsets.UTF_8)
                bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(bytes.size).array())
                bos.write(bytes)
            }
            is Map<*, *> -> {
                bos.write('{'.code)
                val mapEntries = value.entries.map { (k, v) -> k.toString() to v!! }
                bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(mapEntries.size).array())
                for ((k, v) in mapEntries) {
                    bos.write('k'.code)
                    val kb = k.toByteArray(Charsets.UTF_8)
                    bos.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(kb.size).array())
                    bos.write(kb)
                    writeLlsdValue(bos, v)
                }
                bos.write('}'.code)
            }
            else -> throw IllegalArgumentException("Unsupported value in test helper: $value")
        }
    }
}
