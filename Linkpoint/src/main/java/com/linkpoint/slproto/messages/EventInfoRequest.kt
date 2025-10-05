package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EventInfoRequest : SLMessage() {
    val AgentData_Field = AgentData()
    val EventData_Field = EventData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class EventData {
        var EventID: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 40

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleEventInfoRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-77).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, EventData_Field.EventID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        EventData_Field.EventID = unpackInt(buffer)
    }
}