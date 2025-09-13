package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class TrackAgent extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public TargetData TargetData_Field = new TargetData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class TargetData {
        public UUID PreyID;
    }

    public TrackAgent() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 52;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleTrackAgent(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -126);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.TargetData_Field.PreyID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.TargetData_Field.PreyID = unpackUUID(byteBuffer);
    }
}
