package com.linkpoint.protocol.messages

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class DeclaredMessageSlicesTest {

    @Test
    fun `user-critical slice supports parser writer handler`() {
        val message = DeclaredMessageSlices.FetchInventoryDescendentsMessage(
            agentId = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            sessionId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
            folderId = UUID.fromString("33333333-3333-3333-3333-333333333333"),
            ownerId = UUID.fromString("44444444-4444-4444-4444-444444444444"),
            sortOrder = 1,
            fetchFolders = true,
            fetchItems = false,
        )

        val payload = DeclaredMessageSlices.writeFetchInventoryDescendents(message)
        val parsed = DeclaredMessageSlices.parseFetchInventoryDescendents(payload)
        assertEquals(message, parsed)

        val rawPacket = wrapRawPacket(MessageIds.FETCH_INVENTORY_DESCENDENTS, payload)
        var handledSummary = ""
        val handled = DeclaredMessageSlices.handle(MessageIds.FETCH_INVENTORY_DESCENDENTS, rawPacket) {
            handledSummary = it
        }
        assertTrue(handled)
        assertTrue(handledSummary.contains("FetchInventoryDescendents"))
        assertTrue(MessageParserRegistry.hasHandler(MessageIds.FETCH_INVENTORY_DESCENDENTS))
        assertTrue(MessageTemplateCatalog.supportedParserOrWriterMessages.contains("FetchInventoryDescendents"))
    }

    @Test
    fun `medium-utility slice supports parser writer handler`() {
        val message = DeclaredMessageSlices.MapNameRequestMessage(
            agentId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
            sessionId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
            flags = 2,
            estateId = 8,
            godlike = false,
            name = "Sandbox",
        )

        val payload = DeclaredMessageSlices.writeMapNameRequest(message)
        val parsed = DeclaredMessageSlices.parseMapNameRequest(payload)
        assertEquals(message, parsed)

        val rawPacket = wrapRawPacket(MessageIds.MAP_NAME_REQUEST, payload)
        var handledSummary = ""
        val handled = DeclaredMessageSlices.handle(MessageIds.MAP_NAME_REQUEST, rawPacket) {
            handledSummary = it
        }
        assertTrue(handled)
        assertTrue(handledSummary.contains("MapNameRequest"))
        assertTrue(MessageParserRegistry.hasHandler(MessageIds.MAP_NAME_REQUEST))
        assertTrue(MessageTemplateCatalog.supportedParserOrWriterMessages.contains("MapNameRequest"))
    }

    @Test
    fun `low-priority-admin slice supports parser writer handler`() {
        val message = DeclaredMessageSlices.AgentPauseMessage(
            agentId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
            sessionId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
            serialNum = 42,
        )

        val payload = DeclaredMessageSlices.writeAgentPause(message)
        val parsed = DeclaredMessageSlices.parseAgentPause(payload)
        assertEquals(message, parsed)

        val rawPacket = wrapRawPacket(MessageIds.AGENT_PAUSE, payload)
        var handledSummary = ""
        val handled = DeclaredMessageSlices.handle(MessageIds.AGENT_PAUSE, rawPacket) {
            handledSummary = it
        }
        assertTrue(handled)
        assertTrue(handledSummary.contains("AgentPause"))
        assertTrue(MessageParserRegistry.hasHandler(MessageIds.AGENT_PAUSE))
        assertTrue(MessageTemplateCatalog.supportedParserOrWriterMessages.contains("AgentPause"))
    }

    @Test
    fun `declared-only catalog is split into three slices`() {
        assertTrue(MessageTemplateCatalog.declaredOnlyUserCriticalMessagesWithRationale.isNotEmpty())
        assertTrue(MessageTemplateCatalog.declaredOnlyMediumUtilityMessagesWithRationale.isNotEmpty())
        assertTrue(MessageTemplateCatalog.declaredOnlyLowPriorityAdminMessagesWithRationale.isNotEmpty())
        assertTrue(MessageTemplateCatalog.declaredOnlyMessagesWithRationale.keys.none { it == "MapNameRequest" })
    }

    private fun wrapRawPacket(messageId: Int, payload: ByteArray): ByteArray {
        val idBytes = encodeMessageId(messageId)
        val packet = ByteArray(6 + idBytes.size + payload.size)
        idBytes.copyInto(packet, destinationOffset = 6)
        payload.copyInto(packet, destinationOffset = 6 + idBytes.size)
        return packet
    }

    private fun encodeMessageId(messageId: Int): ByteArray {
        return when {
            messageId > 0 && messageId < 256 -> byteArrayOf(messageId.toByte())
            messageId < 0 -> {
                ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
                    .put(0xFF.toByte())
                    .put(0xFF.toByte())
                    .putShort((messageId and 0xFFFF).toShort())
                    .array()
            }
            else -> byteArrayOf(0xFF.toByte(), (messageId and 0xFF).toByte())
        }
    }
}
