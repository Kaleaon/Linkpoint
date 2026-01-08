package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class UnsubscribeLoad : SLMessage {
    UnsubscribeLoad() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUnsubscribeLoad(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 8)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
    }
}
