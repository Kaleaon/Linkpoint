// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AttachedSoundGainChange extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public AttachedSoundGainChange() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 22;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAttachedSoundGainChange(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)14);
        this.packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        this.packFloat(byteBuffer, this.DataBlock_Field.Gain);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.Gain = this.unpackFloat(byteBuffer);
    }
    
    public static class DataBlock
    {
        public float Gain;
        public UUID ObjectID;
    }
}
