package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GodKickUser : SLMessage() {
    public UserInfo UserInfo_Field = UserInfo()

    @JvmStatic
    class UserInfo {
        public UUID AgentID
        public UUID GodID
        public UUID GodSessionID
        public Int KickFlags
        public Byte[] Reason
    }

    public GodKickUser() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.UserInfo_Field.Reason.length + 54 + 4
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGodKickUser(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -91)
        packUUID(byteBuffer, this.UserInfo_Field.GodID)
        packUUID(byteBuffer, this.UserInfo_Field.GodSessionID)
        packUUID(byteBuffer, this.UserInfo_Field.AgentID)
        packInt(byteBuffer, this.UserInfo_Field.KickFlags)
        packVariable(byteBuffer, this.UserInfo_Field.Reason, 2)
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.UserInfo_Field.GodID = unpackUUID(byteBuffer)
        this.UserInfo_Field.GodSessionID = unpackUUID(byteBuffer)
        this.UserInfo_Field.AgentID = unpackUUID(byteBuffer)
        this.UserInfo_Field.KickFlags = unpackInt(byteBuffer)
        this.UserInfo_Field.Reason = unpackVariable(byteBuffer, 2)
    }
}
