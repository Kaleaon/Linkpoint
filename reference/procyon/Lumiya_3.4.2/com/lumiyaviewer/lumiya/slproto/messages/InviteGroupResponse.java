// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class InviteGroupResponse extends SLMessage
{
    public InviteData InviteData_Field;
    
    public InviteGroupResponse() {
        this.zeroCoded = false;
        this.InviteData_Field = new InviteData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 72;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleInviteGroupResponse(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)94);
        this.packUUID(byteBuffer, this.InviteData_Field.AgentID);
        this.packUUID(byteBuffer, this.InviteData_Field.InviteeID);
        this.packUUID(byteBuffer, this.InviteData_Field.GroupID);
        this.packUUID(byteBuffer, this.InviteData_Field.RoleID);
        this.packInt(byteBuffer, this.InviteData_Field.MembershipFee);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.InviteData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.InviteData_Field.InviteeID = this.unpackUUID(byteBuffer);
        this.InviteData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.InviteData_Field.RoleID = this.unpackUUID(byteBuffer);
        this.InviteData_Field.MembershipFee = this.unpackInt(byteBuffer);
    }
    
    public static class InviteData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID InviteeID;
        public int MembershipFee;
        public UUID RoleID;
    }
}
