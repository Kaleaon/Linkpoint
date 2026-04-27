package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SimWideDeletes extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public DataBlock DataBlock_Field = new DataBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class DataBlock {
        public int Flags;
        public UUID TargetID;
    }

    public SimWideDeletes() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 56;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleSimWideDeletes(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -127);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.DataBlock_Field.TargetID);
        packInt(byteBuffer, this.DataBlock_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.DataBlock_Field.TargetID = unpackUUID(byteBuffer);
        this.DataBlock_Field.Flags = unpackInt(byteBuffer);
    }
}
