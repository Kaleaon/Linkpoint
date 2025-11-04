// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class JoinGroupReply extends SLMessage
{
    public AgentData AgentData_Field;
    public GroupData GroupData_Field;
    
    public JoinGroupReply() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GroupData_Field = new GroupData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 37;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleJoinGroupReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)88);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.GroupData_Field.GroupID);
        this.packBoolean(byteBuffer, this.GroupData_Field.Success);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.GroupData_Field.Success = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class GroupData
    {
        public UUID GroupID;
        public boolean Success;
    }
}
