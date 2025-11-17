package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLureRequest : SLMessage {
    Info Info_Field = Info()

    class Info {
        UUID AgentID
        UUID LureID
        UUID SessionID
        Int TeleportFlags
    }

    TeleportLureRequest() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 56
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLureRequest(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 71)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packUUID(byteBuffer, this.Info_Field.SessionID)
        packUUID(byteBuffer, this.Info_Field.LureID)
        packInt(byteBuffer, this.Info_Field.TeleportFlags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.LureID = unpackUUID(byteBuffer)
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer)
    }
}
