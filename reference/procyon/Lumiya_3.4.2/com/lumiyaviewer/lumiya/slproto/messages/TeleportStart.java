// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class TeleportStart extends SLMessage
{
    public Info Info_Field;
    
    public TeleportStart() {
        this.zeroCoded = false;
        this.Info_Field = new Info();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 8;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleTeleportStart(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)73);
        this.packInt(byteBuffer, this.Info_Field.TeleportFlags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.Info_Field.TeleportFlags = this.unpackInt(byteBuffer);
    }
    
    public static class Info
    {
        public int TeleportFlags;
    }
}
