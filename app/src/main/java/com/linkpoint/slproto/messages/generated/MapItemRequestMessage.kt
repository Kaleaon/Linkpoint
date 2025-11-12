package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapItemRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var estateId: Int = 0
    var godlike: Boolean = false
    var itemType: Int = 0
    var regionHandle: Long = 0L


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
        packInt(buffer, itemType)
        packLong(buffer, regionHandle)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
        itemType = unpackInt(buffer)
        regionHandle = unpackLong(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF019A

    override fun getMessageName(): String = "MapItemRequest"
}
