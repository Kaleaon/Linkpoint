package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLureRequest : SLMessage() {
    public Info Info_Field = Info()

    @JvmStatic
    class Info {
        public UUID AgentID
        public UUID LureID
        public UUID SessionID
        public Int TeleportFlags
    }

    public TeleportLureRequest() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return 56
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTeleportLureRequest(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 71)
        packUUID(byteBuffer, this.Info_Field.AgentID)
        packUUID(byteBuffer, this.Info_Field.SessionID)
        packUUID(byteBuffer, this.Info_Field.LureID)
        packInt(byteBuffer, this.Info_Field.TeleportFlags)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = unpackUUID(byteBuffer)
        this.Info_Field.SessionID = unpackUUID(byteBuffer)
        this.Info_Field.LureID = unpackUUID(byteBuffer)
        this.Info_Field.TeleportFlags = unpackInt(byteBuffer)
    }
}
