package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SubscribeLoad : SLMessage {
    SubscribeLoad() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSubscribeLoad(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 7)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
    }
}
