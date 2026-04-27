package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class GodKickUser extends SLMessage {
    public UserInfo UserInfo_Field = new UserInfo();

    public static class UserInfo {
        public UUID AgentID;
        public UUID GodID;
        public UUID GodSessionID;
        public int KickFlags;
        public byte[] Reason;
    }

    public GodKickUser() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.UserInfo_Field.Reason.length + 54 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGodKickUser(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -91);
        packUUID(byteBuffer, this.UserInfo_Field.GodID);
        packUUID(byteBuffer, this.UserInfo_Field.GodSessionID);
        packUUID(byteBuffer, this.UserInfo_Field.AgentID);
        packInt(byteBuffer, this.UserInfo_Field.KickFlags);
        packVariable(byteBuffer, this.UserInfo_Field.Reason, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.UserInfo_Field.GodID = unpackUUID(byteBuffer);
        this.UserInfo_Field.GodSessionID = unpackUUID(byteBuffer);
        this.UserInfo_Field.AgentID = unpackUUID(byteBuffer);
        this.UserInfo_Field.KickFlags = unpackInt(byteBuffer);
        this.UserInfo_Field.Reason = unpackVariable(byteBuffer, 2);
    }
}
