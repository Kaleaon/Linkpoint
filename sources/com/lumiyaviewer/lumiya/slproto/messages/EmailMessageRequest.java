package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class EmailMessageRequest extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public byte[] FromAddress;
        public UUID ObjectID;
        public byte[] Subject;
    }

    public EmailMessageRequest() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DataBlock_Field.FromAddress.length + 17 + 1 + this.DataBlock_Field.Subject.length + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEmailMessageRequest(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 79);
        packUUID(byteBuffer, this.DataBlock_Field.ObjectID);
        packVariable(byteBuffer, this.DataBlock_Field.FromAddress, 1);
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.ObjectID = unpackUUID(byteBuffer);
        this.DataBlock_Field.FromAddress = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1);
    }
}
