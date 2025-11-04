// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupMembersReply extends SLMessage
{
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList<MemberData> MemberData_Fields;
    
    public GroupMembersReply() {
        this.MemberData_Fields = new ArrayList<MemberData>();
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }
    
    @Override
    public int CalcPayloadSize() {
        final Iterator<Object> iterator = this.MemberData_Fields.iterator();
        int n = 57;
        while (iterator.hasNext()) {
            final MemberData memberData = iterator.next();
            n += memberData.Title.length + (memberData.OnlineStatus.length + 21 + 8 + 1) + 1;
        }
        return n;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupMembersReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)111);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.GroupData_Field.GroupID);
        this.packUUID(byteBuffer, this.GroupData_Field.RequestID);
        this.packInt(byteBuffer, this.GroupData_Field.MemberCount);
        byteBuffer.put((byte)this.MemberData_Fields.size());
        for (final MemberData memberData : this.MemberData_Fields) {
            this.packUUID(byteBuffer, memberData.AgentID);
            this.packInt(byteBuffer, memberData.Contribution);
            this.packVariable(byteBuffer, memberData.OnlineStatus, 1);
            this.packLong(byteBuffer, memberData.AgentPowers);
            this.packVariable(byteBuffer, memberData.Title, 1);
            this.packBoolean(byteBuffer, memberData.IsOwner);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.MemberCount = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final MemberData e = new MemberData();
            e.AgentID = this.unpackUUID(byteBuffer);
            e.Contribution = this.unpackInt(byteBuffer);
            e.OnlineStatus = this.unpackVariable(byteBuffer, 1);
            e.AgentPowers = this.unpackLong(byteBuffer);
            e.Title = this.unpackVariable(byteBuffer, 1);
            e.IsOwner = this.unpackBoolean(byteBuffer);
            this.MemberData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class GroupData
    {
        public UUID GroupID;
        public int MemberCount;
        public UUID RequestID;
    }
    
    public static class MemberData
    {
        public UUID AgentID;
        public long AgentPowers;
        public int Contribution;
        public boolean IsOwner;
        public byte[] OnlineStatus;
        public byte[] Title;
    }
}
