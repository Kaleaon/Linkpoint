package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FormFriendship : SLMessage {
    AgentBlock AgentBlock_Field = AgentBlock()

    class AgentBlock {
        UUID DestID
        UUID SourceID
    }

    FormFriendship() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleFormFriendship(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 43)
        packUUID(byteBuffer, this.AgentBlock_Field.SourceID)
        packUUID(byteBuffer, this.AgentBlock_Field.DestID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentBlock_Field.SourceID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer)
    }
}
