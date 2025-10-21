package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class OfferCallingCard : SLMessage() {
    public AgentBlock AgentBlock_Field = AgentBlock()
    public AgentData AgentData_Field = AgentData()

    @JvmStatic
    class AgentBlock {
        public UUID DestID
        public UUID TransactionID
    }

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    public OfferCallingCard() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 68
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleOfferCallingCard(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 45)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentBlock_Field.DestID)
        packUUID(byteBuffer, this.AgentBlock_Field.TransactionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.DestID = unpackUUID(byteBuffer)
        this.AgentBlock_Field.TransactionID = unpackUUID(byteBuffer)
    }
}
