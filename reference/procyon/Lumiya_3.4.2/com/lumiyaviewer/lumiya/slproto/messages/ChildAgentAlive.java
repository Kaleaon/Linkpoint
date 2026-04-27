// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ChildAgentAlive extends SLMessage
{
    public AgentData AgentData_Field;
    
    public ChildAgentAlive() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 45;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleChildAgentAlive(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)26);
        this.packLong(byteBuffer, this.AgentData_Field.RegionHandle);
        this.packInt(byteBuffer, this.AgentData_Field.ViewerCircuitCode);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.AgentData_Field.ViewerCircuitCode = this.unpackInt(byteBuffer);
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public long RegionHandle;
        public UUID SessionID;
        public int ViewerCircuitCode;
    }
}
