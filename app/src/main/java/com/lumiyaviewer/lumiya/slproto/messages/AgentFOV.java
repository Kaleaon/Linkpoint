package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentFOV extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public FOVBlock FOVBlock_Field = new FOVBlock();

    public static class AgentData {
        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;
    }

    public static class FOVBlock {
        public int GenCounter;
        public float VerticalAngle;
    }

    public AgentFOV() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 48;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentFOV(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 82);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.AgentData_Field.CircuitCode);
        packInt(byteBuffer, this.FOVBlock_Field.GenCounter);
        packFloat(byteBuffer, this.FOVBlock_Field.VerticalAngle);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer);
        this.FOVBlock_Field.GenCounter = unpackInt(byteBuffer);
        this.FOVBlock_Field.VerticalAngle = unpackFloat(byteBuffer);
    }
}
