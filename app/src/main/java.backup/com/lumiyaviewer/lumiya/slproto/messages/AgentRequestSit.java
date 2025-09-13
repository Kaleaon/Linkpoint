package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AgentRequestSit extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public TargetObject TargetObject_Field = new TargetObject();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class TargetObject {
        public LLVector3 Offset;
        public UUID TargetID;
    }

    public AgentRequestSit() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 61;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentRequestSit(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) 6);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.TargetObject_Field.TargetID);
        packLLVector3(byteBuffer, this.TargetObject_Field.Offset);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.TargetObject_Field.TargetID = unpackUUID(byteBuffer);
        this.TargetObject_Field.Offset = unpackLLVector3(byteBuffer);
    }
}
