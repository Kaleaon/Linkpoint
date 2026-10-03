package com.linkpoint.chat

import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.assertThrows

/**
 * Unit tests for ChatManager packet building logic
 * These tests verify buffer size calculations to prevent BufferOverflowException
 */
class ChatManagerTest {

    companion object {
        // ChatFromViewer packet structure sizes (per SL protocol):
        const val UUID_SIZE = 16        // LLUUID is 16 bytes
        const val VARIABLE2_PREFIX = 2   // Variable 2 uses 2-byte length prefix
        const val NULL_TERMINATOR = 1    // String null terminator
        const val TYPE_FIELD = 1         // Type (U8)
        const val CHANNEL_FIELD = 4      // Channel (S32)
        
        // Fixed overhead = AgentID (16) + SessionID (16) + length prefix (2) + null (1) + type (1) + channel (4)
        const val FIXED_OVERHEAD = UUID_SIZE + UUID_SIZE + VARIABLE2_PREFIX + NULL_TERMINATOR + TYPE_FIELD + CHANNEL_FIELD
        // = 16 + 16 + 2 + 1 + 1 + 4 = 40 bytes
    }
    
    @Test
    fun `test fixed overhead calculation equals 40 bytes`() {
        assertEquals("Fixed overhead should be 40 bytes", 40, FIXED_OVERHEAD)
    }
    
    @Test
    fun `test empty message buffer size should be 40 bytes`() {
        val messageBytes = "".toByteArray(Charsets.UTF_8)
        val requiredSize = FIXED_OVERHEAD + messageBytes.size
        
        assertEquals("Empty message should require 40 bytes", 40, requiredSize)
        
        // Verify buffer can hold all data without overflow
        val buffer = ByteBuffer.allocate(requiredSize).order(ByteOrder.LITTLE_ENDIAN)
        simulateChatPacketWrite(buffer, messageBytes)
        
        // Buffer should be completely full
        assertEquals("Buffer should be fully utilized", 0, buffer.remaining())
    }
    
    @Test
    fun `test short message buffer size calculation`() {
        val message = "Hello"
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        val requiredSize = FIXED_OVERHEAD + messageBytes.size
        
        assertEquals("'Hello' (5 bytes) should require 45 bytes total", 45, requiredSize)
        
        // Verify buffer can hold all data without overflow
        val buffer = ByteBuffer.allocate(requiredSize).order(ByteOrder.LITTLE_ENDIAN)
        simulateChatPacketWrite(buffer, messageBytes)
        
        assertEquals("Buffer should be fully utilized", 0, buffer.remaining())
    }
    
    @Test
    fun `test long message buffer size calculation`() {
        val message = "This is a longer chat message that contains more text"
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        val requiredSize = FIXED_OVERHEAD + messageBytes.size
        
        assertEquals(
            "Long message should require 40 + message length bytes",
            40 + messageBytes.size,
            requiredSize
        )
        
        // Verify buffer can hold all data without overflow
        val buffer = ByteBuffer.allocate(requiredSize).order(ByteOrder.LITTLE_ENDIAN)
        simulateChatPacketWrite(buffer, messageBytes)
        
        assertEquals("Buffer should be fully utilized", 0, buffer.remaining())
    }
    
    @Test
    fun `test unicode message buffer size uses UTF-8 byte count`() {
        // Unicode characters may require multiple bytes
        val message = "Hello 世界! 🌍"
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        
        // Verify that byte count differs from character count for unicode
        assertTrue(
            "UTF-8 byte count should be greater than character count for unicode",
            messageBytes.size > message.length
        )
        
        val requiredSize = FIXED_OVERHEAD + messageBytes.size
        
        // Verify buffer can hold all data without overflow
        val buffer = ByteBuffer.allocate(requiredSize).order(ByteOrder.LITTLE_ENDIAN)
        simulateChatPacketWrite(buffer, messageBytes)
        
        assertEquals("Buffer should be fully utilized", 0, buffer.remaining())
    }
    
    @Test
    fun `test maximum chat message size`() {
        // SL chat messages have a practical limit of 1024 bytes
        val maxMessageSize = 1024
        val message = "A".repeat(maxMessageSize)
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        
        assertEquals("ASCII message byte size should equal character count", maxMessageSize, messageBytes.size)
        
        val requiredSize = FIXED_OVERHEAD + messageBytes.size
        
        assertEquals(
            "Max size message should require 1064 bytes",
            FIXED_OVERHEAD + maxMessageSize,
            requiredSize
        )
        
        // Verify buffer can hold all data without overflow
        val buffer = ByteBuffer.allocate(requiredSize).order(ByteOrder.LITTLE_ENDIAN)
        simulateChatPacketWrite(buffer, messageBytes)
        
        assertEquals("Buffer should be fully utilized", 0, buffer.remaining())
    }
    
    @Test
    fun `test undersized buffer throws BufferOverflowException`() {
        val message = "Hello"
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        
        // Intentionally allocate one byte too small (the old bug: FIXED_OVERHEAD - 1 = 39)
        val undersizedBuffer = ByteBuffer.allocate(FIXED_OVERHEAD - 1 + messageBytes.size)
            .order(ByteOrder.LITTLE_ENDIAN)
        
        // This should throw BufferOverflowException
        assertThrows(java.nio.BufferOverflowException::class.java) {
            simulateChatPacketWrite(undersizedBuffer, messageBytes)
        }
    }
    
    /**
     * Simulates writing a chat packet to the buffer.
     * This matches the structure in ChatManager.buildChatPacket
     */
    private fun simulateChatPacketWrite(buffer: ByteBuffer, messageBytes: ByteArray) {
        // AgentID (16 bytes) - simulated with zeros
        repeat(16) { buffer.put(0) }
        
        // SessionID (16 bytes) - simulated with zeros  
        repeat(16) { buffer.put(0) }
        
        // Message length prefix (Variable 2: 2 bytes) - includes null terminator
        buffer.putShort((messageBytes.size + 1).toShort())
        
        // Message bytes
        buffer.put(messageBytes)
        
        // Null terminator
        buffer.put(0)
        
        // Type (U8)
        buffer.put(0)
        
        // Channel (S32)
        buffer.putInt(0)
    }
}
