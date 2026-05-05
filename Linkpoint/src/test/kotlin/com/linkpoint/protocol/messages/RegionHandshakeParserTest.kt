package com.linkpoint.protocol.messages

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

// parseRegionHandshake calls android.util.Log on rejected payloads,
// which is a JNI native method that throws UnsatisfiedLinkError under
// stock JUnit. Robolectric's Log shadow turns those calls into
// harmless no-ops so the rejection branch stays testable.
@RunWith(AndroidJUnit4::class)
class RegionHandshakeParserTest {

    @Test
    fun `parseRegionHandshake trims null bytes from sim name`() {
        val data = buildRegionHandshakePayload("TestRegion\u0000\u0000")
        val parsed = MessageParser.parseRegionHandshake(data)
        assertNotNull(parsed)
        assertEquals("TestRegion", parsed?.simName)
    }

    @Test
    fun `parseRegionHandshake rejects partial or empty payload`() {
        assertNull(MessageParser.parseRegionHandshake(byteArrayOf()))
        assertNull(MessageParser.parseRegionHandshake(ByteArray(10)))
    }

    private fun buildRegionHandshakePayload(simName: String): ByteArray {
        val simNameBytes = simName.toByteArray(Charsets.UTF_8)
        val buffer = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN)

        // RegionInfo block
        buffer.putInt(0) // RegionFlags
        buffer.put(0) // SimAccess
        buffer.put(simNameBytes.size.toByte())
        buffer.put(simNameBytes)

        // SimOwner UUID
        buffer.put(UUID(0L, 1L).toBytes())

        // IsEstateManager
        buffer.put(0)

        // WaterHeight
        buffer.putFloat(20f)

        // BillableFactor
        buffer.putFloat(1f)

        // CacheID
        buffer.put(UUID(2L, 3L).toBytes())

        // TerrainBase0-3 and TerrainDetail0-3 (8 UUIDs)
        repeat(8) { buffer.put(UUID(it.toLong(), (it + 1).toLong()).toBytes()) }

        // TerrainStartHeight00-11 (4 floats)
        repeat(4) { buffer.putFloat(0f) }

        // TerrainHeightRange00-11 (4 floats)
        repeat(4) { buffer.putFloat(0f) }

        val size = buffer.position()
        return buffer.array().copyOf(size)
    }

    private fun UUID.toBytes(): ByteArray {
        val bytes = ByteArray(16)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        bb.putLong(this.mostSignificantBits)
        bb.putLong(this.leastSignificantBits)
        return bytes
    }
}
