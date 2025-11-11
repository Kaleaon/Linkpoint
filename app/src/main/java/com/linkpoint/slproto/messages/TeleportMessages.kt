package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.net.Inet4Address
import java.net.InetAddress
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
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packLong(buffer, regionHandle)
        position.pack(buffer)
        lookAt.pack(buffer)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        regionHandle = unpackLong(buffer)
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
        packInt(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        flags = unpackInt(buffer)
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
    var simIp: String = "0.0.0.0"
    var simPort: Int = 0
    var regionHandle: Long = 0L
    var seedCapability: String = ""
    var simAccess: Int = 0
    var teleportFlags: Int = 0

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, locationId)
        packIPAddress(buffer, resolveIPv4(simIp))
        packUInt16(buffer, simPort)
        packLong(buffer, regionHandle)
        packVariable(buffer, seedCapability.toByteArray(StandardCharsets.UTF_8), 2)
        packByte(buffer, simAccess)
        packInt(buffer, teleportFlags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        locationId = unpackInt(buffer)
        simIp = unpackIPAddress(buffer)?.hostAddress ?: "0.0.0.0"
        simPort = unpackUInt16(buffer)
        regionHandle = unpackLong(buffer)
        seedCapability = String(unpackVariable(buffer, 2), StandardCharsets.UTF_8)
        simAccess = unpackByte(buffer)
        teleportFlags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TELEPORT_FINISH

    override fun getMessageName(): String = "TeleportFinish"
}

/**
 * Teleport progress message
 */
class TeleportProgressMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var teleportFlags: Int = 0
    var statusMessage: String = ""

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packInt(buffer, teleportFlags)
        packVariable(buffer, statusMessage.toByteArray(StandardCharsets.UTF_8), 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        teleportFlags = unpackInt(buffer)
        statusMessage = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TELEPORT_PROGRESS

    override fun getMessageName(): String = "TeleportProgress"
}

/**
 * Teleport failed message
 */
class TeleportFailedMessage : SLMessage() {
    var agentId: UUID = UUID.randomUUID()
    var reason: String = ""

    data class AlertInfo(
        var message: String = "",
        var extraParams: ByteArray = ByteArray(0),
    )

    val alerts: MutableList<AlertInfo> = mutableListOf()

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packVariable(buffer, reason.toByteArray(StandardCharsets.UTF_8), 1)
        require(alerts.size <= 0xFF) { "Too many teleport alerts (${alerts.size})" }
        packByte(buffer, alerts.size)
        alerts.forEach { alert ->
            packVariable(buffer, alert.message.toByteArray(StandardCharsets.UTF_8), 1)
            packVariable(buffer, alert.extraParams, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        reason = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
        val count = unpackByte(buffer)
        alerts.clear()
        repeat(count) {
            val message = String(unpackVariable(buffer, 1), StandardCharsets.UTF_8)
            val extra = unpackVariable(buffer, 1)
            alerts += AlertInfo(message, extra)
        }
    }

    override fun getMessageID(): Int = SLMessageFactory.MessageIDs.TELEPORT_FAILED

    override fun getMessageName(): String = "TeleportFailed"
}

private fun resolveIPv4(address: String): Inet4Address? =
    try {
        InetAddress.getByName(address) as? Inet4Address
    } catch (_: Exception) {
        null
    }
