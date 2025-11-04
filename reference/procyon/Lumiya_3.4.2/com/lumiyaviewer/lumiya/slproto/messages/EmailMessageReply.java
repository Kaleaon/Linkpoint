// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class EmailMessageReply extends SLMessage
{
    public DataBlock DataBlock_Field;
    
    public EmailMessageReply() {
        this.zeroCoded = false;
        this.DataBlock_Field = new DataBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.DataBlock_Field.FromAddress.length + 25 + 1 + this.DataBlock_Field.Subject.length + 2 + this.DataBlock_Field.Data.length + 1 + this.DataBlock_Field.MailFilter.length + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleEmailMessageReply(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)80);
        this.packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        this.packInt(byteBuffer, this.DataBlock_Field.More);
        this.packInt(byteBuffer, this.DataBlock_Field.Time);
        this.packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1);
        this.packVariable(byteBuffer, this.DataBlock_Field.Subject, 1);
        this.packVariable(byteBuffer, this.DataBlock_Field.Data, 2);
        this.packVariable(byteBuffer, this.DataBlock_Field.MailFilter, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.DataBlock_Field.More = this.unpackInt(byteBuffer);
        this.DataBlock_Field.Time = this.unpackInt(byteBuffer);
        this.DataBlock_Field.FromAddress = this.unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Subject = this.unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Data = this.unpackVariable(byteBuffer, 2);
        this.DataBlock_Field.MailFilter = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class DataBlock
    {
        public byte[] Data;
        public byte[] FromAddress;
        public byte[] MailFilter;
        public int More;
        public UUID ObjectID;
        public byte[] Subject;
        public int Time;
    }
}
