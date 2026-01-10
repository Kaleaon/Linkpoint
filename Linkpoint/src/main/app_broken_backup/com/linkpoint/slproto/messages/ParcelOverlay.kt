package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ParcelOverlay : SLMessage {
    ParcelData ParcelData_Field = ParcelData()

    class ParcelData {
        ByteArray Data
        Int SequenceID
    }

    ParcelOverlay() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.ParcelData_Field.Data.size + 6 + 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleParcelOverlay(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -60)
        packInt(byteBuffer, this.ParcelData_Field.SequenceID)
        packVariable(byteBuffer, this.ParcelData_Field.Data, 2)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer)
        this.ParcelData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
