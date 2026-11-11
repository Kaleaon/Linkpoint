package com.linkpoint.protocol

import com.linkpoint.protocol.binary.SLPacketCodec
import com.linkpoint.protocol.binary.SLPacketFlags
import com.linkpoint.protocol.binary.SLPacketFrequency
import com.linkpoint.protocol.messages.ObjectFlags
import com.linkpoint.protocol.messages.RegionFlags
import com.linkpoint.protocol.spatial.PackedQuaternion
import com.linkpoint.protocol.spatial.Vector3U16
import com.linkpoint.protocol.spatial.Vector3U8
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CrossLanguagePacketSpatialTest {

    @Test
    @DisplayName("Verify SLPacketFlags match protocol standards")
    fun testSLPacketFlags() {
        assertEquals(0x80, SLPacketFlags.ZEROCODED)
        assertEquals(0x40, SLPacketFlags.RELIABLE)
        assertEquals(0x20, SLPacketFlags.RESENT)
        assertEquals(0x10, SLPacketFlags.APPENDED_ACKS)
    }

    @Test
    @DisplayName("Verify Packet Header parsing with extra bytes, frequencies, and appended ACKs")
    fun testHeaderParsing() {
        // High frequency
        val highFreq = byteArrayOf(
            0x80.toByte(), // Zerocoded
            0x00, 0x00, 0x00, 0x64, // Sequence = 100
            0x00, // Extra len = 0
            0x04 // High freq MsgID = 4
        )
        val h1 = SLPacketCodec.parseHeader(highFreq)
        assertTrue(h1.isZerocoded)
        assertEquals(100L, h1.sequenceNumber)
        assertEquals(SLPacketFrequency.HIGH, h1.frequency)
        assertEquals(4, h1.messageId)

        // Low frequency with appended ACKs
        val lowFreq = byteArrayOf(
            0x10.toByte(), // Appended ACKs
            0x00, 0x00, 0x00, 0x66, // Sequence = 102
            0x00, // Extra len = 0
            0xFF.toByte(), 0xFF.toByte(), 0x12, 0x34, // Low freq MsgID = 0x1234
            0x11, 0x22, 0x33, 0x44, // Payload
            0x00, 0x00, 0x00, 0x07, // ACK 7
            0x00, 0x00, 0x00, 0x09, // ACK 9
            0x02 // Ack count = 2
        )
        val h2 = SLPacketCodec.parseHeader(lowFreq)
        assertTrue(h2.hasAppendedAcks)
        assertEquals(102L, h2.sequenceNumber)
        assertEquals(SLPacketFrequency.LOW, h2.frequency)
        assertEquals(0x1234, h2.messageId)
        assertEquals(listOf(7L, 9L), h2.acks)
    }

    @Test
    @DisplayName("Verify Vector3U16 and Vector3U8 quantization within 0.01 accuracy")
    fun testVectorQuantization() {
        val u16Deq = Vector3U16.dequantize(0, 32767, 65535)
        assertEquals(-128.0f, u16Deq[0], 0.01f)
        assertEquals(0.0f, u16Deq[1], 0.01f)
        assertEquals(128.0f, u16Deq[2], 0.01f)

        val u8Deq = Vector3U8.dequantize(0, 128, 255)
        assertEquals(0.0f, u8Deq[0], 0.01f)
        assertEquals(128.0f, u8Deq[1], 0.5f)
        assertEquals(255.0f, u8Deq[2], 0.01f)
    }

    @Test
    @DisplayName("Verify 16-bit Packed Quaternion reconstruction")
    fun testPackedQuaternion() {
        val q1 = PackedQuaternion.unpack16(0, 0, 0)
        assertEquals(0.0f, q1[0], 0.001f)
        assertEquals(0.0f, q1[1], 0.001f)
        assertEquals(0.0f, q1[2], 0.001f)
        assertEquals(1.0f, q1[3], 0.001f)

        val q2 = PackedQuaternion.unpack16(0, 0, 23170)
        assertEquals(0.0f, q2[0], 0.01f)
        assertEquals(0.0f, q2[1], 0.01f)
        assertEquals(0.7071f, q2[2], 0.01f)
        assertEquals(0.7071f, q2[3], 0.01f)
    }

    @Test
    @DisplayName("Verify bitfield flags for RegionFlags and ObjectFlags")
    fun testBitfieldFlags() {
        val rFlags = RegionFlags.ALLOW_DAMAGE or RegionFlags.ALLOW_VOICE
        assertTrue(RegionFlags.hasFlag(rFlags, RegionFlags.ALLOW_DAMAGE))
        assertFalse(RegionFlags.hasFlag(rFlags, RegionFlags.IS_SANDBOX))

        val oFlags = ObjectFlags.PHYSICS or ObjectFlags.PHANTOM
        assertTrue(ObjectFlags.hasFlag(oFlags, ObjectFlags.PHYSICS))
        assertFalse(ObjectFlags.hasFlag(oFlags, ObjectFlags.CAST_SHADOWS))
    }
}
