package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TallyVotes : SLMessage {
    TallyVotes() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 4
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleTallyVotes(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 109)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
    }
}
