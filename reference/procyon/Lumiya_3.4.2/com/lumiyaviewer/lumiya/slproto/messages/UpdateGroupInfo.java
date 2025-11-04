// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateGroupInfo extends SLMessage
{
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    
    public UpdateGroupInfo() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.GroupData_Field.Charter.length + 18 + 1 + 16 + 4 + 1 + 1 + 1 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateGroupInfo(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)85);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.GroupData_Field.GroupID);
        this.packVariable(byteBuffer, this.GroupData_Field.Charter, 2);
        this.packBoolean(byteBuffer, this.GroupData_Field.ShowInList);
        this.packUUID(byteBuffer, this.GroupData_Field.InsigniaID);
        this.packInt(byteBuffer, this.GroupData_Field.MembershipFee);
        this.packBoolean(byteBuffer, this.GroupData_Field.OpenEnrollment);
        this.packBoolean(byteBuffer, this.GroupData_Field.AllowPublish);
        this.packBoolean(byteBuffer, this.GroupData_Field.MaturePublish);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.Charter = this.unpackVariable(byteBuffer, 2);
        this.GroupData_Field.ShowInList = this.unpackBoolean(byteBuffer);
        this.GroupData_Field.InsigniaID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.MembershipFee = this.unpackInt(byteBuffer);
        this.GroupData_Field.OpenEnrollment = this.unpackBoolean(byteBuffer);
        this.GroupData_Field.AllowPublish = this.unpackBoolean(byteBuffer);
        this.GroupData_Field.MaturePublish = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class GroupData
    {
        public boolean AllowPublish;
        public byte[] Charter;
        public UUID GroupID;
        public UUID InsigniaID;
        public boolean MaturePublish;
        public int MembershipFee;
        public boolean OpenEnrollment;
        public boolean ShowInList;
    }
}
