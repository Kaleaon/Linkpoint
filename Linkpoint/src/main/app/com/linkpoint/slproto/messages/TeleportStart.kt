package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TeleportStart : SLMessage {
    Info Info_Field = Info()

    class Info {
        Int TeleportFlags
    }

    TeleportStart() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 8
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTeleportStart(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 73)
        packInt(byteBuffer, this.Info_Field.TeleportFlags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer)
    }
}
