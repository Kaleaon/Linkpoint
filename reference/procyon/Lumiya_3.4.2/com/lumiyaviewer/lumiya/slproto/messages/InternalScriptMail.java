// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class InternalScriptMail extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public InternalScriptMail() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.From.length + 1 + 16 + 1 + this.DataBlock_Field.Subject.length + 2 + this.DataBlock_Field.Body.length + 2;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleInternalScriptMail(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)16);
        this.packVariable(byteBuffer, this.DataBlock_Field.From, 1);
        this.packUUID(byteBuffer, this.DataBlock_Field.To);
        this.packVariable(byteBuffer, this.DataBlock_Field.Subject, 1);
        this.packVariable(byteBuffer, this.DataBlock_Field.Body, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.From = this.unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.To = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.Subject = this.unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Body = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class DataBlock
    {
        public byte[] Body;
        public byte[] From;
        public byte[] Subject;
        public UUID To;
    }
}
