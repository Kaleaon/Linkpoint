// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class PreloadSound extends SLMessage
{
    public ArrayList<DataBlock> DataBlock_Fields;
    
    public PreloadSound() {
        this.DataBlock_Fields = new ArrayList<DataBlock>();
        this.zeroCoded = false;
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Fields.size() * 48 + 3;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandlePreloadSound(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)15);
        byteBuffer.put((byte)this.DataBlock_Fields.size());
        for (final DataBlock dataBlock : this.DataBlock_Fields) {
            this.packUUID(byteBuffer, dataBlock.ObjectID);
            this.packUUID(byteBuffer, dataBlock.OwnerID);
            this.packUUID(byteBuffer, dataBlock.SoundID);
        }
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        final byte value = byteBuffer.get();
        for (int i = 0; i < (value & 0xFF); ++i) {
            final DataBlock e = new DataBlock();
            e.ObjectID = this.unpackUUID(byteBuffer);
            e.OwnerID = this.unpackUUID(byteBuffer);
            e.SoundID = this.unpackUUID(byteBuffer);
            this.DataBlock_Fields.add(e);
        }
    }
    
    public static class DataBlock
    {
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID SoundID;
    }
}
