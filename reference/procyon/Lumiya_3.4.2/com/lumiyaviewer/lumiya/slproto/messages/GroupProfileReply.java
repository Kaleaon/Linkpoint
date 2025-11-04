// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GroupProfileReply extends SLMessage
{
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    
    public GroupProfileReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.GroupData_Field.Name.length + 17 + 2 + this.GroupData_Field.Charter.length + 1 + 1 + this.GroupData_Field.MemberTitle.length + 8 + 16 + 16 + 4 + 1 + 4 + 4 + 4 + 1 + 1 + 16 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGroupProfileReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)96);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.GroupData_Field.GroupID);
        this.packVariable(byteBuffer, this.GroupData_Field.Name, 1);
        this.packVariable(byteBuffer, this.GroupData_Field.Charter, 2);
        this.packBoolean(byteBuffer, this.GroupData_Field.ShowInList);
        this.packVariable(byteBuffer, this.GroupData_Field.MemberTitle, 1);
        this.packLong(byteBuffer, this.GroupData_Field.PowersMask);
        this.packUUID(byteBuffer, this.GroupData_Field.InsigniaID);
        this.packUUID(byteBuffer, this.GroupData_Field.FounderID);
        this.packInt(byteBuffer, this.GroupData_Field.MembershipFee);
        this.packBoolean(byteBuffer, this.GroupData_Field.OpenEnrollment);
        this.packInt(byteBuffer, this.GroupData_Field.Money);
        this.packInt(byteBuffer, this.GroupData_Field.GroupMembershipCount);
        this.packInt(byteBuffer, this.GroupData_Field.GroupRolesCount);
        this.packBoolean(byteBuffer, this.GroupData_Field.AllowPublish);
        this.packBoolean(byteBuffer, this.GroupData_Field.MaturePublish);
        this.packUUID(byteBuffer, this.GroupData_Field.OwnerRole);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.GroupData_Field.Charter = this.unpackVariable(byteBuffer, 2);
        this.GroupData_Field.ShowInList = this.unpackBoolean(byteBuffer);
        this.GroupData_Field.MemberTitle = this.unpackVariable(byteBuffer, 1);
        this.GroupData_Field.PowersMask = this.unpackLong(byteBuffer);
        this.GroupData_Field.InsigniaID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.FounderID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.MembershipFee = this.unpackInt(byteBuffer);
        this.GroupData_Field.OpenEnrollment = this.unpackBoolean(byteBuffer);
        this.GroupData_Field.Money = this.unpackInt(byteBuffer);
        this.GroupData_Field.GroupMembershipCount = this.unpackInt(byteBuffer);
        this.GroupData_Field.GroupRolesCount = this.unpackInt(byteBuffer);
        this.GroupData_Field.AllowPublish = this.unpackBoolean(byteBuffer);
        this.GroupData_Field.MaturePublish = this.unpackBoolean(byteBuffer);
        this.GroupData_Field.OwnerRole = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class GroupData
    {
        public boolean AllowPublish;
        public byte[] Charter;
        public UUID FounderID;
        public UUID GroupID;
        public int GroupMembershipCount;
        public int GroupRolesCount;
        public UUID InsigniaID;
        public boolean MaturePublish;
        public byte[] MemberTitle;
        public int MembershipFee;
        public int Money;
        public byte[] Name;
        public boolean OpenEnrollment;
        public UUID OwnerRole;
        public long PowersMask;
        public boolean ShowInList;
    }
}
