package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class CompletePingCheck : SLMessage {
    PingID PingID_Field = PingID()

    class PingID {
        Int PingID
    }

    CompletePingCheck() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 2
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleCompletePingCheck(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 2)
        packByte(byteBuffer, (byte) this.PingID_Field.PingID)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
