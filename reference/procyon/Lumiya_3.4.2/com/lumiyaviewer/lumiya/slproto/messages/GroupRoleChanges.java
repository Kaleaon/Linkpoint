// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupRoleChanges extends SLMessage
{
    public AgentData AgentData_Field;
    public ArrayList<RoleChange> RoleChange_Fields;
    
    public GroupRoleChanges() {
        this.RoleChange_Fields = new ArrayList<RoleChange>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.RoleChange_Fields.size() * 36 + 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupRoleChanges(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)86);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        byteBuffer.put((byte)this.RoleChange_Fields.size());
        for (final RoleChange roleChange : this.RoleChange_Fields) {
            this.packUUID(byteBuffer, roleChange.RoleID);
            this.packUUID(byteBuffer, roleChange.MemberID);
            this.packInt(byteBuffer, roleChange.Change);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final RoleChange e = new RoleChange();
            e.RoleID = this.unpackUUID(byteBuffer);
            e.MemberID = this.unpackUUID(byteBuffer);
            e.Change = this.unpackInt(byteBuffer);
            this.RoleChange_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }
    
    public static class RoleChange
    {
        public int Change;
        public UUID MemberID;
        public UUID RoleID;
    }
}
