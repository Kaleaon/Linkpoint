// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AddCircuitCode extends SLMessage
{
    public CircuitCode CircuitCode_Field;
    
    public AddCircuitCode() {
        this.zeroCoded = false;
        this.CircuitCode_Field = new CircuitCode();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 40;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAddCircuitCode(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)2);
        this.packInt(byteBuffer, this.CircuitCode_Field.Code);
        this.packUUID(byteBuffer, this.CircuitCode_Field.SessionID);
        this.packUUID(byteBuffer, this.CircuitCode_Field.AgentID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.CircuitCode_Field.Code = this.unpackInt(byteBuffer);
        this.CircuitCode_Field.SessionID = this.unpackUUID(byteBuffer);
        this.CircuitCode_Field.AgentID = this.unpackUUID(byteBuffer);
    }
    
    public static class CircuitCode
    {
        public UUID AgentID;
        public int Code;
        public UUID SessionID;
    }
}
