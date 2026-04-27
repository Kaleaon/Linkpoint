// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AvatarPickerRequestBackend extends SLMessage
{
    public AgentData AgentData_Field;
    public Data Data_Field;
    
    public AvatarPickerRequestBackend() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Data_Field = new Data();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Data_Field.Name.length + 1 + 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAvatarPickerRequestBackend(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)27);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.QueryID);
        this.packByte(byteBuffer, (byte)this.AgentData_Field.GodLevel);
        this.packVariable(byteBuffer, this.Data_Field.Name, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GodLevel = (this.unpackByte(byteBuffer) & 0xFF);
        this.Data_Field.Name = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public int GodLevel;
        public UUID QueryID;
        public UUID SessionID;
    }
    
    public static class Data
    {
        public byte[] Name;
    }
}
