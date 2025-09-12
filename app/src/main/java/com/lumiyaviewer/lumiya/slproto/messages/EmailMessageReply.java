package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EmailMessageReply extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public byte[] Data;
        public byte[] FromAddress;
        public byte[] MailFilter;
        public int More;
        public UUID ObjectID;
        public byte[] Subject;
        public int Time;
    }

    public EmailMessageReply() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DataBlock_Field.FromAddress.length + 25 + 1 + this.DataBlock_Field.Subject.length + 2 + this.DataBlock_Field.Data.length + 1 + this.DataBlock_Field.MailFilter.length + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEmailMessageReply(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 80);
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        packInt(byteBuffer, this.DataBlock_Field.More);
        packInt(byteBuffer, this.DataBlock_Field.Time);
        packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1);
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1);
        packVariable(byteBuffer, this.DataBlock_Field.Data, 2);
        packVariable(byteBuffer, this.DataBlock_Field.MailFilter, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer);
        this.DataBlock_Field.More = unpackInt(byteBuffer);
        this.DataBlock_Field.Time = unpackInt(byteBuffer);
        this.DataBlock_Field.FromAddress = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Data = unpackVariable(byteBuffer, 2);
        this.DataBlock_Field.MailFilter = unpackVariable(byteBuffer, 1);
    }
}
