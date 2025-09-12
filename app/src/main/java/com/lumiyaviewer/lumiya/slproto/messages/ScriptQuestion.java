package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ScriptQuestion extends SLMessage {
    public Data Data_Field = new Data();

    public static class Data {
        public UUID ItemID;
        public byte[] ObjectName;
        public byte[] ObjectOwner;
        public int Questions;
        public UUID TaskID;
    }

    public ScriptQuestion() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 33 + 1 + this.Data_Field.ObjectOwner.length + 4 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptQuestion(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -68);
        packUUID(byteBuffer, this.Data_Field.TaskID);
        packUUID(byteBuffer, this.Data_Field.ItemID);
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        packVariable(byteBuffer, this.Data_Field.ObjectOwner, 1);
        packInt(byteBuffer, this.Data_Field.Questions);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.TaskID = unpackUUID(byteBuffer);
        this.Data_Field.ItemID = unpackUUID(byteBuffer);
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1);
        this.Data_Field.ObjectOwner = unpackVariable(byteBuffer, 1);
        this.Data_Field.Questions = unpackInt(byteBuffer);
    }
}
