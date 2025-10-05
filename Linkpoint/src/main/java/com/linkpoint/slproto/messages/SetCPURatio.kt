package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SetCPURatio : SLMessage() {
    val Data_Field = Data()

    class Data {
        var Ratio: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 5

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSetCPURatio(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(71.toByte())
        packByte(buffer, Data_Field.Ratio.toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        Data_Field.Ratio = unpackByte(buffer).toInt() and UnsignedBytes.MAX_VALUE.toInt()
    }
}