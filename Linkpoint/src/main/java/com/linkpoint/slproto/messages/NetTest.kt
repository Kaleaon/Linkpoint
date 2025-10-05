package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NetTest : SLMessage() {
    val NetBlock_Field = NetBlock()

    class NetBlock {
        var Port: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 6

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleNetTest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(70.toByte())
        packShort(buffer, NetBlock_Field.Port.toShort())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        NetBlock_Field.Port = unpackShort(buffer).toInt() and 65535
    }
}