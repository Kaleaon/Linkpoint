package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
/* loaded from: classes.dex */
public class GroupRoleChanges extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<RoleChange> RoleChange_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    /* loaded from: classes.dex */
    public static class RoleChange {
        public int Change;
        public UUID MemberID;
        public UUID RoleID;
    }

    public GroupRoleChanges() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return (this.RoleChange_Fields.size() * 36) + 53;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleChanges(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
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

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            RoleChange roleChange = new RoleChange();
            roleChange.RoleID = unpackUUID(byteBuffer);
            roleChange.MemberID = unpackUUID(byteBuffer);
            roleChange.Change = unpackInt(byteBuffer);
            this.RoleChange_Fields.add(roleChange);
        }
    }
}
