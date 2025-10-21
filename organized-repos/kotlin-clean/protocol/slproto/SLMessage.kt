package com.linkpoint.slproto

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * SLMessage - Base class for all Second Life protocol messages
 * 
 * Messages can be:
 * - Reliable (acknowledged, retransmitted if lost)
 * - Unreliable (fire and forget, faster but may be lost)
 * 
 * Based on Firestorm's message system
 */
abstract class SLMessage {
    
    // Message metadata
    var sequenceNumber: Int = 0
    var isReliable: Boolean = true
    var isResent: Boolean = false
    var sentTimeMillis: Long = 0
    var retries: Int = 0
    
    /**
     * Get the message name (e.g. "AgentUpdate", "ObjectUpdate")
     */
    abstract fun getMessageName(): String
    
    /**
     * Encode this message to a ByteBuffer
     */
    abstract fun encode(buffer: ByteBuffer)
    
    /**
     * Decode this message from a ByteBuffer
     */
    abstract fun decode(buffer: ByteBuffer)
    
    /**
     * Handle the message (process it)
     */
    open fun handleMessage(circuit: SLCircuit) {
        // Default: route to message router
        circuit.DefaultMessageHandler(this)
    }
    
    /**
     * Called when message times out (no ack received)
     */
    open fun handleMessageTimeout() {
        // Default: log error
        com.linkpoint.Debug.Log("Message timeout: ${getMessageName()}")
    }
    
    /**
     * Check if this message is high frequency (8-bit ID)
     */
    open fun isHighFrequency(): Boolean = false
    
    /**
     * Check if this message is medium frequency (16-bit ID)
     */
    open fun isMediumFrequency(): Boolean = true
    
    /**
     * Check if this message is low frequency (32-bit ID)
     */
    open fun isLowFrequency(): Boolean = false
    
    companion object {
        
        // Message flags
        const val LL_ZERO_CODE_FLAG: Byte = 0x80.toByte()
        const val LL_RELIABLE_FLAG: Byte = 0x40
        const val LL_RESENT_FLAG: Byte = 0x20
        const val LL_ACK_FLAG: Byte = 0x10
        
        /**
         * Convert string to UTF-8 bytes with length prefix
         */
        @JvmStatic
        fun stringToVariableUTF(str: String?): ByteArray {
            if (str == null) return ByteArray(0)
            return str.toByteArray(StandardCharsets.UTF_8)
        }
        
        /**
         * Write a variable-length field to buffer
         * Format: 1-byte length, then data
         */
        @JvmStatic
        fun writeVariableField(buffer: ByteBuffer, data: ByteArray) {
            buffer.put(data.size.toByte())
            buffer.put(data)
        }
        
        /**
         * Write a variable-length string to buffer
         */
        @JvmStatic
        fun writeVariableString(buffer: ByteBuffer, str: String?) {
            val bytes = stringToVariableUTF(str ?: "")
            writeVariableField(buffer, bytes)
        }
        
        /**
         * Read a variable-length field from buffer
         */
        @JvmStatic
        fun readVariableField(buffer: ByteBuffer): ByteArray {
            val length = buffer.get().toInt() and 0xFF
            val data = ByteArray(length)
            buffer.get(data)
            return data
        }
        
        /**
         * Read a variable-length string from buffer
         */
        @JvmStatic
        fun readVariableString(buffer: ByteBuffer): String {
            val bytes = readVariableField(buffer)
            return String(bytes, StandardCharsets.UTF_8)
        }
        
        /**
         * Write UUID to buffer (16 bytes)
         */
        @JvmStatic
        fun writeUUID(buffer: ByteBuffer, uuid: UUID) {
            buffer.putLong(uuid.mostSignificantBits)
            buffer.putLong(uuid.leastSignificantBits)
        }
        
        /**
         * Read UUID from buffer (16 bytes)
         */
        @JvmStatic
        fun readUUID(buffer: ByteBuffer): UUID {
            val mostSig = buffer.getLong()
            val leastSig = buffer.getLong()
            return UUID(mostSig, leastSig)
        }
        
        /**
         * Write IP address to buffer (4 bytes)
         */
        @JvmStatic
        fun writeIPAddress(buffer: ByteBuffer, ip: ByteArray) {
            if (ip.size == 4) {
                buffer.put(ip)
            } else {
                buffer.putInt(0) // Invalid IP
            }
        }
        
        /**
         * Read IP address from buffer (4 bytes)
         */
        @JvmStatic
        fun readIPAddress(buffer: ByteBuffer): ByteArray {
            val ip = ByteArray(4)
            buffer.get(ip)
            return ip
        }
    }
}

/**
 * Simple message for testing
 */
class TestMessage : SLMessage() {
    var testData: String = ""
    
    override fun getMessageName() = "TestMessage"
    
    override fun encode(buffer: ByteBuffer) {
        writeVariableString(buffer, testData)
    }
    
    override fun decode(buffer: ByteBuffer) {
        testData = readVariableString(buffer)
    }
}
