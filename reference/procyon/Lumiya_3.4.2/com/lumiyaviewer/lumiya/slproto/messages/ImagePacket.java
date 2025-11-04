// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ImagePacket extends SLMessage
{
    public ImageData ImageData_Field;
    public ImageID ImageID_Field;
    
    public ImagePacket() {
        this.zeroCoded = false;
        this.ImageID_Field = new ImageID();
        this.ImageData_Field = new ImageData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ImageData_Field.Data.length + 2 + 19;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleImagePacket(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)10);
        this.packUUID(byteBuffer, this.ImageID_Field.ID);
        this.packShort(byteBuffer, (short)this.ImageID_Field.Packet);
        this.packVariable(byteBuffer, this.ImageData_Field.Data, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = this.unpackUUID(byteBuffer);
        this.ImageID_Field.Packet = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.ImageData_Field.Data = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class ImageData
    {
        public byte[] Data;
    }
    
    public static class ImageID
    {
        public UUID ID;
        public int Packet;
    }
}
