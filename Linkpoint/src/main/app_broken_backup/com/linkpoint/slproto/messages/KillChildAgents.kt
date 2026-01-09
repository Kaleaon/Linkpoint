package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class KillChildAgents : SLMessage {
    IDBlock IDBlock_Field = IDBlock()

    class IDBlock {
        UUID AgentID
    }

    KillChildAgents() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 20
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleKillChildAgents(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -14)
        packUUID(byteBuffer, this.IDBlock_Field.AgentID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.IDBlock_Field.AgentID = unpackUUID(byteBuffer)
    }
}
