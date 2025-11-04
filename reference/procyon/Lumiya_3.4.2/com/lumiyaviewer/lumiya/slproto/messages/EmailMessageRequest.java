// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EmailMessageRequest extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public EmailMessageRequest() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.FromAddress.length + 17 + 1 + this.DataBlock_Field.Subject.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEmailMessageRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)79);
        this.packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        this.packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1);
        this.packVariable(byteBuffer, this.DataBlock_Field.Subject, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.FromAddress = this.unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Subject = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class DataBlock
    {
        public byte[] FromAddress;
        public UUID ObjectID;
        public byte[] Subject;
    }
}
