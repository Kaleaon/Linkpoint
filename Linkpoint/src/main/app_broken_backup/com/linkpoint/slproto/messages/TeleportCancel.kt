package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportCancel : SLMessage {
    Info Info_Field = Info()

    class Info {
        UUID AgentID
        UUID SessionID
    }

    TeleportCancel() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 36
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleTeleportCancel(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 72)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packUUID(byteBuffer, this.Info_Field.SessionID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.SessionID = unpackUUID(byteBuffer)
    }
}
