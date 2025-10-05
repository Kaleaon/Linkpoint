package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FormFriendship : SLMessage() {
    val AgentBlock_Field = AgentBlock()

    class AgentBlock {
        var SourceID: UUID? = null
        var DestID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 36

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleFormFriendship(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(43.toByte())
        packUUID(buffer, AgentBlock_Field.SourceID)
        packUUID(buffer, AgentBlock_Field.DestID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentBlock_Field.SourceID = unpackUUID(buffer)
        AgentBlock_Field.DestID = unpackUUID(buffer)
    }
}