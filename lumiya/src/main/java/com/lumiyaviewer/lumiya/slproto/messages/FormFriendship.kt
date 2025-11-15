package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFormFriendship(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 43)
        packUUID(byteBuffer, this.AgentBlock_Field.SourceID)
        packUUID(byteBuffer, this.AgentBlock_Field.DestID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentBlock_Field.SourceID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer)
    }
}
