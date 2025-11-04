// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentFOV extends SLMessage
{
    public AgentData AgentData_Field;
    public FOVBlock FOVBlock_Field;
    
    public AgentFOV() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.FOVBlock_Field = new FOVBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 48;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentFOV(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)82);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.CircuitCode);
        this.packInt(byteBuffer, this.FOVBlock_Field.GenCounter);
        this.packFloat(byteBuffer, this.FOVBlock_Field.VerticalAngle);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.CircuitCode = this.unpackInt(byteBuffer);
        this.FOVBlock_Field.GenCounter = this.unpackInt(byteBuffer);
        this.FOVBlock_Field.VerticalAngle = this.unpackFloat(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;
    }
    
    public static class FOVBlock
    {
        public int GenCounter;
        public float VerticalAngle;
    }
}
