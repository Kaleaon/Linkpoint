package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelPropertiesRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var sequenceId: Int = 0
    var west: Float = 0f
    var south: Float = 0f
    var east: Float = 0f
    var north: Float = 0f
    var snapSelection: Boolean = false


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, sequenceId)
        packFloat(buffer, west)
        packFloat(buffer, south)
        packFloat(buffer, east)
        packFloat(buffer, north)
        packBoolean(buffer, snapSelection)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        sequenceId = unpackInt(buffer)
        west = unpackFloat(buffer)
        south = unpackFloat(buffer)
        east = unpackFloat(buffer)
        north = unpackFloat(buffer)
        snapSelection = unpackBoolean(buffer)
    }

    override fun getMessageID(): Int = 0x0000FF0B

    override fun getMessageName(): String = "ParcelPropertiesRequest"
}
