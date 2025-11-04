package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class KickUser extends SLMessage {
    public TargetBlock TargetBlock_Field;
    public UserInfo UserInfo_Field;

    /* loaded from: classes.dex */
    public static class TargetBlock {
        public Inet4Address TargetIP;
        public int TargetPort;
    }

    /* loaded from: classes.dex */
    public static class UserInfo {
        public UUID AgentID;
        public byte[] Reason;
        public UUID SessionID;
    }

    public KickUser() {
        this.zeroCoded = false;
        this.TargetBlock_Field = new TargetBlock();
        this.UserInfo_Field = new UserInfo();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.UserInfo_Field.Reason.length + 34 + 10;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleKickUser(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -93);
        packIPAddress(byteBuffer, this.TargetBlock_Field.TargetIP);
        packShort(byteBuffer, (short) this.TargetBlock_Field.TargetPort);
        packUUID(byteBuffer, this.UserInfo_Field.AgentID);
        packUUID(byteBuffer, this.UserInfo_Field.SessionID);
        packVariable(byteBuffer, this.UserInfo_Field.Reason, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TargetBlock_Field.TargetIP = unpackIPAddress(byteBuffer);
        this.TargetBlock_Field.TargetPort = unpackShort(byteBuffer) & 65535;
        this.UserInfo_Field.AgentID = unpackUUID(byteBuffer);
        this.UserInfo_Field.SessionID = unpackUUID(byteBuffer);
        this.UserInfo_Field.Reason = unpackVariable(byteBuffer, 2);
    }
}
