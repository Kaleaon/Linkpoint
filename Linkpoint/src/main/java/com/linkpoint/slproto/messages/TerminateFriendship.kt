package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TerminateFriendship : SLMessage() {
    val AgentData_Field = AgentData()
    val ExBlock_Field = ExBlock()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class ExBlock {
        var OtherID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 52

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleTerminateFriendship(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(44.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, ExBlock_Field.OtherID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        ExBlock_Field.OtherID = unpackUUID(buffer)
    }
}