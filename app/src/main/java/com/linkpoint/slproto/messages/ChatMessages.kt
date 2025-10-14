package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Chat from simulator message
 */
class ChatFromSimulatorMessage : SLMessage() {
    var fromName: String = ""
    var sourceId: UUID = UUID.randomUUID()
    var ownerId: UUID = UUID.randomUUID()
    var sourceType: Byte = 0
    var chatType: Byte = 0
    var audible: Byte = 0
    var position: LLVector3 = LLVector3()
    var message: String = ""

    override fun packPayload(buffer: ByteBuffer) {
        // From name (variable length)
        val nameBytes = fromName.toByteArray(StandardCharsets.UTF_8)
        buffer.put(nameBytes.size.toByte())
        buffer.put(nameBytes)

        // Source ID
        buffer.putLong(sourceId.mostSignificantBits)
        buffer.putLong(sourceId.leastSignificantBits)

        // Owner ID
        buffer.putLong(ownerId.mostSignificantBits)
        buffer.putLong(ownerId.leastSignificantBits)

        // Chat data
        buffer.put(sourceType)
        buffer.put(chatType)
        buffer.put(audible)

        // Position
        position.pack(buffer)

        // Message (variable length)
        val messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        buffer.putShort(messageBytes.size.toShort())
        buffer.put(messageBytes)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // From name
        val nameLength = buffer.get().toInt() and 0xFF
        val nameBytes = ByteArray(nameLength)
        buffer.get(nameBytes)
        fromName = String(nameBytes, StandardCharsets.UTF_8)

        // Source ID
        val sourceMsb = buffer.getLong()
        val sourceLsb = buffer.getLong()
        sourceId = UUID(sourceMsb, sourceLsb)

        // Owner ID
        val ownerMsb = buffer.getLong()
        val ownerLsb = buffer.getLong()
        ownerId = UUID(ownerMsb, ownerLsb)

        // Chat data
        sourceType = buffer.get()
        chatType = buffer.get()
        audible = buffer.get()

        // Position
        position = LLVector3.unpack(buffer)

        // Message
        val messageLength = buffer.getShort().toInt() and 0xFFFF
        val messageBytes = ByteArray(messageLength)
        buffer.get(messageBytes)
        message = String(messageBytes, StandardCharsets.UTF_8)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CHAT_FROM_SIMULATOR

    override fun getMessageName(): String = "ChatFromSimulator"
}

/**
 * Chat from viewer message
 */
class ChatFromViewerMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var message: String = ""
    var type: Byte = 0
    var channel: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        // Agent data
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)

        // Message (variable length)
        val messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        buffer.putShort(messageBytes.size.toShort())
        buffer.put(messageBytes)

        // Chat data
        buffer.put(type)
        buffer.putInt(channel)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // Agent data
        val agentMsb = buffer.getLong()
        val agentLsb = buffer.getLong()
        agentId = UUID(agentMsb, agentLsb)

        val sessionMsb = buffer.getLong()
        val sessionLsb = buffer.getLong()
        sessionId = UUID(sessionMsb, sessionLsb)

        // Message
        val messageLength = buffer.getShort().toInt() and 0xFFFF
        val messageBytes = ByteArray(messageLength)
        buffer.get(messageBytes)
        message = String(messageBytes, StandardCharsets.UTF_8)

        // Chat data
        type = buffer.get()
        channel = buffer.getInt()
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.CHAT_FROM_VIEWER

    override fun getMessageName(): String = "ChatFromViewer"
}

/**
 * Improved IM message
 */
class ImprovedIMMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var fromGroup: Boolean = false
    var toAgentId: UUID = UUID.randomUUID()
    var parentEstateId: Int = 0
    var regionId: UUID = UUID.randomUUID()
    var position: LLVector3 = LLVector3()
    var offline: Byte = 0
    var dialog: Byte = 0
    var id: UUID = UUID.randomUUID()
    var timestamp: Int = 0
    var fromAgentName: String = ""
    var message: String = ""
    var binaryBucket: ByteArray = ByteArray(0)

    override fun packPayload(buffer: ByteBuffer) {
        // Agent data
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        buffer.put(if (fromGroup) 1 else 0)

        // Message block
        buffer.putLong(toAgentId.mostSignificantBits)
        buffer.putLong(toAgentId.leastSignificantBits)
        buffer.putInt(parentEstateId)
        buffer.putLong(regionId.mostSignificantBits)
        buffer.putLong(regionId.leastSignificantBits)
        position.pack(buffer)
        buffer.put(offline)
        buffer.put(dialog)
        buffer.putLong(id.mostSignificantBits)
        buffer.putLong(id.leastSignificantBits)
        buffer.putInt(timestamp)

        // From agent name
        val nameBytes = fromAgentName.toByteArray(StandardCharsets.UTF_8)
        buffer.put(nameBytes.size.toByte())
        buffer.put(nameBytes)

        // Message
        val messageBytes = message.toByteArray(StandardCharsets.UTF_8)
        buffer.putShort(messageBytes.size.toShort())
        buffer.put(messageBytes)

        // Binary bucket
        buffer.putShort(binaryBucket.size.toShort())
        buffer.put(binaryBucket)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        // Agent data
        val agentMsb = buffer.getLong()
        val agentLsb = buffer.getLong()
        agentId = UUID(agentMsb, agentLsb)

        val sessionMsb = buffer.getLong()
        val sessionLsb = buffer.getLong()
        sessionId = UUID(sessionMsb, sessionLsb)

        fromGroup = buffer.get() != 0.toByte()

        // Message block
        val toAgentMsb = buffer.getLong()
        val toAgentLsb = buffer.getLong()
        toAgentId = UUID(toAgentMsb, toAgentLsb)

        parentEstateId = buffer.getInt()

        val regionMsb = buffer.getLong()
        val regionLsb = buffer.getLong()
        regionId = UUID(regionMsb, regionLsb)

        position = LLVector3.unpack(buffer)
        offline = buffer.get()
        dialog = buffer.get()

        val idMsb = buffer.getLong()
        val idLsb = buffer.getLong()
        id = UUID(idMsb, idLsb)

        timestamp = buffer.getInt()

        // From agent name
        val nameLength = buffer.get().toInt() and 0xFF
        val nameBytes = ByteArray(nameLength)
        buffer.get(nameBytes)
        fromAgentName = String(nameBytes, StandardCharsets.UTF_8)

        // Message
        val messageLength = buffer.getShort().toInt() and 0xFFFF
        val messageBytes = ByteArray(messageLength)
        buffer.get(messageBytes)
        message = String(messageBytes, StandardCharsets.UTF_8)

        // Binary bucket
        val bucketLength = buffer.getShort().toInt() and 0xFFFF
        binaryBucket = ByteArray(bucketLength)
        buffer.get(binaryBucket)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.IMPROVED_IM

    override fun getMessageName(): String = "ImprovedInstantMessage"
}
