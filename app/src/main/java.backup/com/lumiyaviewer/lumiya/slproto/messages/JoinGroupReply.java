package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class JoinGroupReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public GroupData GroupData_Field = new GroupData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class GroupData {
        public UUID GroupID;
        public boolean Success;
    }

    public JoinGroupReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleJoinGroupReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 88);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
        packBoolean(byteBuffer, this.GroupData_Field.Success);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
        this.GroupData_Field.Success = unpackBoolean(byteBuffer);
    }
}
