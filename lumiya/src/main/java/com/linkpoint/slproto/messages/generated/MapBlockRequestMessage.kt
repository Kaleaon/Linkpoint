package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapBlockRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var flags: Int = 0
    var estateId: Int = 0
    var godlike: Boolean = false
    var minX: Int = 0
    var maxX: Int = 0
    var minY: Int = 0
    var maxY: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
        packInt(buffer, estateId)
        packBoolean(buffer, godlike)
        packUInt16(buffer, minX)
        packUInt16(buffer, maxX)
        packUInt16(buffer, minY)
        packUInt16(buffer, maxY)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
        estateId = unpackInt(buffer)
        godlike = unpackBoolean(buffer)
        minX = unpackUInt16(buffer)
        maxX = unpackUInt16(buffer)
        minY = unpackUInt16(buffer)
        maxY = unpackUInt16(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0197

    override fun getMessageName(): String = "MapBlockRequest"
}
