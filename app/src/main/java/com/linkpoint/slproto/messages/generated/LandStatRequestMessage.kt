package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LandStatRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var reportType: Int = 0
    var requestFlags: Int = 0
    var filter: ByteArray = ByteArray(0)
    var parcelLocalId: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, reportType)
        packInt(buffer, requestFlags)
        packVariable(buffer, filter, 1)
        packInt(buffer, parcelLocalId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        reportType = unpackInt(buffer)
        requestFlags = unpackInt(buffer)
        filter = unpackVariable(buffer, 1)
        parcelLocalId = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF01A5

    override fun getMessageName(): String = "LandStatRequest"
}
