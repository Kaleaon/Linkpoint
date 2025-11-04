// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentQuitCopy extends SLMessage
{
    public AgentData AgentData_Field;
    public FuseBlock FuseBlock_Field;
    
    public AgentQuitCopy() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.FuseBlock_Field = new FuseBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 40;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentQuitCopy(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)85);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.FuseBlock_Field.ViewerCircuitCode);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.FuseBlock_Field.ViewerCircuitCode = this.unpackInt(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class FuseBlock
    {
        public int ViewerCircuitCode;
    }
}
