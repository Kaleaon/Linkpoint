// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportLocal extends SLMessage
{
    public Info Info_Field;
    
    public TeleportLocal() {
        this.zeroCoded = false;
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 52;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportLocal(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)64);
        this.packUUID(byteBuffer, this.Info_Field.AgentID);
        this.packInt(byteBuffer, this.Info_Field.LocationID);
        this.packLLVector3(byteBuffer, this.Info_Field.Position);
        this.packLLVector3(byteBuffer, this.Info_Field.LookAt);
        this.packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Info_Field.LocationID = this.unpackInt(byteBuffer);
        this.Info_Field.Position = this.unpackLLVector3(byteBuffer);
        this.Info_Field.LookAt = this.unpackLLVector3(byteBuffer);
        this.Info_Field.TeleportFlags = this.unpackInt(byteBuffer);
    }
    
    public static class Info
    {
        public UUID AgentID;
        public int LocationID;
        public LLVector3 LookAt;
        public LLVector3 Position;
        public int TeleportFlags;
    }
}
