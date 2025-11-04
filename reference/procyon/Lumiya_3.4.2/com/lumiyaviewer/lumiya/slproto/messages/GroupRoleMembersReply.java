// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupRoleMembersReply extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<MemberData> MemberData_Fields;
    
    public GroupRoleMembersReply() {
        this.MemberData_Fields = new ArrayList<MemberData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.MemberData_Fields.size() * 32 + 57;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupRoleMembersReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)118);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packUUID(byteBuffer, this.AgentData_Field.RequestID);
        this.packInt(byteBuffer, this.AgentData_Field.TotalPairs);
        byteBuffer.put((byte)this.MemberData_Fields.size());
        for (final MemberData memberData : this.MemberData_Fields) {
            this.packUUID(byteBuffer, memberData.RoleID);
            this.packUUID(byteBuffer, memberData.MemberID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.RequestID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.TotalPairs = this.unpackInt(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final MemberData e = new MemberData();
            e.RoleID = this.unpackUUID(byteBuffer);
            e.MemberID = this.unpackUUID(byteBuffer);
            this.MemberData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID RequestID;
        public int TotalPairs;
    }
    
    public static class MemberData
    {
        public UUID MemberID;
        public UUID RoleID;
    }
}
