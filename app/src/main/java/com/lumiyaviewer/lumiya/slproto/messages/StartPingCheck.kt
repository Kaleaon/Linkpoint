package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class StartPingCheck : SLMessage {
    PingID PingID_Field = PingID()

    class PingID {
        Int OldestUnacked
        Int PingID
    }

    StartPingCheck() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 6
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartPingCheck(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) 1)
        packByte(byteBuffer, (Byte) this.PingID_Field.PingID)
        packInt(byteBuffer, this.PingID_Field.OldestUnacked)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.PingID_Field.PingID = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.PingID_Field.OldestUnacked = unpackInt(byteBuffer)
    }
}
