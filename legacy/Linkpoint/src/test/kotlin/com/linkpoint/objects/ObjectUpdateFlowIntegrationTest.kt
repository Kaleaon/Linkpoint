package com.linkpoint.objects

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linkpoint.protocol.messages.MESSAGE_BYTE_ORDER
import com.linkpoint.protocol.messages.ObjectMessageParsers
import com.linkpoint.protocol.messages.UDPConnectionFixed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

// ObjectMessageParsers / UDPConnectionFixed call android.util.Log on
// the parse path, which trips UnsatisfiedLinkError under stock JUnit.
// Robolectric's Log shadow keeps the integration end-to-end runnable
// off-device.
@RunWith(AndroidJUnit4::class)
class ObjectUpdateFlowIntegrationTest {
    @Test
    fun objectUpdateCompressedAndTerse_endToEnd_insertsObjectIntoSceneState() {
        val objectManager = ObjectManager(UDPConnectionFixed())
        val fullId = UUID.fromString("11111111-2222-3333-4444-555555555555")
        val localId = 9001

        val parsedCompressed = ObjectMessageParsers.parseObjectUpdateCompressed(buildObjectUpdateCompressedPacket(localId, fullId))
        assertTrue(parsedCompressed.isNotEmpty())
        parsedCompressed.forEach { objectManager.handleObjectUpdate(it) }

        val parsedTerse = ObjectMessageParsers.parseTerseObjectUpdate(buildImprovedTersePacket(localId))
        assertTrue(parsedTerse.isNotEmpty())
        parsedTerse.forEach { objectManager.handleTerseUpdate(it) }

        assertTrue(objectManager.getAllObjects().isNotEmpty())
        val inserted = objectManager.getObject(localId)
        assertNotNull(inserted)
        assertEquals(fullId, inserted?.fullId)

        val counters = objectManager.getUpdateCounters()
        assertTrue(counters.packetsReceived >= 2)
        assertTrue(counters.packetsParsed >= 2)
        assertTrue(counters.objectsCreatedOrUpdated >= 2)
        assertTrue(counters.sceneInsertedOrUpdated >= 2)
    }

    private fun buildObjectUpdateCompressedPacket(localId: Int, fullId: UUID): ByteArray {
        // Layout: UUID(16) + localId(4) + state(1) + crcSeed(1) + crc(4)
        // + pcode(1) + extraParams(1) + scale(12) + position(12)
        // + velocity(12) + flags(4) = 68 bytes. The previous 62-byte
        // allocation under-counted three of the float triplets and
        // tripped a BufferOverflowException on construction.
        // SL UUIDs travel as 16 raw bytes in network (big-endian)
        // order, *regardless* of MESSAGE_BYTE_ORDER on the surrounding
        // fields. Writing the longs through the LITTLE_ENDIAN buffer
        // reverses each half and the parser ends up with a mangled
        // UUID. Build the canonical 16 bytes separately and embed
        // them.
        val uuidBytes = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN).apply {
            putLong(fullId.mostSignificantBits)
            putLong(fullId.leastSignificantBits)
        }.array()
        val compressedBlock = ByteBuffer.allocate(68).order(MESSAGE_BYTE_ORDER).apply {
            put(uuidBytes)
            putInt(localId)
            put(9)
            put(0)
            putInt(0)
            put(3)
            put(0)
            repeat(3) { putFloat(1f) }
            repeat(3) { putFloat(10f) }
            putFloat(0f); putFloat(0f); putFloat(0f)
            putInt(0)
        }.array()

        return ByteBuffer.allocate(8 + 2 + 1 + 4 + 2 + compressedBlock.size).order(MESSAGE_BYTE_ORDER).apply {
            putLong(0L)
            putShort(0)
            put(1)
            putInt(0)
            putShort(compressedBlock.size.toShort())
            put(compressedBlock)
        }.array()
    }

    private fun buildImprovedTersePacket(localId: Int): ByteArray {
        // Layout: localId(4) + state(1) + payload(26) = 31 bytes.
        // The previous 30-byte allocation under-counted by one and
        // tripped a BufferOverflowException on the last `put(0)`.
        val block = ByteBuffer.allocate(31).order(MESSAGE_BYTE_ORDER).apply {
            putInt(localId)
            put(0)
            repeat(26) { put(0) }
        }.array()

        return ByteBuffer.allocate(8 + 2 + 1 + 1 + block.size).order(MESSAGE_BYTE_ORDER).apply {
            putLong(0L)
            putShort(0)
            put(1)
            put(block.size.toByte())
            put(block)
        }.array()
    }
}
