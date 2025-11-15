package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.net.Inet4Address
import java.nio.ByteBuffer
import java.util.UUID

class DataServerLogoutMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var viewerIp: Inet4Address? = null
    var disconnect: Boolean = false
    var sessionId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packIPAddress(buffer, viewerIp)
        packBoolean(buffer, disconnect)
        packUUID(buffer, sessionId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        viewerIp = unpackIPAddress(buffer)
        disconnect = unpackBoolean(buffer)
        sessionId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00FB

    override fun getMessageName(): String = "DataServerLogout"
}
