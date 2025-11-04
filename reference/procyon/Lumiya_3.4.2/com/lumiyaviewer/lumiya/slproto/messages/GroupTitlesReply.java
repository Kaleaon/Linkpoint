// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupTitlesReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<GroupData> GroupData_Fields;
    
    public GroupTitlesReply() {
        this.GroupData_Fields = new ArrayList<GroupData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.GroupData_Fields.iterator();
        int n = 53;
        while (iterator.hasNext()) {
            n += iterator.next().Title.length + 1 + 16 + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupTitlesReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)120);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.AgentData_Field.RequestID);
        byteBuffer.put((byte)this.GroupData_Fields.size());
        for (final GroupData groupData : this.GroupData_Fields) {
            this.packVariable(byteBuffer, groupData.Title, 1);
            this.packUUID(byteBuffer, groupData.RoleID);
            this.packBoolean(byteBuffer, groupData.Selected);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.RequestID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final GroupData e = new GroupData();
            e.Title = this.unpackVariable(byteBuffer, 1);
            e.RoleID = this.unpackUUID(byteBuffer);
            e.Selected = this.unpackBoolean(byteBuffer);
            this.GroupData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID RequestID;
    }
    
    public static class GroupData
    {
        public UUID RoleID;
        public boolean Selected;
        public byte[] Title;
    }
}
