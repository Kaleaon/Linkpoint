package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class KickUserAck extends SLMessage {
    public UserInfo UserInfo_Field = new UserInfo();

    public static class UserInfo {
        public int Flags;
        public UUID SessionID;
    }

    public KickUserAck() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 24;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKickUserAck(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -92);
        packUUID(byteBuffer, this.UserInfo_Field.SessionID);
        packInt(byteBuffer, this.UserInfo_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.UserInfo_Field.SessionID = unpackUUID(byteBuffer);
        this.UserInfo_Field.Flags = unpackInt(byteBuffer);
    }
}
