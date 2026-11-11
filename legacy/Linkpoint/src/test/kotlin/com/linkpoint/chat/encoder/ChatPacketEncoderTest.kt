package com.linkpoint.chat.encoder

import com.linkpoint.linden.llmessage.IMType
import com.linkpoint.protocol.core.AgentIdentity
import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ChatPacketEncoderTest {

    @Test
    fun `test spatial chat encoder 32-byte header and sequence increment`() {
        val identity = AgentIdentity(
            agentId = UUID.fromString("11111111-2222-3333-4444-555555555555"),
            sessionId = UUID.fromString("66666666-7777-8888-9999-000000000000")
        )
        val encoder = ChatPacketEncoder(initialSequence = 100, agentIdentity = identity)
        assertEquals(100, encoder.getCurrentSequenceNumber())

        val message = "Hello spatial world"
        val packet = encoder.encodeSpatialChat(message, channel = 0)

        // Verify sequence increment
        assertEquals(100, packet.sequenceNumber)
        assertEquals(101, encoder.getCurrentSequenceNumber())

        // Verify header fields on EncodedChatPacket metadata
        assertEquals(ChatPacketEncoder.SPATIAL_HEADER_FLAGS, packet.headerFlags)
        assertEquals(ChatPacketEncoder.TYPE_SPATIAL_CHAT, packet.channelTypeId)

        val payload = packet.payload
        assertTrue("Payload length should be at least 32 bytes for AgentData header", payload.size >= 32)
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // 32-byte AgentData header verification (Big-Endian UUIDs)
        val bufferBE = ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN)
        val agentIdRead = UUID(bufferBE.long, bufferBE.long)
        val sessionIdRead = UUID(bufferBE.long, bufferBE.long)

        assertEquals(identity.agentId, agentIdRead)
        assertEquals(identity.sessionId, sessionIdRead)

        // ChatData verification (position 32)
        buffer.position(32)
        val strLen = buffer.short.toInt() and 0xFFFF
        val expectedLen = message.toByteArray(Charsets.UTF_8).size + 1
        assertEquals(expectedLen, strLen)

        val msgBytes = ByteArray(strLen - 1)
        buffer.get(msgBytes)
        assertEquals(message, String(msgBytes, Charsets.UTF_8))
        assertEquals(0.toByte(), buffer.get()) // NUL byte

        // Chat type (1 byte) and Channel ID (4 bytes LE int)
        assertEquals(ChatPacketEncoder.TYPE_SPATIAL_CHAT, buffer.get())
        assertEquals(0, buffer.int)
    }

    @Test
    fun `test direct IM packet encoder session bytes and header framing`() {
        val identity = AgentIdentity(
            agentId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
            sessionId = UUID.fromString("12345678-1234-1234-1234-123456789abc")
        )
        val sessionUuid = UUID.fromString("98765432-4321-4321-4321-cba987654321")
        val encoder = IMPacketEncoder(initialSequence = 1, agentIdentity = identity, sessionUuid = sessionUuid)

        val message = "Direct message content"
        val packet = encoder.encodeDirectIM(message)

        assertEquals(IMPacketEncoder.IM_HEADER_FLAGS, packet.headerFlags)
        assertEquals(IMPacketEncoder.TYPE_DIRECT_IM, packet.channelTypeId)
        assertEquals(sessionUuid, packet.sessionUuid)

        val expectedSessionBytes = IMPacketEncoder.uuidToSessionBytes(sessionUuid)
        assertEquals(16, expectedSessionBytes.size)
        assertArrayEquals(expectedSessionBytes, encoder.getChannelSessionBytes())

        val payload = packet.payload
        assertTrue("Payload length should be at least 32 bytes for AgentData header", payload.size >= 32)

        val bufferBE = ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN)
        val agentIdRead = UUID(bufferBE.long, bufferBE.long)
        val sessionIdRead = UUID(bufferBE.long, bufferBE.long)
        assertEquals(identity.agentId, agentIdRead)
        assertEquals(identity.sessionId, sessionIdRead)

        val bufferLE = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)
        bufferLE.position(32) // MessageBlock start

        // Byte 32: fromGroup (0)
        assertEquals(0.toByte(), bufferLE.get())

        // Bytes 33..48: toAgentId (16-byte BE UUID matching target sessionUuid)
        bufferBE.position(33)
        val toAgentIdRead = UUID(bufferBE.long, bufferBE.long)
        assertEquals(sessionUuid, toAgentIdRead)

        // Verify Dialog type byte at offset 82
        assertEquals(IMType.NOTHING_SPECIAL.value.toByte(), payload[82])
    }

    @Test
    fun `test group message encoder context and multi-byte unicode support`() {
        val identity = AgentIdentity(
            agentId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            sessionId = UUID.fromString("00000000-0000-0000-0000-000000000002")
        )
        val groupUuid = UUID.fromString("fe001122-3344-5566-7788-99aabbccdde0")
        val encoder = GroupMessageEncoder(initialSequence = 50, agentIdentity = identity, groupUuid = groupUuid)

        val unicodeMsg = "Group Chat Announcement! 📢 🔥"
        val packet = encoder.encodeGroupMessage(unicodeMsg, channel = 42)

        assertEquals(50, packet.sequenceNumber)
        assertEquals(GroupMessageEncoder.GROUP_HEADER_FLAGS, packet.headerFlags)
        assertEquals(GroupMessageEncoder.TYPE_GROUP_CHAT, packet.channelTypeId)
        assertEquals(groupUuid, packet.groupUuid)

        val payload = packet.payload
        val bufferBE = ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN)

        bufferBE.position(33)
        val toAgentIdRead = UUID(bufferBE.long, bufferBE.long)
        assertEquals(groupUuid, toAgentIdRead)

        // Verify Dialog = IMType.SESSION_SEND (17)
        assertEquals(IMType.SESSION_SEND.value.toByte(), payload[82])
    }

    @Test
    fun `test full 128-bit session and group UUID preservation without truncation`() {
        val arbitraryUuid = UUID(0x123456789ABCDEF0L, -0x0123456789ABCDEFL)
        val sessionBytes = IMPacketEncoder.uuidToSessionBytes(arbitraryUuid)
        assertEquals(16, sessionBytes.size)

        val reconstructedUuid = IMPacketEncoder.sessionBytesToUuid(sessionBytes)
        assertEquals(arbitraryUuid, reconstructedUuid)

        val groupBytes = GroupMessageEncoder.groupUuidToBytes(arbitraryUuid)
        assertEquals(16, groupBytes.size)

        val reconstructedGroupUuid = GroupMessageEncoder.groupBytesToUuid(groupBytes)
        assertEquals(arbitraryUuid, reconstructedGroupUuid)
    }

    @Test
    fun `test thread safe sequence incrementing`() {
        val encoder = ChatPacketEncoder(initialSequence = 1)
        val threadCount = 10
        val iterationsPerThread = 100
        val executor = Executors.newFixedThreadPool(threadCount)

        for (i in 0 until threadCount) {
            executor.submit {
                for (j in 0 until iterationsPerThread) {
                    encoder.encodeSpatialChat("Msg $i-$j")
                }
            }
        }

        executor.shutdown()
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS))

        val finalSeq = encoder.getCurrentSequenceNumber()
        assertEquals(1 + (threadCount * iterationsPerThread), finalSeq)
    }

    @Test
    fun `test ChatNetworkService dispatcher pipeline and listener callbacks`() {
        val service = ChatNetworkService()
        val dispatchedPackets = mutableListOf<EncodedChatPacket>()

        service.registerListener { packet ->
            dispatchedPackets.add(packet)
        }

        val sessionUuid = UUID.randomUUID()
        val groupUuid = UUID.randomUUID()

        val spatial = service.dispatchSpatialChat("Spatial test", channel = 0)
        val im = service.dispatchDirectIM("IM test", sessionUuid = sessionUuid)
        val group = service.dispatchGroupMessage("Group test", groupUuid = groupUuid)

        assertEquals(3, dispatchedPackets.size)
        assertEquals(spatial, dispatchedPackets[0])
        assertEquals(im, dispatchedPackets[1])
        assertEquals(group, dispatchedPackets[2])
    }

    @Test
    fun `test oversized payload throws IllegalArgumentException`() {
        val encoder = ChatPacketEncoder()
        val longMessage = "A".repeat(AbstractChatPacketEncoder.MAX_PAYLOAD_SIZE + 10)

        assertThrows(IllegalArgumentException::class.java) {
            encoder.encodeSpatialChat(longMessage)
        }
    }
}
