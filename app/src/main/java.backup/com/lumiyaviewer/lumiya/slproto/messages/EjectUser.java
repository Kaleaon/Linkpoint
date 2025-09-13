package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EjectUser extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public int Flags;
        public UUID TargetID;
    }

    public EjectUser() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 56;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEjectUser(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -89);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.TargetID);
        packInt(byteBuffer, this.Data_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.TargetID = unpackUUID(byteBuffer);
        this.Data_Field.Flags = unpackInt(byteBuffer);
    }
}
