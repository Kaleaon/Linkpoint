package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EstateCovenantReply : SLMessage() {
    public Data Data_Field = Data()

    @JvmStatic
    class Data {
        public UUID CovenantID
        public Int CovenantTimestamp
        public ByteArray EstateName
        public UUID EstateOwnerID
    }

    public EstateCovenantReply() {
        this.zeroCoded = false
    }

    public fun CalcPayloadSize(): Int {
        return this.Data_Field.EstateName.length + 21 + 16 + 4
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleEstateCovenantReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -52)
        packUUID(byteBuffer, this.Data_Field.CovenantID)
        packInt(byteBuffer, this.Data_Field.CovenantTimestamp)
        packVariable(byteBuffer, this.Data_Field.EstateName, 1)
        packUUID(byteBuffer, this.Data_Field.EstateOwnerID)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.Data_Field.CovenantID = unpackUUID(byteBuffer)
        this.Data_Field.CovenantTimestamp = unpackInt(byteBuffer)
        this.Data_Field.EstateName = unpackVariable(byteBuffer, 1)
        this.Data_Field.EstateOwnerID = unpackUUID(byteBuffer)
    }
}
