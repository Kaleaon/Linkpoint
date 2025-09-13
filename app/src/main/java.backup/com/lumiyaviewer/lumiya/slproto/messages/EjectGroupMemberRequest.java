package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class EjectGroupMemberRequest extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<EjectData> EjectData_Fields = new ArrayList<>();
    public GroupData GroupData_Field;

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class EjectData {
        public UUID EjecteeID;
    }

    public static class GroupData {
        public UUID GroupID;
    }

    public EjectGroupMemberRequest() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }

    public int CalcPayloadSize() {
        return (this.EjectData_Fields.size() * 16) + 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEjectGroupMemberRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 89);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
        byteBuffer.put((byte) this.EjectData_Fields.size());
        for (EjectData ejectData : this.EjectData_Fields) {
            packUUID(byteBuffer, ejectData.EjecteeID);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            EjectData ejectData = new EjectData();
            ejectData.EjecteeID = unpackUUID(byteBuffer);
            this.EjectData_Fields.add(ejectData);
        }
    }
}
