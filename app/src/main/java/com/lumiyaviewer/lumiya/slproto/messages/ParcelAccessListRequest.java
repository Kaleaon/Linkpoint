package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ParcelAccessListRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public Data Data_Field = new Data();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class Data {
        public int Flags;
        public int LocalID;
        public int SequenceID;
    }

    public ParcelAccessListRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 48;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelAccessListRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -41);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.Data_Field.SequenceID);
        packInt(byteBuffer, this.Data_Field.Flags);
        packInt(byteBuffer, this.Data_Field.LocalID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.Data_Field.SequenceID = unpackInt(byteBuffer);
        this.Data_Field.Flags = unpackInt(byteBuffer);
        this.Data_Field.LocalID = unpackInt(byteBuffer);
    }
}
