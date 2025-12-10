package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class NetTest : SLMessage {
    NetBlock NetBlock_Field = NetBlock()

    class NetBlock {
        Int Port
    }

    NetTest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 6
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleNetTest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 70)
        packShort(byteBuffer, (this as Short).NetBlock_Field.Port)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.NetBlock_Field.Port = unpackShort(byteBuffer) & 65535
    }
}
