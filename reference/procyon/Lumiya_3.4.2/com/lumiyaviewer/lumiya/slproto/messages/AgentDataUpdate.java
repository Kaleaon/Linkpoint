// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentDataUpdate extends SLMessage
{
    public AgentData AgentData_Field;
    
    public AgentDataUpdate() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AgentData_Field.FirstName.length + 17 + 1 + this.AgentData_Field.LastName.length + 1 + this.AgentData_Field.GroupTitle.length + 16 + 8 + 1 + this.AgentData_Field.GroupName.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentDataUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-125));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packVariable(byteBuffer, this.AgentData_Field.FirstName, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.LastName, 1);
        this.packVariable(byteBuffer, this.AgentData_Field.GroupTitle, 1);
        this.packUUID(byteBuffer, this.AgentData_Field.ActiveGroupID);
        this.packLong(byteBuffer, this.AgentData_Field.GroupPowers);
        this.packVariable(byteBuffer, this.AgentData_Field.GroupName, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.FirstName = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.LastName = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.GroupTitle = this.unpackVariable(byteBuffer, 1);
        this.AgentData_Field.ActiveGroupID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupPowers = this.unpackLong(byteBuffer);
        this.AgentData_Field.GroupName = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID ActiveGroupID;
        public UUID AgentID;
        public byte[] FirstName;
        public byte[] GroupName;
        public long GroupPowers;
        public byte[] GroupTitle;
        public byte[] LastName;
    }
}
