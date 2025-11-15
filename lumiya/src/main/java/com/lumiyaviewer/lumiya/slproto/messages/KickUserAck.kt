package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class KickUserAck : SLMessage {
    UserInfo UserInfo_Field = UserInfo()

    class UserInfo {
        Int Flags
        UUID SessionID
    }

    KickUserAck() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 24
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKickUserAck(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -92)
        packUUID(byteBuffer, this.UserInfo_Field.SessionID)
        packInt(byteBuffer, this.UserInfo_Field.Flags)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.UserInfo_Field.SessionID = unpackUUID(byteBuffer)
        this.UserInfo_Field.Flags = unpackInt(byteBuffer)
    }
}
