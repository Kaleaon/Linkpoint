package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GodKickUser : SLMessage {
    UserInfo UserInfo_Field = UserInfo()

    class UserInfo {
        UUID AgentID
        UUID GodID
        UUID GodSessionID
        Int KickFlags
        byte[] Reason
    }

    GodKickUser() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.UserInfo_Field.Reason.length + 54 + 4
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGodKickUser(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -91)
        packUUID(byteBuffer, this.UserInfo_Field.GodID)
        packUUID(byteBuffer, this.UserInfo_Field.GodSessionID)
        packUUID(byteBuffer, this.UserInfo_Field.AgentID)
        packInt(byteBuffer, this.UserInfo_Field.KickFlags)
        packVariable(byteBuffer, this.UserInfo_Field.Reason, 2)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.UserInfo_Field.GodID = unpackUUID(byteBuffer)
        this.UserInfo_Field.GodSessionID = unpackUUID(byteBuffer)
        this.UserInfo_Field.AgentID = unpackUUID(byteBuffer)
        this.UserInfo_Field.KickFlags = unpackInt(byteBuffer)
        this.UserInfo_Field.Reason = unpackVariable(byteBuffer, 2)
    }
}
