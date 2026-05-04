package com.linkpoint.objects

import com.linkpoint.protocol.messages.MESSAGE_BYTE_ORDER
import com.linkpoint.protocol.messages.ObjectMessageParsers
import com.linkpoint.protocol.messages.UDPConnectionFixed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.util.UUID

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
        val compressedBlock = ByteBuffer.allocate(62).order(MESSAGE_BYTE_ORDER).apply {
            putLong(fullId.mostSignificantBits)
            putLong(fullId.leastSignificantBits)
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
        val block = ByteBuffer.allocate(30).order(MESSAGE_BYTE_ORDER).apply {
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
