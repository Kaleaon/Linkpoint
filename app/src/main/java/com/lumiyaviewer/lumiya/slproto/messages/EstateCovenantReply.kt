package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EstateCovenantReply : SLMessage {
    Data Data_Field = Data()

    class Data {
        UUID CovenantID
        Int CovenantTimestamp
        byte[] EstateName
        UUID EstateOwnerID
    }

    EstateCovenantReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.Data_Field.EstateName.length + 21 + 16 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEstateCovenantReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -52)
        packUUID(byteBuffer, this.Data_Field.CovenantID)
        packInt(byteBuffer, this.Data_Field.CovenantTimestamp)
        packVariable(byteBuffer, this.Data_Field.EstateName, 1)
        packUUID(byteBuffer, this.Data_Field.EstateOwnerID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.CovenantID = unpackUUID(byteBuffer)
        this.Data_Field.CovenantTimestamp = unpackInt(byteBuffer)
        this.Data_Field.EstateName = unpackVariable(byteBuffer, 1)
        this.Data_Field.EstateOwnerID = unpackUUID(byteBuffer)
    }
}
