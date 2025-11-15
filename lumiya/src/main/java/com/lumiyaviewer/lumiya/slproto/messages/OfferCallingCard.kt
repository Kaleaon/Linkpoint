package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class OfferCallingCard : SLMessage {
    AgentBlock AgentBlock_Field = AgentBlock()
    AgentData AgentData_Field = AgentData()

    class AgentBlock {
        UUID DestID
        UUID TransactionID
    }

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    OfferCallingCard() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 68
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOfferCallingCard(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 45)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentBlock_Field.DestID)
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
