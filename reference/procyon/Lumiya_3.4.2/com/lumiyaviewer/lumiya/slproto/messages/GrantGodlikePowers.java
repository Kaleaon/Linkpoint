// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class GrantGodlikePowers extends SLMessage
{
    public AgentData AgentData_Field;
    public GrantData GrantData_Field;
    
    public GrantGodlikePowers() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.GrantData_Field = new GrantData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 53;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleGrantGodlikePowers(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)2);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packByte(byteBuffer, (byte)this.GrantData_Field.GodLevel);
        this.packUUID(byteBuffer, this.GrantData_Field.Token);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.GrantData_Field.GodLevel = (this.unpackByte(byteBuffer) & 0xFF);
        this.GrantData_Field.Token = this.unpackUUID(byteBuffer);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID SessionID;
    }
    
    public static class GrantData
    {
        public int GodLevel;
        public UUID Token;
    }
}
