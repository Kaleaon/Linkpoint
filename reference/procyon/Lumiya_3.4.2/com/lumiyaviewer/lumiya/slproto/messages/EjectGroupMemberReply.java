// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EjectGroupMemberReply extends SLMessage
{
    public AgentData AgentData_Field;
    public EjectData EjectData_Field;
    public GroupData GroupData_Field;
    
    public EjectGroupMemberReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
        this.EjectData_Field = new EjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 37;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEjectGroupMemberReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)90);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.GroupData_Field.GroupID);
        this.packBoolean(byteBuffer, this.EjectData_Field.Success);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.EjectData_Field.Success = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class EjectData
    {
        public boolean Success;
    }
    
    public static class GroupData
    {
        public UUID GroupID;
    }
}
