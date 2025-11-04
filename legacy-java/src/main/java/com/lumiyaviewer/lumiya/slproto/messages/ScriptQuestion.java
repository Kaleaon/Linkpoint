package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class ScriptQuestion extends SLMessage {
    public Data Data_Field;

    /* loaded from: classes.dex */
    public static class Data {
        public UUID ItemID;
        public byte[] ObjectName;
        public byte[] ObjectOwner;
        public int Questions;
        public UUID TaskID;
    }

    public ScriptQuestion() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 33 + 1 + this.Data_Field.ObjectOwner.length + 4 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleScriptQuestion(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -68);
        packUUID(byteBuffer, this.Data_Field.TaskID);
        packUUID(byteBuffer, this.Data_Field.ItemID);
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        packVariable(byteBuffer, this.Data_Field.ObjectOwner, 1);
        packInt(byteBuffer, this.Data_Field.Questions);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.TaskID = unpackUUID(byteBuffer);
        this.Data_Field.ItemID = unpackUUID(byteBuffer);
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1);
        this.Data_Field.ObjectOwner = unpackVariable(byteBuffer, 1);
        this.Data_Field.Questions = unpackInt(byteBuffer);
    }
}
