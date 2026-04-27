package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class GroupRoleChanges extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<RoleChange> RoleChange_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    public static class RoleChange {
        public int Change;
        public UUID MemberID;
        public UUID RoleID;
    }

    public GroupRoleChanges() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        return (this.RoleChange_Fields.size() * 36) + 53;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleChanges(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 86);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        byteBuffer.put((byte) this.RoleChange_Fields.size());
        for (RoleChange roleChange : this.RoleChange_Fields) {
            packUUID(byteBuffer, roleChange.RoleID);
            packUUID(byteBuffer, roleChange.MemberID);
            packInt(byteBuffer, roleChange.Change);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            RoleChange roleChange = new RoleChange();
            roleChange.RoleID = unpackUUID(byteBuffer);
            roleChange.MemberID = unpackUUID(byteBuffer);
            roleChange.Change = unpackInt(byteBuffer);
            this.RoleChange_Fields.add(roleChange);
        }
    }
}
