package com.linkpoint.protocol.messages

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Regression tests for `MessageParser.extractPayload` honoring the
 * LLUDP "extra header" byte at offset 5.
 *
 * Header layout per `SLMessage.Pack` and the SL viewer reference:
 *   byte 0    flags
 *   bytes 1-4 sequence number (BE)
 *   byte 5    extra-header length N (0..255)
 *   bytes 6..(6+N-1) extra-header bytes (uninterpreted by Linkpoint)
 *   bytes 6+N..  message ID prefix + payload
 *
 * Before the fix `extractPayload` started reading the message ID at
 * offset 6 unconditionally, so packets where the simulator chose to
 * include extra-header bytes had their ID misread and their payload
 * silently dropped from the parsed handler chain.
 */
class MessageParserExtraHeaderTest {

    @Test
    fun `extractPayload skips zero-length extra header (common case)`() {
        // 6-byte header (flags=0, seq=1, extraHeader=0)
        // Then high-frequency message ID 0x04 (AgentUpdate)
        // Then 4 bytes of payload
        val packet = byteArrayOf(
            0x00, 0x00, 0x00, 0x00, 0x01, 0x00,  // header
            0x04,                                  // msg ID (high freq)
            0x11, 0x22, 0x33, 0x44                 // payload
        )
        val payload = MessageParser.extractPayload(packet)
        assertArrayEquals(byteArrayOf(0x11, 0x22, 0x33, 0x44), payload)
    }

    @Test
    fun `extractPayload skips non-zero extra header bytes`() {
        // 6-byte header with extraHeader=3
        // Three extra-header bytes (uninterpreted)
        // Then high-freq message ID 0x04
        // Then payload
        val packet = byteArrayOf(
            0x00, 0x00, 0x00, 0x00, 0x01,
            0x03,                                  // extra-header length = 3
            0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(),  // 3 extra-header bytes
            0x04,                                  // msg ID
            0x11, 0x22, 0x33, 0x44
        )
        val payload = MessageParser.extractPayload(packet)
        assertArrayEquals(
            "Payload extracted past extra-header bytes",
            byteArrayOf(0x11, 0x22, 0x33, 0x44),
            payload
        )
    }

    @Test
    fun `extractPayload returns null for truncated packet past extra-header`() {
        // extraHeader=5 but only 6 bytes in packet — no room for message ID.
        val packet = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x01, 0x05)
        assertNull(MessageParser.extractPayload(packet))
    }

    @Test
    fun `extractPayload skips extra header before low-frequency message ID`() {
        // extraHeader=2, then low-freq prefix 0xFF 0xFF + 2-byte ID, then payload
        val packet = byteArrayOf(
            0x40, 0x00, 0x00, 0x00, 0x01, 0x02,
            0x77, 0x88.toByte(),                  // extra-header
            0xFF.toByte(), 0xFF.toByte(),         // low-freq prefix
            0x00, 0x50,                           // ID = 80 (ChatFromViewer)
            0xDE.toByte(), 0xAD.toByte()          // 2-byte payload
        )
        val payload = MessageParser.extractPayload(packet)
        assertArrayEquals(byteArrayOf(0xDE.toByte(), 0xAD.toByte()), payload)
    }
}
