// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportLureRequest extends SLMessage
{
    public Info Info_Field;
    
    public TeleportLureRequest() {
        this.zeroCoded = false;
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 56;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportLureRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)71);
        this.packUUID(byteBuffer, this.Info_Field.AgentID);
        this.packUUID(byteBuffer, this.Info_Field.SessionID);
        this.packUUID(byteBuffer, this.Info_Field.LureID);
        this.packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Info_Field.AgentID = this.unpackUUID(byteBuffer);
        this.Info_Field.SessionID = this.unpackUUID(byteBuffer);
        this.Info_Field.LureID = this.unpackUUID(byteBuffer);
        this.Info_Field.TeleportFlags = this.unpackInt(byteBuffer);
    }
    
    public static class Info
    {
        public UUID AgentID;
        public UUID LureID;
        public UUID SessionID;
        public int TeleportFlags;
    }
}
