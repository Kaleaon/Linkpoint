package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UpdateUserInfo extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public UserData UserData_Field = new UserData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class UserData {
        public byte[] DirectoryVisibility;
        public boolean IMViaEMail;
    }

    public UpdateUserInfo() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 36;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateUserInfo(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -111);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packBoolean(byteBuffer, this.UserData_Field.IMViaEMail);
        packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.UserData_Field.IMViaEMail = unpackBoolean(byteBuffer);
        this.UserData_Field.DirectoryVisibility = unpackVariable(byteBuffer, 1);
    }
}
