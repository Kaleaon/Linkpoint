package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ChildAgentAlive extends SLMessage {
    public AgentData AgentData_Field = new AgentData();

    public static class AgentData {
        public UUID AgentID;
        public long RegionHandle;
        public UUID SessionID;
        public int ViewerCircuitCode;
    }

    public ChildAgentAlive() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 45;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleChildAgentAlive(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.SUB);
        packLong(byteBuffer, this.AgentData_Field.RegionHandle);
        packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.RegionHandle = unpackLong(byteBuffer);
        this.AgentData_Field.ViewerCircuitCode = unpackInt(byteBuffer);
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
    }
}
