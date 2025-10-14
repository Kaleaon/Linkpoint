package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * Teleport request message
 */
class TeleportRequestMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()
    var regionHandle: Long = 0L
    var position: LLVector3 = LLVector3()
    var lookAt: LLVector3 = LLVector3()

    override fun packPayload(buffer: ByteBuffer) {
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        buffer.putLong(regionHandle)
        position.pack(buffer)
        lookAt.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = UUID(buffer.getLong(), buffer.getLong())
        sessionId = UUID(buffer.getLong(), buffer.getLong())
        regionHandle = buffer.getLong()
        position = LLVector3.unpack(buffer)
        lookAt = LLVector3.unpack(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TELEPORT_REQUEST

    override fun getMessageName(): String = "TeleportRequest"
}

/**
 * Teleport start message
 */
class TeleportStartMessage : SLMessage() {
    var flags: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        buffer.putInt(flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        flags = buffer.getInt()
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TELEPORT_START

    override fun getMessageName(): String = "TeleportStart"
}

/**
 * Teleport finish message
 */
class TeleportFinishMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var locationId: Int = 0
    var simIp: String = ""
    var simPort: Int = 0
    var regionHandle: Long = 0L
    var seedCapability: String = ""
    var simAccess: Byte = 0
    var teleportFlags: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        buffer.putInt(locationId)

        val ipBytes = simIp.toByteArray(StandardCharsets.UTF_8)
        buffer.put(ipBytes.size.toByte())
        buffer.put(ipBytes)

        buffer.putInt(simPort)
        buffer.putLong(regionHandle)

        val seedBytes = seedCapability.toByteArray(StandardCharsets.UTF_8)
        buffer.putShort(seedBytes.size.toShort())
        buffer.put(seedBytes)

        buffer.put(simAccess)
        buffer.putInt(teleportFlags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = UUID(buffer.getLong(), buffer.getLong())
        locationId = buffer.getInt()

        val ipLength = buffer.get().toInt() and 0xFF
        val ipBytes = ByteArray(ipLength)
        buffer.get(ipBytes)
        simIp = String(ipBytes, StandardCharsets.UTF_8)

        simPort = buffer.getInt()
        regionHandle = buffer.getLong()

        val seedLength = buffer.getShort().toInt() and 0xFFFF
        val seedBytes = ByteArray(seedLength)
        buffer.get(seedBytes)
        seedCapability = String(seedBytes, StandardCharsets.UTF_8)

        simAccess = buffer.get()
        teleportFlags = buffer.getInt()
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TELEPORT_FINISH

    override fun getMessageName(): String = "TeleportFinish"
}
