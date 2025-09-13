package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ParcelPropertiesRequestByID extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ParcelData ParcelData_Field = new ParcelData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class ParcelData {
        public int LocalID;
        public int SequenceID;
    }

    public ParcelPropertiesRequestByID() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 44;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleParcelPropertiesRequestByID(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -59);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.ParcelData_Field.SequenceID);
        packInt(byteBuffer, this.ParcelData_Field.LocalID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.ParcelData_Field.SequenceID = unpackInt(byteBuffer);
        this.ParcelData_Field.LocalID = unpackInt(byteBuffer);
    }
}
