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

    Int CalcPayloadSize() {
        return 6
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleNetTest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 70)
        packShort(byteBuffer, (Short) this.NetBlock_Field.Port)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.NetBlock_Field.Port = unpackShort(byteBuffer) & 65535
    }
}
