// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AttachedSound extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public AttachedSound() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 55;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAttachedSound(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)13);
        this.packUUID(byteBuffer, this.DataBlock_Field.SoundID);
        this.packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        this.packUUID(byteBuffer, this.DataBlock_Field.OwnerID);
        this.packFloat(byteBuffer, this.DataBlock_Field.Gain);
        this.packByte(byteBuffer, (byte)this.DataBlock_Field.Flags);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.SoundID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.Gain = this.unpackFloat(byteBuffer);
        this.DataBlock_Field.Flags = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class DataBlock
    {
        public int Flags;
        public float Gain;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID SoundID;
    }
}
