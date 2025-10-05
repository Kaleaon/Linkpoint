package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class MapLayerRequest : SLMessage() {
    val AgentData_Field = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
        var Flags: Int = 0
        var EstateID: Int = 0
        var Godlike: Boolean = false
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 45

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleMapLayerRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put((-107).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, AgentData_Field.Flags)
        packInt(buffer, AgentData_Field.EstateID)
        packBoolean(buffer, AgentData_Field.Godlike)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        AgentData_Field.Flags = unpackInt(buffer)
        AgentData_Field.EstateID = unpackInt(buffer)
        AgentData_Field.Godlike = unpackBoolean(buffer)
    }
}