package com.linkpoint.chat.encoder

import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ChatPacketEncoderTest {

    @Test
    fun `test spatial chat encoder 12-byte header and sequence increment`() {
        val encoder = ChatPacketEncoder(initialSequence = 100)
        assertEquals(100, encoder.getCurrentSequenceNumber())

        val message = "Hello spatial world"
        val packet = encoder.encodeSpatialChat(message, channel = 0)

        // Verify sequence increment
        assertEquals(100, packet.sequenceNumber)
        assertEquals(101, encoder.getCurrentSequenceNumber())

        // Verify header fields
        assertEquals(ChatPacketEncoder.SPATIAL_HEADER_FLAGS, packet.headerFlags)
        assertEquals(ChatPacketEncoder.TYPE_SPATIAL_CHAT, packet.channelTypeId)

        val payload = packet.payload
        val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

        // Byte 0: Flags
        assertEquals(ChatPacketEncoder.SPATIAL_HEADER_FLAGS, buffer.get())

        // Bytes 1..4: Sequence Number (Big Endian uint32)
        val seqBE = ((buffer.get().toInt() and 0xFF) shl 24) or
                ((buffer.get().toInt() and 0xFF) shl 16) or
                ((buffer.get().toInt() and 0xFF) shl 8) or
                (buffer.get().toInt() and 0xFF)
        assertEquals(100, seqBE)

        // Byte 5: Channel Type ID
        assertEquals(ChatPacketEncoder.TYPE_SPATIAL_CHAT, buffer.get())

        // Bytes 6..11: Session context (6 bytes zeros for spatial)
        val sessionBytes = ByteArray(6)
        buffer.get(sessionBytes)
        assertArrayEquals(ByteArray(6), sessionBytes)

        // Payload: U16 LE string length
        val strLen = buffer.short.toInt() and 0xFFFF
        val expectedLen = message.toByteArray(Charsets.UTF_8).size + 1
        assertEquals(expectedLen, strLen)

        // String bytes + NUL
        val msgBytes = ByteArray(strLen - 1)
        buffer.get(msgBytes)
        assertEquals(message, String(msgBytes, Charsets.UTF_8))
        assertEquals(0.toByte(), buffer.get()) // NUL byte

        // Channel ID (4 bytes LE)
        assertEquals(0, buffer.int)
    }

    @Test
    fun `test direct IM packet encoder session bytes and header framing`() {
        val sessionUuid = UUID.randomUUID()
        val encoder = IMPacketEncoder(initialSequence = 1, sessionUuid = sessionUuid)

        val message = "Direct message content"
        val packet = encoder.encodeDirectIM(message)

        assertEquals(IMPacketEncoder.IM_HEADER_FLAGS, packet.headerFlags)
        assertEquals(IMPacketEncoder.TYPE_DIRECT_IM, packet.channelTypeId)

        val expectedSessionBytes = IMPacketEncoder.uuidToSessionBytes(sessionUuid)
        assertArrayEquals(expectedSessionBytes, encoder.getChannelSessionBytes())

        val buffer = ByteBuffer.wrap(packet.payload).order(ByteOrder.LITTLE_ENDIAN)
        assertEquals(IMPacketEncoder.IM_HEADER_FLAGS, buffer.get())

        // Sequence number (1)
        val seqBE = ((buffer.get().toInt() and 0xFF) shl 24) or
                ((buffer.get().toInt() and 0xFF) shl 16) or
                ((buffer.get().toInt() and 0xFF) shl 8) or
                (buffer.get().toInt() and 0xFF)
        assertEquals(1, seqBE)

        assertEquals(IMPacketEncoder.TYPE_DIRECT_IM, buffer.get())

        val readSessionBytes = ByteArray(6)
        buffer.get(readSessionBytes)
        assertArrayEquals(expectedSessionBytes, readSessionBytes)
    }

    @Test
    fun `test group message encoder context and multi-byte unicode support`() {
        val groupUuid = UUID.fromString("00000000-0000-0000-0000-000000000001")
        val encoder = GroupMessageEncoder(initialSequence = 50, groupUuid = groupUuid)

        val unicodeMsg = "Group Chat Announcement! 📢 🔥"
        val packet = encoder.encodeGroupMessage(unicodeMsg, channel = 42)

        assertEquals(50, packet.sequenceNumber)
        assertEquals(GroupMessageEncoder.GROUP_HEADER_FLAGS, packet.headerFlags)
        assertEquals(GroupMessageEncoder.TYPE_GROUP_CHAT, packet.channelTypeId)
        assertEquals(42, packet.channel)

        val buffer = ByteBuffer.wrap(packet.payload).order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(12) // Skip 12-byte header

        val strLen = buffer.short.toInt() and 0xFFFF
        val expectedByteCount = unicodeMsg.toByteArray(Charsets.UTF_8).size + 1
        assertEquals(expectedByteCount, strLen)

        val msgBytes = ByteArray(strLen - 1)
        buffer.get(msgBytes)
        assertEquals(unicodeMsg, String(msgBytes, Charsets.UTF_8))
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
