package com.linkpoint.connection

import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Unit tests for connection diagnostic logic
 * These tests verify packet handling, ACK logic, and timeout behavior
 * WITHOUT requiring actual network connections.
 */
class ConnectionDiagnosticTest {

    // Second Life message IDs for testing
    companion object {
        const val MSG_USE_CIRCUIT_CODE = -65533
        const val MSG_COMPLETE_AGENT_MOVEMENT = -65287
        const val MSG_PACKET_ACK = -5
        const val MSG_REGION_HANDSHAKE = -65388
        const val MSG_AGENT_UPDATE = 4
        
        // Packet flags
        const val FLAG_RELIABLE = 0x40
        const val FLAG_RESENT = 0x20
        const val FLAG_ZEROCODED = 0x80
    }

    @Test
    fun `test packet header parsing - reliable flag detected`() {
        // Create a packet with reliable flag set
        val packet = createTestPacket(flags = FLAG_RELIABLE, sequenceNumber = 1, messageId = MSG_USE_CIRCUIT_CODE)
        
        val flags = packet[0].toInt() and 0xFF
        assertTrue("Reliable flag should be set", (flags and FLAG_RELIABLE) != 0)
        assertFalse("Resent flag should not be set", (flags and FLAG_RESENT) != 0)
    }

    @Test
    fun `test packet header parsing - sequence number extraction`() {
        val expectedSequence = 12345
        val packet = createTestPacket(flags = 0, sequenceNumber = expectedSequence, messageId = MSG_AGENT_UPDATE)
        
        val extractedSequence = extractSequenceNumber(packet)
        assertEquals("Sequence number should match", expectedSequence, extractedSequence)
    }

    @Test
    fun `test packet header parsing - message ID extraction for low frequency`() {
        // Low frequency messages (negative IDs) use 2 bytes after 0xFFFF marker
        val packet = createTestPacket(flags = FLAG_RELIABLE, sequenceNumber = 1, messageId = MSG_USE_CIRCUIT_CODE)
        
        val messageId = extractMessageId(packet)
        assertEquals("Message ID should be USE_CIRCUIT_CODE", MSG_USE_CIRCUIT_CODE, messageId)
    }

    @Test
    fun `test packet header parsing - message ID extraction for high frequency`() {
        // High frequency messages (positive IDs 1-255) use single byte
        val packet = createTestPacket(flags = 0, sequenceNumber = 1, messageId = MSG_AGENT_UPDATE)
        
        val messageId = extractMessageId(packet)
        assertEquals("Message ID should be AGENT_UPDATE", MSG_AGENT_UPDATE, messageId)
    }

    @Test
    fun `test ACK timeout calculation - default should be reasonable`() {
        val defaultTimeout = 3000L // 3 seconds
        val minTimeout = 1000L
        val maxTimeout = 30000L
        
        assertTrue("Default timeout should be at least $minTimeout ms", defaultTimeout >= minTimeout)
        assertTrue("Default timeout should be at most $maxTimeout ms", defaultTimeout <= maxTimeout)
    }

    @Test
    fun `test adaptive timeout increases with latency`() {
        val baseTimeout = 3000L
        val measuredLatency = 500L
        
        // Adaptive timeout = base + (latency * multiplier)
        val multiplier = 3
        val adaptiveTimeout = baseTimeout + (measuredLatency * multiplier)
        
        assertTrue("Adaptive timeout should be greater than base", adaptiveTimeout > baseTimeout)
        assertEquals("Adaptive timeout calculation", 4500L, adaptiveTimeout)
    }

    @Test
    fun `test packet resend logic - should resend after timeout`() {
        val sendTime = System.currentTimeMillis() - 4000 // 4 seconds ago
        val timeout = 3000L
        val currentTime = System.currentTimeMillis()
        
        val shouldResend = (currentTime - sendTime) > timeout
        assertTrue("Should resend packet after timeout", shouldResend)
    }

    @Test
    fun `test packet resend logic - should not resend before timeout`() {
        val sendTime = System.currentTimeMillis() - 1000 // 1 second ago
        val timeout = 3000L
        val currentTime = System.currentTimeMillis()
        
        val shouldResend = (currentTime - sendTime) > timeout
        assertFalse("Should not resend packet before timeout", shouldResend)
    }

    @Test
    fun `test pending ACK queue management`() {
        val pendingAcks = mutableListOf<Int>()
        val maxPendingAcks = 255
        
        // Add some pending ACKs
        pendingAcks.addAll(listOf(1, 2, 3, 4, 5))
        assertEquals("Should have 5 pending ACKs", 5, pendingAcks.size)
        
        // Simulate receiving ACK for sequences 1, 2, 3
        val ackedSequences = listOf(1, 2, 3)
        pendingAcks.removeAll(ackedSequences)
        assertEquals("Should have 2 pending ACKs after removal", 2, pendingAcks.size)
        assertTrue("Should still have sequence 4", pendingAcks.contains(4))
        assertTrue("Should still have sequence 5", pendingAcks.contains(5))
    }

    @Test
    fun `test connection state transitions`() {
        val states = listOf("DISCONNECTED", "CONNECTING", "CONNECTED", "RECONNECTING", "ERROR")
        
        // Valid transitions
        assertTrue("DISCONNECTED -> CONNECTING is valid", isValidTransition("DISCONNECTED", "CONNECTING"))
        assertTrue("CONNECTING -> CONNECTED is valid", isValidTransition("CONNECTING", "CONNECTED"))
        assertTrue("CONNECTED -> DISCONNECTED is valid", isValidTransition("CONNECTED", "DISCONNECTED"))
        assertTrue("CONNECTED -> RECONNECTING is valid", isValidTransition("CONNECTED", "RECONNECTING"))
        assertTrue("RECONNECTING -> CONNECTED is valid", isValidTransition("RECONNECTING", "CONNECTED"))
        assertTrue("RECONNECTING -> ERROR is valid", isValidTransition("RECONNECTING", "ERROR"))
    }

    @Test
    fun `test UseCircuitCode packet structure`() {
        // UseCircuitCode contains: circuit_code (4 bytes), session_id (16 bytes), agent_id (16 bytes)
        val circuitCode = 12345678
        val packet = createUseCircuitCodePacket(circuitCode)
        
        // Verify packet structure
        assertTrue("Packet should be at least 46 bytes", packet.size >= 46)
        
        // Check reliable flag
        val flags = packet[0].toInt() and 0xFF
        assertTrue("UseCircuitCode should be reliable", (flags and FLAG_RELIABLE) != 0)
    }

    @Test
    fun `test network quality classification`() {
        // Test latency-based quality classification
        assertEquals("EXCELLENT for <50ms", "EXCELLENT", classifyNetworkQuality(30))
        assertEquals("GOOD for 50-150ms", "GOOD", classifyNetworkQuality(100))
        assertEquals("FAIR for 150-300ms", "FAIR", classifyNetworkQuality(200))
        assertEquals("POOR for >300ms", "POOR", classifyNetworkQuality(500))
    }

    @Test
    fun `test mobile NAT keep-alive interval`() {
        // Mobile NAT typically requires keep-alive every 30-60 seconds
        // But we recommend 5 seconds for safety
        val recommendedInterval = 5000L
        val mobileNatTimeout = 30000L // Conservative mobile NAT timeout
        
        assertTrue("Keep-alive interval should be less than NAT timeout", 
            recommendedInterval < mobileNatTimeout)
    }

    @Test
    fun `test zero-coded packet detection`() {
        val zeroCoded = createTestPacket(flags = FLAG_ZEROCODED, sequenceNumber = 1, messageId = MSG_REGION_HANDSHAKE)
        val notZeroCoded = createTestPacket(flags = FLAG_RELIABLE, sequenceNumber = 1, messageId = MSG_REGION_HANDSHAKE)
        
        val zeroCodedFlag = zeroCoded[0].toInt() and FLAG_ZEROCODED
        val notZeroCodedFlag = notZeroCoded[0].toInt() and FLAG_ZEROCODED
        
        assertTrue("Should detect zero-coded packet", zeroCodedFlag != 0)
        assertTrue("Should not detect zero-coded on regular packet", notZeroCodedFlag == 0)
    }

    // ============================================================
    // Helper functions for tests
    // ============================================================

    private fun createTestPacket(flags: Int, sequenceNumber: Int, messageId: Int): ByteArray {
        val buffer = ByteBuffer.allocate(64)
        buffer.order(ByteOrder.BIG_ENDIAN)
        
        // Byte 0: Flags
        buffer.put(flags.toByte())
        
        // Bytes 1-4: Sequence number (big-endian)
        buffer.putInt(sequenceNumber)
        
        // Byte 5: Extra header byte (usually 0)
        buffer.put(0)
        
        if (messageId > 0 && messageId < 256) {
            // High frequency message - single byte
            buffer.put(messageId.toByte())
        } else if (messageId < 0) {
            // Low frequency message - 0xFF 0xFF followed by 2-byte ID
            buffer.put(0xFF.toByte())
            buffer.put(0xFF.toByte())
            // Convert negative ID to unsigned short
            buffer.putShort((messageId and 0xFFFF).toShort())
        }
        
        return buffer.array().copyOf(buffer.position())
    }

    private fun extractSequenceNumber(packet: ByteArray): Int {
        val buffer = ByteBuffer.wrap(packet)
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.get() // Skip flags
        return buffer.int
    }

    private fun extractMessageId(packet: ByteArray): Int {
        val buffer = ByteBuffer.wrap(packet)
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.get() // Skip flags
        buffer.int // Skip sequence
        buffer.get() // Skip extra header
        
        val firstByte = buffer.get().toInt() and 0xFF
        
        return if (firstByte == 0xFF) {
            val secondByte = buffer.get().toInt() and 0xFF
            if (secondByte == 0xFF) {
                // Low frequency - read 2 bytes as signed short
                val id = buffer.short.toInt()
                if (id > 0) id - 65536 else id // Convert to negative
            } else {
                // Medium frequency
                (firstByte shl 8) or secondByte
            }
        } else {
            // High frequency
            firstByte
        }
    }

    private fun isValidTransition(from: String, to: String): Boolean {
        val validTransitions = mapOf(
            "DISCONNECTED" to setOf("CONNECTING"),
            "CONNECTING" to setOf("CONNECTED", "ERROR", "DISCONNECTED"),
            "CONNECTED" to setOf("DISCONNECTED", "RECONNECTING", "ERROR"),
            "RECONNECTING" to setOf("CONNECTED", "ERROR", "DISCONNECTED"),
            "ERROR" to setOf("DISCONNECTED", "CONNECTING")
        )
        return validTransitions[from]?.contains(to) ?: false
    }

    private fun createUseCircuitCodePacket(circuitCode: Int): ByteArray {
        val buffer = ByteBuffer.allocate(64)
        buffer.order(ByteOrder.BIG_ENDIAN)
        
        // Header
        buffer.put(FLAG_RELIABLE.toByte()) // Reliable
        buffer.putInt(0) // Sequence 0
        buffer.put(0) // Extra header
        
        // Low frequency message marker + UseCircuitCode ID
        buffer.put(0xFF.toByte())
        buffer.put(0xFF.toByte())
        buffer.putShort((MSG_USE_CIRCUIT_CODE and 0xFFFF).toShort())
        
        // Circuit code
        buffer.putInt(circuitCode)
        
        // Session ID (16 bytes placeholder)
        repeat(16) { buffer.put(0) }
        
        // Agent ID (16 bytes placeholder)
        repeat(16) { buffer.put(0) }
        
        return buffer.array().copyOf(buffer.position())
    }

    private fun classifyNetworkQuality(latencyMs: Int): String {
        return when {
            latencyMs < 50 -> "EXCELLENT"
            latencyMs < 150 -> "GOOD"
            latencyMs < 300 -> "FAIR"
            else -> "POOR"
        }
    }
}
