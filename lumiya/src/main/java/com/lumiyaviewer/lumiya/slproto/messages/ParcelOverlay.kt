package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return this.ParcelData_Field.Data.length + 6 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelOverlay(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -60)
        packInt(byteBuffer, this.ParcelData_Field.SequenceID)
        packVariable(byteBuffer, this.ParcelData_Field.Data, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer)
        this.ParcelData_Field.Data = unpackVariable(byteBuffer, 2)
    }
}
