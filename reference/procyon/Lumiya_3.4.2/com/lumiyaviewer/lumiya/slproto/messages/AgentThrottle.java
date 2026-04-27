// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentThrottle extends SLMessage
{
    public AgentData AgentData_Field;
    public Throttle Throttle_Field;
    
    public AgentThrottle() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.Throttle_Field = new Throttle();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Throttle_Field.Throttles.length + 5 + 40;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentThrottle(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)81);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.CircuitCode);
        this.packInt(byteBuffer, this.Throttle_Field.GenCounter);
        this.packVariable(byteBuffer, this.Throttle_Field.Throttles, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.CircuitCode = this.unpackInt(byteBuffer);
        this.Throttle_Field.GenCounter = this.unpackInt(byteBuffer);
        this.Throttle_Field.Throttles = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;
    }
    
    public static class Throttle
    {
        public int GenCounter;
        public byte[] Throttles;
    }
}
