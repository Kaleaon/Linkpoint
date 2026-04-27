package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SetGroupAcceptNotices extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();
    public NewData NewData_Field = new NewData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public boolean AcceptNotices;
        public UUID GroupID;
    }

    public static class NewData {
        public boolean ListInProfile;
    }

    public SetGroupAcceptNotices() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 54;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSetGroupAcceptNotices(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 114);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.GroupID);
        packBoolean(byteBuffer, this.Data_Field.AcceptNotices);
        packBoolean(byteBuffer, this.NewData_Field.ListInProfile);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.GroupID = unpackUUID(byteBuffer);
        this.Data_Field.AcceptNotices = unpackBoolean(byteBuffer);
        this.NewData_Field.ListInProfile = unpackBoolean(byteBuffer);
    }
}
