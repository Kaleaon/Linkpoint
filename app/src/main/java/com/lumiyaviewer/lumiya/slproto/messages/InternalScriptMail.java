package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class InternalScriptMail extends SLMessage {
    public DataBlock DataBlock_Field = new DataBlock();

    public static class DataBlock {
        public byte[] Body;
        public byte[] From;
        public byte[] Subject;
        public UUID To;
    }

    public InternalScriptMail() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.DataBlock_Field.From.length + 1 + 16 + 1 + this.DataBlock_Field.Subject.length + 2 + this.DataBlock_Field.Body.length + 2;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInternalScriptMail(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 16);
        packVariable(byteBuffer, this.DataBlock_Field.From, 1);
        packUUID(byteBuffer, this.DataBlock_Field.To);
        packVariable(byteBuffer, this.DataBlock_Field.Subject, 1);
        packVariable(byteBuffer, this.DataBlock_Field.Body, 2);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.DataBlock_Field.From = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.To = unpackUUID(byteBuffer);
        this.DataBlock_Field.Subject = unpackVariable(byteBuffer, 1);
        this.DataBlock_Field.Body = unpackVariable(byteBuffer, 2);
    }
}
