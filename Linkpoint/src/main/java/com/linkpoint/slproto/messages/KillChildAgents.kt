package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class KillChildAgents : SLMessage() {
    val IDBlock_Field = IDBlock()

    class IDBlock {
        var AgentID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 20

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleKillChildAgents(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-14).toByte())
        packUUID(buffer, IDBlock_Field.AgentID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        IDBlock_Field.AgentID = unpackUUID(buffer)
    }
}