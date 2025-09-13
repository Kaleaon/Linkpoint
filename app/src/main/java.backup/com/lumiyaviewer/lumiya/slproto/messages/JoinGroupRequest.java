package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class JoinGroupRequest extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public GroupData GroupData_Field = new GroupData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class GroupData {
        public UUID GroupID;
    }

    public JoinGroupRequest() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 52;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleJoinGroupRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 87);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
    }
}
