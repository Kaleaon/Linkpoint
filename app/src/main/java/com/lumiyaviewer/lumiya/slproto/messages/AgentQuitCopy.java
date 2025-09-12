package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentQuitCopy extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public FuseBlock FuseBlock_Field = new FuseBlock();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class FuseBlock {
        public int ViewerCircuitCode;
    }

    public AgentQuitCopy() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 40;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentQuitCopy(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 85);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packInt(byteBuffer, this.FuseBlock_Field.ViewerCircuitCode);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.FuseBlock_Field.ViewerCircuitCode = unpackInt(byteBuffer);
    }
}
