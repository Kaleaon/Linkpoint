// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RebakeAvatarTextures extends SLMessage
{
    public TextureData TextureData_Field;
    
    public RebakeAvatarTextures() {
        this.zeroCoded = false;
        this.TextureData_Field = new TextureData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRebakeAvatarTextures(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)87);
        this.packUUID(byteBuffer, this.TextureData_Field.TextureID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.TextureData_Field.TextureID = this.unpackUUID(byteBuffer);
    }
    
    public static class TextureData
    {
        public UUID TextureID;
    }
}
