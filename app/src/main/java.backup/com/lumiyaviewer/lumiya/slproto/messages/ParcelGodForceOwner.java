package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ParcelGodForceOwner extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public int LocalID;
        public UUID OwnerID;
    }

    public ParcelGodForceOwner() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 56;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelGodForceOwner(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -42);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.Data_Field.OwnerID);
        packInt(byteBuffer, this.Data_Field.LocalID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.OwnerID = unpackUUID(byteBuffer);
        this.Data_Field.LocalID = unpackInt(byteBuffer);
    }
}
