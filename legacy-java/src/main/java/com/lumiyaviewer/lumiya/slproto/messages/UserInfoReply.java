package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class UserInfoReply extends SLMessage {
    public AgentData AgentData_Field;
    public UserData UserData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class UserData {
        public byte[] DirectoryVisibility;
        public byte[] EMail;
        public boolean IMViaEMail;
    }

    public UserInfoReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.UserData_Field = new UserData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 2 + this.UserData_Field.EMail.length + 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserInfoReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -112);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packBoolean(byteBuffer, this.UserData_Field.IMViaEMail);
        packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1);
        packVariable(byteBuffer, this.UserData_Field.EMail, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.UserData_Field.IMViaEMail = unpackBoolean(byteBuffer);
        this.UserData_Field.DirectoryVisibility = unpackVariable(byteBuffer, 1);
        this.UserData_Field.EMail = unpackVariable(byteBuffer, 2);
    }
}
