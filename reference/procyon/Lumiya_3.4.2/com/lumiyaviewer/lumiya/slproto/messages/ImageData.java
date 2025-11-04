// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ImageData extends SLMessage
{
    public ImageDataData ImageDataData_Field;
    public ImageID ImageID_Field;
    
    public ImageData() {
        this.zeroCoded = false;
        this.ImageID_Field = new ImageID();
        this.ImageDataData_Field = new ImageDataData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ImageDataData_Field.Data.length + 2 + 24;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleImageData(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)9);
        this.packUUID(byteBuffer, this.ImageID_Field.ID);
        this.packByte(byteBuffer, (byte)this.ImageID_Field.Codec);
        this.packInt(byteBuffer, this.ImageID_Field.Size);
        this.packShort(byteBuffer, (short)this.ImageID_Field.Packets);
        this.packVariable(byteBuffer, this.ImageDataData_Field.Data, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = this.unpackUUID(byteBuffer);
        this.ImageID_Field.Codec = (this.unpackByte(byteBuffer) & 0xFF);
        this.ImageID_Field.Size = this.unpackInt(byteBuffer);
        this.ImageID_Field.Packets = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.ImageDataData_Field.Data = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class ImageDataData
    {
        public byte[] Data;
    }
    
    public static class ImageID
    {
        public int Codec;
        public UUID ID;
        public int Packets;
        public int Size;
    }
}
