package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EjectGroupMemberReply extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public EjectData EjectData_Field = new EjectData();
    public GroupData GroupData_Field = new GroupData();

    public static class AgentData {
        public UUID AgentID;
    }

    public static class EjectData {
        public boolean Success;
    }

    public static class GroupData {
        public UUID GroupID;
    }

    public EjectGroupMemberReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 37;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEjectGroupMemberReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 90);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
        packBoolean(byteBuffer, this.EjectData_Field.Success);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
        this.EjectData_Field.Success = unpackBoolean(byteBuffer);
    }
}
