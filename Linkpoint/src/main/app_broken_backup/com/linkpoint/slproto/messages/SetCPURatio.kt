package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SetCPURatio : SLMessage {
    Data Data_Field = Data()

    class Data {
        Int Ratio
    }

    SetCPURatio() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 5
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSetCPURatio(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 71)
        packByte(byteBuffer, (this as Byte).Data_Field.Ratio)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.Data_Field.Ratio = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
