package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelJoinMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var west: Float = 0f
    var south: Float = 0f
    var east: Float = 0f
    var north: Float = 0f


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packFloat(buffer, west)
        packFloat(buffer, south)
        packFloat(buffer, east)
        packFloat(buffer, north)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        west = unpackFloat(buffer)
        south = unpackFloat(buffer)
        east = unpackFloat(buffer)
        north = unpackFloat(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00D2.toInt()

    override fun getMessageName(): String = "ParcelJoin"
}
