package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class UpdateUserInfo extends SLMessage {
    public AgentData AgentData_Field;
    public UserData UserData_Field;

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class UserData {
        public byte[] DirectoryVisibility;
        public boolean IMViaEMail;
    }

    public UpdateUserInfo() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.UserData_Field = new UserData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 36;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateUserInfo(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -111);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packBoolean(byteBuffer, this.UserData_Field.IMViaEMail);
        packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.UserData_Field.IMViaEMail = unpackBoolean(byteBuffer);
        this.UserData_Field.DirectoryVisibility = unpackVariable(byteBuffer, 1);
    }
}
