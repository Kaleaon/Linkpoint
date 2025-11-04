// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class SoundTrigger extends SLMessage
{
    public SoundData SoundData_Field;
    
    public SoundTrigger() {
        this.zeroCoded = false;
        this.SoundData_Field = new SoundData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 89;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleSoundTrigger(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)29);
        this.packUUID(byteBuffer, this.SoundData_Field.SoundID);
        this.packUUID(byteBuffer, this.SoundData_Field.OwnerID);
        this.packUUID(byteBuffer, this.SoundData_Field.ObjectID);
        this.packUUID(byteBuffer, this.SoundData_Field.ParentID);
        this.packLong(byteBuffer, this.SoundData_Field.Handle);
        this.packLLVector3(byteBuffer, this.SoundData_Field.Position);
        this.packFloat(byteBuffer, this.SoundData_Field.Gain);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.SoundData_Field.SoundID = this.unpackUUID(byteBuffer);
        this.SoundData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.SoundData_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.SoundData_Field.ParentID = this.unpackUUID(byteBuffer);
        this.SoundData_Field.Handle = this.unpackLong(byteBuffer);
        this.SoundData_Field.Position = this.unpackLLVector3(byteBuffer);
        this.SoundData_Field.Gain = this.unpackFloat(byteBuffer);
    }
    
    public static class SoundData
    {
        public float Gain;
        public long Handle;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID ParentID;
        public LLVector3 Position;
        public UUID SoundID;
    }
}
