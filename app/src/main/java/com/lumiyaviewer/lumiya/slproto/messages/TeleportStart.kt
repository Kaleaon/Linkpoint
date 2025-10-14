package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class TeleportStart : SLMessage {
    Info Info_Field = Info()

    class Info {
        Int TeleportFlags
    }

    TeleportStart() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 8
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportStart(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 73)
        packInt(byteBuffer, this.Info_Field.TeleportFlags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer)
    }
}
