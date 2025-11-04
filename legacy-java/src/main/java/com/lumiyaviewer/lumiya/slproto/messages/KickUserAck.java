package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class KickUserAck extends SLMessage {
    public UserInfo UserInfo_Field;

    /* loaded from: classes.dex */
    public static class UserInfo {
        public int Flags;
        public UUID SessionID;
    }

    public KickUserAck() {
        this.zeroCoded = false;
        this.UserInfo_Field = new UserInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 24;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKickUserAck(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -92);
        packUUID(byteBuffer, this.UserInfo_Field.SessionID);
        packInt(byteBuffer, this.UserInfo_Field.Flags);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.UserInfo_Field.SessionID = unpackUUID(byteBuffer);
        this.UserInfo_Field.Flags = unpackInt(byteBuffer);
    }
}
