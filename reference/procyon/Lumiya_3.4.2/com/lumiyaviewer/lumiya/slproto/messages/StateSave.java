// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class StateSave extends SLMessage
{
    public AgentData AgentData_Field;
    public DataBlock DataBlock_Field;
    
    public StateSave() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.Filename.length + 1 + 36;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleStateSave(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)127);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packVariable(byteBuffer, this.DataBlock_Field.Filename, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.Filename = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class DataBlock
    {
        public byte[] Filename;
    }
}
