// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AgentHeightWidth extends SLMessage
{
    public AgentData AgentData_Field;
    public HeightWidthBlock HeightWidthBlock_Field;
    
    public AgentHeightWidth() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.HeightWidthBlock_Field = new HeightWidthBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 48;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAgentHeightWidth(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)83);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packInt(byteBuffer, this.AgentData_Field.CircuitCode);
        this.packInt(byteBuffer, this.HeightWidthBlock_Field.GenCounter);
        this.packShort(byteBuffer, (short)this.HeightWidthBlock_Field.Height);
        this.packShort(byteBuffer, (short)this.HeightWidthBlock_Field.Width);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.CircuitCode = this.unpackInt(byteBuffer);
        this.HeightWidthBlock_Field.GenCounter = this.unpackInt(byteBuffer);
        this.HeightWidthBlock_Field.Height = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.HeightWidthBlock_Field.Width = (this.unpackShort(byteBuffer) & 0xFFFF);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int CircuitCode;
        public UUID SessionID;
    }
    
    public static class HeightWidthBlock
    {
        public int GenCounter;
        public int Height;
        public int Width;
    }
}
