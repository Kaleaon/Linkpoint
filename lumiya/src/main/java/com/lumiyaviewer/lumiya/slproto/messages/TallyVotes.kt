package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class TallyVotes : SLMessage {
    TallyVotes() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTallyVotes(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 109)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
    }
}
