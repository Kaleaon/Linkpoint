// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportProgress extends SLMessage
{
    public AgentData AgentData_Field;
    public Info Info_Field;
    
    public TeleportProgress() {
        this.zeroCoded = false;
        this.AgentData_Field = new AgentData();
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.Info_Field.Message.length + 5 + 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportProgress(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)66);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packInt(byteBuffer, this.Info_Field.TeleportFlags);
        this.packVariable(byteBuffer, this.Info_Field.Message, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Info_Field.TeleportFlags = this.unpackInt(byteBuffer);
        this.Info_Field.Message = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
    }
    
    public static class Info
    {
        public byte[] Message;
        public int TeleportFlags;
    }
}
