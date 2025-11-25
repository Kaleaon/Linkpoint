package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class KickUserMessage : SLMessage() {
    var targetIp: Inet4Address? = null
    var targetPort: Int = 0
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var reason: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packIPAddress(buffer, targetIp)
        packUInt16(buffer, targetPort)
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packVariable(buffer, reason, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        targetIp = unpackIPAddress(buffer)
        targetPort = unpackUInt16(buffer)
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        reason = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF00A3.toInt()

    override fun getMessageName(): String = "KickUser"
}
