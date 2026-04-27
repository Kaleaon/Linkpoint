package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UserInfoReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public UserData UserData_Field = new UserData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class UserData {
        public byte[] DirectoryVisibility;
        public byte[] EMail;
        public boolean IMViaEMail;
    }

    public UserInfoReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.UserData_Field.DirectoryVisibility.length + 2 + 2 + this.UserData_Field.EMail.length + 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUserInfoReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) -112);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packBoolean(byteBuffer, this.UserData_Field.IMViaEMail);
        packVariable(byteBuffer, this.UserData_Field.DirectoryVisibility, 1);
        packVariable(byteBuffer, this.UserData_Field.EMail, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.UserData_Field.IMViaEMail = unpackBoolean(byteBuffer);
        this.UserData_Field.DirectoryVisibility = unpackVariable(byteBuffer, 1);
        this.UserData_Field.EMail = unpackVariable(byteBuffer, 2);
    }
}
