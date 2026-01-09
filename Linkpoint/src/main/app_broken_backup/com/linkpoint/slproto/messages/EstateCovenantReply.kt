package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EstateCovenantReply : SLMessage {
    Data Data_Field = Data()

    class Data {
        UUID CovenantID
        Int CovenantTimestamp
        ByteArray EstateName
        UUID EstateOwnerID
    }

    EstateCovenantReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.Data_Field.EstateName.size + 21 + 16 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleEstateCovenantReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -52)
        packUUID(byteBuffer, this.Data_Field.CovenantID)
        packInt(byteBuffer, this.Data_Field.CovenantTimestamp)
        packVariable(byteBuffer, this.Data_Field.EstateName, 1)
        packUUID(byteBuffer, this.Data_Field.EstateOwnerID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Data_Field.CovenantID = unpackUUID(byteBuffer)
        this.Data_Field.CovenantTimestamp = unpackInt(byteBuffer)
        this.Data_Field.EstateName = unpackVariable(byteBuffer, 1)
        this.Data_Field.EstateOwnerID = unpackUUID(byteBuffer)
    }
}
