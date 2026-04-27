package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
public class GroupRoleDataReply extends SLMessage {
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList<RoleData> RoleData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class GroupData {
        public UUID GroupID;
        public UUID RequestID;
        public int RoleCount;
    }

    /* loaded from: classes.dex */
    public static class RoleData {
        public byte[] Description;
        public int Members;
        public byte[] Name;
        public long Powers;
        public UUID RoleID;
        public byte[] Title;
    }

    public GroupRoleDataReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 57;
        Iterator<T> it = this.RoleData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            RoleData roleData = (RoleData) it.next();
            i = roleData.Description.length + roleData.Name.length + 17 + 1 + roleData.Title.length + 1 + 8 + 4 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleDataReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 116);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.GroupData_Field.GroupID);
        packUUID(byteBuffer, this.GroupData_Field.RequestID);
        packInt(byteBuffer, this.GroupData_Field.RoleCount);
        byteBuffer.put((byte) this.RoleData_Fields.size());
        for (RoleData roleData : this.RoleData_Fields) {
            packUUID(byteBuffer, roleData.RoleID);
            packVariable(byteBuffer, roleData.Name, 1);
            packVariable(byteBuffer, roleData.Title, 1);
            packVariable(byteBuffer, roleData.Description, 1);
            packLong(byteBuffer, roleData.Powers);
            packInt(byteBuffer, roleData.Members);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer);
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer);
        this.GroupData_Field.RoleCount = unpackInt(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            RoleData roleData = new RoleData();
            roleData.RoleID = unpackUUID(byteBuffer);
            roleData.Name = unpackVariable(byteBuffer, 1);
            roleData.Title = unpackVariable(byteBuffer, 1);
            roleData.Description = unpackVariable(byteBuffer, 1);
            roleData.Powers = unpackLong(byteBuffer);
            roleData.Members = unpackInt(byteBuffer);
            this.RoleData_Fields.add(roleData);
        }
    }
}
