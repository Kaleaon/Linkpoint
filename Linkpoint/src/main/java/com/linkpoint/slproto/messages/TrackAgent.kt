package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TrackAgent : SLMessage() {
    val AgentData_Field = AgentData()
    val TargetData_Field = TargetData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class TargetData {
        var PreyID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 52

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleTrackAgent(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-126).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, TargetData_Field.PreyID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        TargetData_Field.PreyID = unpackUUID(buffer)
    }
}