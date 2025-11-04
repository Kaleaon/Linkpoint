// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelMediaUpdate extends SLMessage
{
    public DataBlockExtended DataBlockExtended_Field;
    public DataBlock DataBlock_Field;
    
    public ParcelMediaUpdate() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
        this.DataBlockExtended_Field = new DataBlockExtended();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.MediaURL.length + 1 + 16 + 1 + 4 + (this.DataBlockExtended_Field.MediaType.length + 1 + 1 + this.DataBlockExtended_Field.MediaDesc.length + 4 + 4 + 1);
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelMediaUpdate(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)(-92));
        this.packVariable(byteBuffer, this.DataBlock_Field.MediaURL, 1);
        this.packUUID(byteBuffer, this.DataBlock_Field.MediaID);
        this.packByte(byteBuffer, (byte)this.DataBlock_Field.MediaAutoScale);
        this.packVariable(byteBuffer, this.DataBlockExtended_Field.MediaType, 1);
        this.packVariable(byteBuffer, this.DataBlockExtended_Field.MediaDesc, 1);
        this.packInt(byteBuffer, this.DataBlockExtended_Field.MediaWidth);
        this.packInt(byteBuffer, this.DataBlockExtended_Field.MediaHeight);
        this.packByte(byteBuffer, (byte)this.DataBlockExtended_Field.MediaLoop);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.MediaURL = this.unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.MediaID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.MediaAutoScale = (this.unpackByte(byteBuffer) & 0xFF);
        this.DataBlockExtended_Field.MediaType = this.unpackVariable(byteBuffer, 1);
        this.DataBlockExtended_Field.MediaDesc = this.unpackVariable(byteBuffer, 1);
        this.DataBlockExtended_Field.MediaWidth = this.unpackInt(byteBuffer);
        this.DataBlockExtended_Field.MediaHeight = this.unpackInt(byteBuffer);
        this.DataBlockExtended_Field.MediaLoop = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class DataBlock
    {
        public int MediaAutoScale;
        public UUID MediaID;
        public byte[] MediaURL;
    }
    
    public static class DataBlockExtended
    {
        public byte[] MediaDesc;
        public int MediaHeight;
        public int MediaLoop;
        public byte[] MediaType;
        public int MediaWidth;
    }
}
