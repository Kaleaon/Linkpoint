// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupRoleDataReply extends SLMessage
{
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList<RoleData> RoleData_Fields;
    
    public GroupRoleDataReply() {
        this.RoleData_Fields = new ArrayList<RoleData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.RoleData_Fields.iterator();
        int n = 57;
        while (iterator.hasNext()) {
            final RoleData roleData = iterator.next();
            n += roleData.Description.length + (roleData.Name.length + 17 + 1 + roleData.Title.length + 1) + 8 + 4;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupRoleDataReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)116);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.GroupData_Field.GroupID);
        this.packUUID(byteBuffer, this.GroupData_Field.RequestID);
        this.packInt(byteBuffer, this.GroupData_Field.RoleCount);
        byteBuffer.put((byte)this.RoleData_Fields.size());
        for (final RoleData roleData : this.RoleData_Fields) {
            this.packUUID(byteBuffer, roleData.RoleID);
            this.packVariable(byteBuffer, roleData.Name, 1);
            this.packVariable(byteBuffer, roleData.Title, 1);
            this.packVariable(byteBuffer, roleData.Description, 1);
            this.packLong(byteBuffer, roleData.Powers);
            this.packInt(byteBuffer, roleData.Members);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.RoleCount = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RoleData e = new RoleData();
            e.RoleID = this.unpackUUID(byteBuffer);
            e.Name = this.unpackVariable(byteBuffer, 1);
            e.Title = this.unpackVariable(byteBuffer, 1);
            e.Description = this.unpackVariable(byteBuffer, 1);
            e.Powers = this.unpackLong(byteBuffer);
            e.Members = this.unpackInt(byteBuffer);
            this.RoleData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class GroupData
    {
        public UUID GroupID;
        public UUID RequestID;
        public int RoleCount;
    }
    
    public static class RoleData
    {
        public byte[] Description;
        public int Members;
        public byte[] Name;
        public long Powers;
        public UUID RoleID;
        public byte[] Title;
    }
}
