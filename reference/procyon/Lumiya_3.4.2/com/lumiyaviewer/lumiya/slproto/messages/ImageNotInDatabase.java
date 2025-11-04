// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ImageNotInDatabase extends SLMessage
{
    public ImageID ImageID_Field;
    
    public ImageNotInDatabase() {
        this.zeroCoded = false;
        this.ImageID_Field = new ImageID();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 20;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleImageNotInDatabase(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)86);
        this.packUUID(byteBuffer, this.ImageID_Field.ID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = this.unpackUUID(byteBuffer);
    }
    
    public static class ImageID
    {
        public UUID ID;
    }
}
