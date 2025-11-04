// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentDropGroup extends SLMessage
{
    public AgentData AgentData_Field;
    
    public AgentDropGroup() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentDropGroup(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-122));
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
    }
}
