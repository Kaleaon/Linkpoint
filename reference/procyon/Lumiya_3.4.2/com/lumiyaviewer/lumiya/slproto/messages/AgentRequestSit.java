// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentRequestSit extends SLMessage
{
    public AgentData AgentData_Field;
    public TargetObject TargetObject_Field;
    
    public AgentRequestSit() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.TargetObject_Field = new TargetObject();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 61;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentRequestSit(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)6);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.TargetObject_Field.TargetID);
        this.packLLVector3(byteBuffer, this.TargetObject_Field.Offset);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.TargetObject_Field.TargetID = this.unpackUUID(byteBuffer);
        this.TargetObject_Field.Offset = this.unpackLLVector3(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class TargetObject
    {
        public LLVector3 Offset;
        public UUID TargetID;
    }
}
