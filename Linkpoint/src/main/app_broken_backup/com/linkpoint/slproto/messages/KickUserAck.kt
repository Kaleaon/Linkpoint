package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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

    fun CalcPayloadSize(): Int {
        return 24
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleKickUserAck(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -92)
        packUUID(byteBuffer, this.UserInfo_Field.SessionID)
        packInt(byteBuffer, this.UserInfo_Field.Flags)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.UserInfo_Field.SessionID = unpackUUID(byteBuffer)
        this.UserInfo_Field.Flags = unpackInt(byteBuffer)
    }
}
