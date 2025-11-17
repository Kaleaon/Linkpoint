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

    Int CalcPayloadSize() {
        return 20
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKillChildAgents(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -14)
        packUUID(byteBuffer, this.IDBlock_Field.AgentID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.IDBlock_Field.AgentID = unpackUUID(byteBuffer)
    }
}
