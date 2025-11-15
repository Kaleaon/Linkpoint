package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class SetCPURatio : SLMessage {
    Data Data_Field = Data()

    class Data {
        Int Ratio
    }

    SetCPURatio() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 5
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetCPURatio(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 71)
        packByte(byteBuffer, (Byte) this.Data_Field.Ratio)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.Ratio = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
