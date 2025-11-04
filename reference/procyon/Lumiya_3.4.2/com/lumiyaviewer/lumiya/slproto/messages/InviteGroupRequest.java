// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class InviteGroupRequest extends SLMessage
{
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    public ArrayList<InviteData> InviteData_Fields;
    
    public InviteGroupRequest() {
        this.InviteData_Fields = new ArrayList<InviteData>();
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.InviteData_Fields.size() * 32 + 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleInviteGroupRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)93);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.GroupData_Field.GroupID);
        byteBuffer.put((byte)this.InviteData_Fields.size());
        for (final InviteData inviteData : this.InviteData_Fields) {
            this.packUUID(byteBuffer, inviteData.InviteeID);
            this.packUUID(byteBuffer, inviteData.RoleID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = this.unpackUUID(byteBuffer);
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final InviteData e = new InviteData();
            e.InviteeID = this.unpackUUID(byteBuffer);
            e.RoleID = this.unpackUUID(byteBuffer);
            this.InviteData_Fields.add(e);
        }
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class GroupData
    {
        public UUID GroupID;
    }
    
    public static class InviteData
    {
        public UUID InviteeID;
        public UUID RoleID;
    }
}
