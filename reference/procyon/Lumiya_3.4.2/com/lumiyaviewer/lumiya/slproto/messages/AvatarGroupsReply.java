// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarGroupsReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<GroupData> GroupData_Fields;
    public NewGroupData NewGroupData_Field;
    
    public AvatarGroupsReply() {
        this.GroupData_Fields = new ArrayList<GroupData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.NewGroupData_Field = new NewGroupData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.GroupData_Fields.iterator();
        int n = 37;
        while (iterator.hasNext()) {
            final GroupData groupData = iterator.next();
            n += groupData.GroupName.length + (groupData.GroupTitle.length + 10 + 16 + 1) + 16;
        }
        return n + 1;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarGroupsReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-83));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.AvatarID);
        byteBuffer.put((byte)this.GroupData_Fields.size());
        for (final GroupData groupData : this.GroupData_Fields) {
            this.packLong(byteBuffer, groupData.GroupPowers);
            this.packBoolean(byteBuffer, groupData.AcceptNotices);
            this.packVariable(byteBuffer, groupData.GroupTitle, 1);
            this.packUUID(byteBuffer, groupData.GroupID);
            this.packVariable(byteBuffer, groupData.GroupName, 1);
            this.packUUID(byteBuffer, groupData.GroupInsigniaID);
        }
        this.packBoolean(byteBuffer, this.NewGroupData_Field.ListInProfile);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.AvatarID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final GroupData e = new GroupData();
            e.GroupPowers = this.unpackLong(byteBuffer);
            e.AcceptNotices = this.unpackBoolean(byteBuffer);
            e.GroupTitle = this.unpackVariable(byteBuffer, 1);
            e.GroupID = this.unpackUUID(byteBuffer);
            e.GroupName = this.unpackVariable(byteBuffer, 1);
            e.GroupInsigniaID = this.unpackUUID(byteBuffer);
            this.GroupData_Fields.add(e);
        }
        this.NewGroupData_Field.ListInProfile = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID AvatarID;
    }
    
    public static class GroupData
    {
        public boolean AcceptNotices;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public byte[] GroupName;
        public long GroupPowers;
        public byte[] GroupTitle;
    }
    
    public static class NewGroupData
    {
        public boolean ListInProfile;
    }
}
