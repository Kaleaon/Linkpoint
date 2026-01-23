package com.linkpoint.protocol.translation

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Message translation utilities for Second Life protocol compatibility.
 * 
 * This handles the differences in how message IDs are encoded/decoded between
 * Linkpoint's Kotlin implementation and Lumiya's original Java implementation.
 * 
 * Second Life uses three frequency classes for messages:
 * - **High frequency**: Single byte, values 1-254 (0xFF is reserved as escape)
 * - **Medium frequency**: 0xFF prefix + byte, resulting in values 65280-65534
 * - **Low frequency**: 0xFF 0xFF prefix + short, resulting in negative values
 * 
 * Lumiya's DecodeMessageID() uses signed bytes/shorts which affects the resulting IDs:
 * ```java
 * public static int DecodeMessageID(ByteBuffer byteBuffer) {
 *     byte b = byteBuffer.get();
 *     if (b != -1) {
 *         return b;  // High frequency: signed byte
 *     }
 *     byte b2 = byteBuffer.get();
 *     return b2 != -1 ? b2 | 65280 : byteBuffer.getShort() | (-65536);
 * }
 * ```
 * 
 * @see com.linkpoint.protocol.messages.UDPConnection
 * @see com.linkpoint.protocol.messages.MessageIds
 */
object MessageTranslation {
    
    private const val TAG = "MessageTranslation"
    
    // Message frequency constants matching Lumiya's implementation
    private const val ESCAPE_BYTE: Byte = 0xFF.toByte() // -1 as signed byte
    private const val MEDIUM_FREQUENCY_BASE = 65280
    private const val LOW_FREQUENCY_BASE = -65536
    private const val HIGH_FREQUENCY_THRESHOLD = -128
    
    /**
     * Message frequency classification.
     */
    enum class MessageFrequency {
        /** Single byte message ID (1-254) */
        HIGH,
        /** Two byte message ID (0xFF + byte) */
        MEDIUM,
        /** Four byte message ID (0xFF 0xFF + short) */
        LOW,
        /** Invalid or unknown frequency */
        UNKNOWN
    }
    
    /**
     * Decode a message ID from raw packet bytes using Lumiya-compatible logic.
     * 
     * This exactly matches Lumiya's SLMessage.DecodeMessageID() behavior:
     * - Reads the first byte as a signed byte
     * - If not -1 (0xFF), returns it as the high frequency ID
     * - If -1, reads next byte; if not -1, returns (byte | 65280) as medium frequency
     * - If both are -1, reads a short and returns (short | -65536) as low frequency
     * 
     * @param data The raw packet data
     * @param offset The offset where the message ID starts (after packet header)
     * @return A pair of (messageId, newOffset) or null if decoding failed
     */
    fun decodeMessageId(data: ByteArray, offset: Int): Pair<Int, Int>? {
        if (data.size <= offset) {
            Log.w(TAG, "Data too short for message ID at offset $offset")
            return null
        }
        
        var pos = offset
        
        // First byte - treat as signed (Java/Kotlin default)
        val b1 = data[pos].toInt() // Signed byte: -128 to 127
        pos++
        
        // High frequency: not -1 (0xFF)
        if (b1 != -1) {
            return Pair(b1, pos)
        }
        
        // Check for second byte
        if (data.size <= pos) {
            Log.w(TAG, "Data too short for medium frequency message ID")
            return null
        }
        
        // Second byte
        val b2 = data[pos].toInt() // Signed byte
        pos++
        
        // Medium frequency: second byte is not -1
        if (b2 != -1) {
            return Pair(b2 or MEDIUM_FREQUENCY_BASE, pos)
        }
        
        // Low frequency: need two more bytes for the short
        if (data.size <= pos + 1) {
            Log.w(TAG, "Data too short for low frequency message ID")
            return null
        }
        
        // Read short in big-endian (network byte order)
        // Each byte is masked with 0xFF to get unsigned value
        val highByte = data[pos].toInt() and 0xFF
        val lowByte = data[pos + 1].toInt() and 0xFF
        pos += 2
        
        // Combine bytes into a 16-bit value
        val rawShort = (highByte shl 8) or lowByte
        
        // Convert to signed short (to match Lumiya's Java behavior)
        // then back to Int for the final message ID calculation
        val signedShortValue = rawShort.toShort().toInt()
        
        return Pair(signedShortValue or LOW_FREQUENCY_BASE, pos)
    }
    
    /**
     * Encode a message ID for transmission using Lumiya-compatible logic.
     * 
     * This is the inverse of decodeMessageId().
     * 
     * @param messageId The message ID to encode
     * @return The encoded byte array for the message ID
     */
    fun encodeMessageId(messageId: Int): ByteArray {
        return when {
            // Low frequency: negative values less than -128
            messageId < HIGH_FREQUENCY_THRESHOLD -> {
                val shortValue = messageId and 0xFFFF
                byteArrayOf(
                    ESCAPE_BYTE,
                    ESCAPE_BYTE,
                    ((shortValue shr 8) and 0xFF).toByte(),
                    (shortValue and 0xFF).toByte()
                )
            }
            // Medium frequency: 65280-65534
            messageId in MEDIUM_FREQUENCY_BASE..65534 -> {
                byteArrayOf(ESCAPE_BYTE, (messageId and 0xFF).toByte())
            }
            // High frequency: -128 to 254 (excluding -1)
            else -> {
                byteArrayOf(messageId.toByte())
            }
        }
    }
    
    /**
     * Determine the frequency class of a message ID.
     * 
     * @param messageId The message ID
     * @return The frequency class
     */
    fun getMessageFrequency(messageId: Int): MessageFrequency {
        return when {
            messageId < HIGH_FREQUENCY_THRESHOLD -> MessageFrequency.LOW
            messageId in MEDIUM_FREQUENCY_BASE..65534 -> MessageFrequency.MEDIUM
            messageId in -128..254 -> MessageFrequency.HIGH
            else -> MessageFrequency.UNKNOWN
        }
    }
    
    /**
     * Get the encoded size of a message ID.
     * 
     * @param messageId The message ID
     * @return The number of bytes needed to encode this ID
     */
    fun getEncodedSize(messageId: Int): Int {
        return when (getMessageFrequency(messageId)) {
            MessageFrequency.HIGH -> 1
            MessageFrequency.MEDIUM -> 2
            MessageFrequency.LOW -> 4
            MessageFrequency.UNKNOWN -> 1 // Fallback
        }
    }
    
    /**
     * Well-known message IDs with Lumiya-compatible values.
     * 
     * These are calculated using the same formula as Lumiya:
     * - High frequency: signed byte value
     * - Medium frequency: byte | 65280
     * - Low frequency: short | -65536
     */
    object KnownMessages {
        // High frequency messages (single byte)
        const val START_PING_CHECK = 1
        const val COMPLETE_PING_CHECK = 2
        const val AGENT_UPDATE = 4
        const val AGENT_ANIMATION = 5
        const val OBJECT_UPDATE = 12
        const val OBJECT_UPDATE_COMPRESSED = 13
        const val OBJECT_UPDATE_CACHED = 14
        const val IMPROVED_TERSE_OBJECT_UPDATE = 15
        const val KILL_OBJECT = 16
        const val AVATAR_ANIMATION = 20
        
        // PacketAck is special - Lumiya treats 0xFB as signed byte = -5
        const val PACKET_ACK = -5
        
        // Medium frequency messages (0xFF + byte -> byte | 65280)
        const val COARSE_LOCATION_UPDATE = 65286 // 0xFF06 -> 6 | 65280
        
        // Low frequency messages (0xFFFF + short -> short | -65536)
        // Format: 0xFFFFxxxx.toInt() where xxxx is the message number
        // These values are negative when stored as Int due to the high bit being set
        // The actual protocol wire format is [0xFF][0xFF][xx][xx] (big-endian short)
        const val USE_CIRCUIT_CODE = (0xFFFF0003).toInt()          // Wire: FF FF 00 03
        const val COMPLETE_AGENT_MOVEMENT = (0xFFFF00F9).toInt()   // Wire: FF FF 00 F9
        const val LOGOUT_REQUEST = (0xFFFF00FC).toInt()            // Wire: FF FF 00 FC
        const val REGION_HANDSHAKE = (0xFFFF0094).toInt()          // Wire: FF FF 00 94
        const val REGION_HANDSHAKE_REPLY = (0xFFFF0095).toInt()    // Wire: FF FF 00 95
        const val AGENT_THROTTLE = (0xFFFF0099).toInt()            // Wire: FF FF 00 99
        const val AGENT_MOVEMENT_COMPLETE = (0xFFFF00FA).toInt()   // Wire: FF FF 00 FA
        const val CHAT_FROM_SIMULATOR = (0xFFFF00A3).toInt()       // Wire: FF FF 00 A3
        const val IMPROVED_INSTANT_MESSAGE = (0xFFFF00FE).toInt()  // Wire: FF FF 00 FE
        
        /**
         * Get a human-readable name for a message ID.
         */
        fun getName(messageId: Int): String {
            return when (messageId) {
                START_PING_CHECK -> "StartPingCheck"
                COMPLETE_PING_CHECK -> "CompletePingCheck"
                AGENT_UPDATE -> "AgentUpdate"
                AGENT_ANIMATION -> "AgentAnimation"
                OBJECT_UPDATE -> "ObjectUpdate"
                OBJECT_UPDATE_COMPRESSED -> "ObjectUpdateCompressed"
                OBJECT_UPDATE_CACHED -> "ObjectUpdateCached"
                IMPROVED_TERSE_OBJECT_UPDATE -> "ImprovedTerseObjectUpdate"
                KILL_OBJECT -> "KillObject"
                AVATAR_ANIMATION -> "AvatarAnimation"
                com.linkpoint.protocol.messages.MessageIds.SOUND_TRIGGER -> "SoundTrigger"
                PACKET_ACK -> "PacketAck"
                COARSE_LOCATION_UPDATE -> "CoarseLocationUpdate"
                USE_CIRCUIT_CODE -> "UseCircuitCode"
                COMPLETE_AGENT_MOVEMENT -> "CompleteAgentMovement"
                LOGOUT_REQUEST -> "LogoutRequest"
                REGION_HANDSHAKE -> "RegionHandshake"
                REGION_HANDSHAKE_REPLY -> "RegionHandshakeReply"
                AGENT_THROTTLE -> "AgentThrottle"
                AGENT_MOVEMENT_COMPLETE -> "AgentMovementComplete"
                CHAT_FROM_SIMULATOR -> "ChatFromSimulator"
                IMPROVED_INSTANT_MESSAGE -> "ImprovedInstantMessage"
                com.linkpoint.protocol.messages.MessageIds.SCRIPT_CONTROL_CHANGE -> "ScriptControlChange"
                else -> "Unknown(0x${messageId.toString(16).uppercase()})"
            }
        }
    }
    
    /**
     * Validate that a message ID is properly formed.
     * 
     * @param messageId The message ID to validate
     * @return true if the ID is valid
     */
    fun isValidMessageId(messageId: Int): Boolean {
        // -1 (0xFF) is reserved as escape and not a valid high frequency ID
        if (messageId == -1) return false
        
        // Check valid ranges
        val frequency = getMessageFrequency(messageId)
        return frequency != MessageFrequency.UNKNOWN
    }
    
    /**
     * Convert a message ID from standard format to Lumiya format.
     * 
     * Standard format uses unsigned values:
     * - High: 0x01-0xFE
     * - Medium: 0xFF00-0xFFFE
     * - Low: 0xFFFF0000-0xFFFFFFFF
     * 
     * Lumiya format uses signed values as decoded by DecodeMessageID().
     * 
     * @param standardId The standard format message ID
     * @return The Lumiya-compatible message ID
     */
    fun standardToLumiya(standardId: Long): Int {
        return when {
            // Low frequency: 0xFFFFxxxx -> negative value
            (standardId and 0xFFFF0000L) == 0xFFFF0000L -> {
                val shortPart = (standardId and 0xFFFFL).toInt()
                (shortPart.toShort().toInt()) or LOW_FREQUENCY_BASE
            }
            // Medium frequency: 0xFFxx -> 65280 + byte
            (standardId and 0xFF00L) == 0xFF00L -> {
                val bytePart = (standardId and 0xFFL).toInt()
                bytePart or MEDIUM_FREQUENCY_BASE
            }
            // High frequency: 0x01-0xFE
            else -> {
                (standardId and 0xFFL).toByte().toInt()
            }
        }
    }
    
    /**
     * Convert a message ID from Lumiya format to standard format.
     * 
     * @param lumiyaId The Lumiya-format message ID
     * @return The standard format message ID
     */
    fun lumiyaToStandard(lumiyaId: Int): Long {
        return when {
            // Low frequency: negative -> 0xFFFFxxxx
            lumiyaId < HIGH_FREQUENCY_THRESHOLD -> {
                val shortValue = lumiyaId and 0xFFFF
                0xFFFF0000L or shortValue.toLong()
            }
            // Medium frequency: 65280+ -> 0xFFxx
            lumiyaId >= MEDIUM_FREQUENCY_BASE -> {
                val byteValue = lumiyaId and 0xFF
                0xFF00L or byteValue.toLong()
            }
            // High frequency
            else -> {
                (lumiyaId and 0xFF).toLong()
            }
        }
    }
    
    /**
     * Log message ID diagnostic information.
     */
    fun logMessageIdDiagnostics(messageId: Int, operation: String) {
        val frequency = getMessageFrequency(messageId)
        val name = KnownMessages.getName(messageId)
        val encoded = encodeMessageId(messageId)
        val hexEncoded = encoded.joinToString(" ") { "%02X".format(it) }
        
        Log.d(TAG, "Message ID Diagnostics [$operation]:")
        Log.d(TAG, "  ID: $messageId (0x${messageId.toString(16).uppercase()})")
        Log.d(TAG, "  Name: $name")
        Log.d(TAG, "  Frequency: $frequency")
        Log.d(TAG, "  Encoded: $hexEncoded")
    }
}
