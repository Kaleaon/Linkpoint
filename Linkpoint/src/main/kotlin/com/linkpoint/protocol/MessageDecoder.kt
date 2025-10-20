package com.linkpoint.protocol

import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

/**
 * MessageType - All Second Life message types
 * 
 * Based on Firestorm's message_template.msg
 * This is a subset of the most common messages
 */
enum class MessageType(val id: Int, val name: String, val frequency: MessageFrequency) {
    // High frequency messages (8-bit ID: 1-254)
    PACKET_ACK(0xFB, "PacketAck", MessageFrequency.HIGH),
    OPEN_CIRCUIT(0xFC, "OpenCircuit", MessageFrequency.HIGH),
    CLOSE_CIRCUIT(0xFD, "CloseCircuit", MessageFrequency.HIGH),
    
    // Agent messages
    AGENT_UPDATE(4, "AgentUpdate", MessageFrequency.HIGH),
    AGENT_ANIMATION(20, "AgentAnimation", MessageFrequency.HIGH),
    AGENT_REQUEST_SIT(23, "AgentRequestSit", MessageFrequency.HIGH),
    AGENT_SIT(24, "AgentSit", MessageFrequency.HIGH),
    
    // Chat messages
    CHAT_FROM_VIEWER(80, "ChatFromViewer", MessageFrequency.LOW),
    CHAT_FROM_SIMULATOR(139, "ChatFromSimulator", MessageFrequency.LOW),
    
    // IM messages
    IMPROVED_INSTANT_MESSAGE(254, "ImprovedInstantMessage", MessageFrequency.HIGH),
    
    // Object messages
    OBJECT_UPDATE(12, "ObjectUpdate", MessageFrequency.HIGH),
    OBJECT_UPDATE_COMPRESSED(13, "ObjectUpdateCompressed", MessageFrequency.HIGH),
    OBJECT_UPDATE_CACHED(14, "ObjectUpdateCached", MessageFrequency.HIGH),
    KILL_OBJECT(78, "KillObject", MessageFrequency.LOW),
    
    // Ping messages
    START_PING_CHECK(1, "StartPingCheck", MessageFrequency.HIGH),
    COMPLETE_PING_CHECK(2, "CompletePingCheck", MessageFrequency.HIGH),
    
    // Session messages
    USE_CIRCUIT_CODE(3, "UseCircuitCode", MessageFrequency.HIGH),
    
    // Teleport messages
    TELEPORT_REQUEST(0x18, "TeleportRequest", MessageFrequency.LOW),
    TELEPORT_START(0x4C, "TeleportStart", MessageFrequency.LOW),
    TELEPORT_PROGRESS(0x56, "TeleportProgress", MessageFrequency.LOW),
    TELEPORT_FINISH(0x41, "TeleportFinish", MessageFrequency.LOW),
    
    // Asset messages
    TRANSFER_REQUEST(0x66, "TransferRequest", MessageFrequency.LOW),
    TRANSFER_INFO(0x67, "TransferInfo", MessageFrequency.LOW),
    TRANSFER_PACKET(0x68, "TransferPacket", MessageFrequency.LOW),
    
    // Inventory messages
    FETCH_INVENTORY_DESCENDENTS(0x72, "FetchInventoryDescendents", MessageFrequency.LOW),
    INVENTORY_DESCENDENTS(0x73, "InventoryDescendents", MessageFrequency.LOW);
    
    companion object {
        private val BY_ID = values().associateBy { it.id }
        
        fun fromId(id: Int): MessageType? = BY_ID[id]
        fun fromName(name: String): MessageType? = values().find { it.name == name }
    }
}

/**
 * MessageFrequency - Determines message ID size
 */
enum class MessageFrequency {
    HIGH,    // 8-bit ID (1-254)
    MEDIUM,  // 16-bit ID
    LOW      // 32-bit ID
}

/**
 * MessageDecoder - Decode Second Life protocol messages
 * 
 * Based on Firestorm's LLTemplateMessageReader
 */
object MessageDecoder {
    
    /**
     * Decode message from buffer
     * 
     * Packet format:
     * - Byte 0: Flags
     * - Bytes 1-4: Sequence number
     * - Byte 5: Extra header data length
     * - Bytes 6+: Message data
     */
    fun decode(buffer: ByteBuffer): SLMessage? {
        try {
            // Message ID is after header (flags, sequence, offset)
            // We're positioned after those already
            
            if (!buffer.hasRemaining()) {
                return null
            }
            
            // Read message ID (variable size based on frequency)
            val firstByte = buffer.get()
            
            val messageType = when {
                // High frequency: 8-bit ID
                firstByte.toInt() != 0xFF && firstByte.toInt() != 0xFE -> {
                    MessageType.fromId(firstByte.toInt() and 0xFF)
                }
                // Medium frequency: 16-bit ID (0xFF + 2 bytes)
                firstByte.toInt() == 0xFF -> {
                    val id = buffer.getShort().toInt() and 0xFFFF
                    MessageType.fromId(id)
                }
                // Low frequency: 32-bit ID (0xFE + 4 bytes)
                else -> {
                    val id = buffer.getInt()
                    MessageType.fromId(id)
                }
            }
            
            if (messageType == null) {
                Debug.Log("Unknown message type")
                return null
            }
            
            // Create and decode message based on type
            return when (messageType) {
                MessageType.PACKET_ACK -> decodePacketAck(buffer)
                MessageType.START_PING_CHECK -> decodeStartPingCheck(buffer)
                MessageType.COMPLETE_PING_CHECK -> decodeCompletePingCheck(buffer)
                MessageType.CHAT_FROM_SIMULATOR -> decodeChatFromSimulator(buffer)
                MessageType.IMPROVED_INSTANT_MESSAGE -> decodeImprovedInstantMessage(buffer)
                MessageType.OBJECT_UPDATE -> decodeObjectUpdate(buffer)
                MessageType.KILL_OBJECT -> decodeKillObject(buffer)
                else -> {
                    Debug.Log("Message decoder not implemented for: ${messageType.name}")
                    null
                }
            }
            
        } catch (e: Exception) {
            Debug.Log("Message decode error: ${e.message}")
            return null
        }
    }
    
    /**
     * Decode PacketAck
     */
    private fun decodePacketAck(buffer: ByteBuffer): SLMessage {
        val packetCount = buffer.get().toInt() and 0xFF
        val packets = mutableListOf<com.linkpoint.slproto.messages.PacketAck.Packet>()
        
        for (i in 0 until packetCount) {
            val id = buffer.getInt()
            packets.add(com.linkpoint.slproto.messages.PacketAck.Packet(id))
        }
        
        return com.linkpoint.slproto.messages.PacketAck(packets)
    }
    
    /**
     * Decode StartPingCheck
     */
    private fun decodeStartPingCheck(buffer: ByteBuffer): SLMessage {
        val pingID = buffer.get()
        return com.linkpoint.slproto.messages.StartPingCheck(pingID)
    }
    
    /**
     * Decode CompletePingCheck
     */
    private fun decodeCompletePingCheck(buffer: ByteBuffer): SLMessage {
        val pingID = buffer.get()
        return com.linkpoint.slproto.messages.CompletePingCheck(pingID)
    }
    
    /**
     * Decode ChatFromSimulator
     */
    private fun decodeChatFromSimulator(buffer: ByteBuffer): SLMessage {
        val fromName = SLMessage.readVariableString(buffer)
        val sourceID = SLMessage.readUUID(buffer)
        val ownerID = SLMessage.readUUID(buffer)
        val sourceType = buffer.get()
        val chatType = buffer.get()
        val audible = buffer.get()
        val channel = buffer.getInt()
        val message = SLMessage.readVariableString(buffer)
        
        // Position
        val x = buffer.getFloat()
        val y = buffer.getFloat()
        val z = buffer.getFloat()
        
        return com.linkpoint.chat.ChatFromSimulator(
            fromName, sourceID, ownerID,
            sourceType, chatType, channel, message,
            com.linkpoint.slproto.types.LLVector3(x, y, z)
        )
    }
    
    /**
     * Decode ImprovedInstantMessage
     */
    private fun decodeImprovedInstantMessage(buffer: ByteBuffer): SLMessage {
        val fromAgentID = SLMessage.readUUID(buffer)
        val toAgentID = SLMessage.readUUID(buffer)
        val parentEstateID = buffer.getInt()
        val regionID = SLMessage.readUUID(buffer)
        
        // Position
        val x = buffer.getFloat()
        val y = buffer.getFloat()
        val z = buffer.getFloat()
        
        val dialog = buffer.get()
        val offline = buffer.get()
        val imSessionID = SLMessage.readUUID(buffer)
        val timestamp = buffer.getInt()
        
        val fromAgentName = SLMessage.readVariableString(buffer)
        val message = SLMessage.readVariableString(buffer)
        
        val binaryBucket = SLMessage.readVariableField(buffer)
        
        return com.linkpoint.chat.ImprovedInstantMessageReceived(
            fromAgentID, fromAgentName, toAgentID,
            message, dialog.toInt(), imSessionID,
            offline != 0.toByte()
        )
    }
    
    /**
     * Decode ObjectUpdate
     */
    private fun decodeObjectUpdate(buffer: ByteBuffer): SLMessage? {
        // Object updates are complex, return null for now
        Debug.Log("ObjectUpdate decoding not yet implemented")
        return null
    }
    
    /**
     * Decode KillObject
     */
    private fun decodeKillObject(buffer: ByteBuffer): SLMessage {
        val count = buffer.get().toInt() and 0xFF
        val localIDs = mutableListOf<Int>()
        
        for (i in 0 until count) {
            localIDs.add(buffer.getInt())
        }
        
        return com.linkpoint.objects.KillObject(localIDs)
    }
}
