package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SubscribeLoad : SLMessage {
    SubscribeLoad() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleSubscribeLoad(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 7)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
    }
}
