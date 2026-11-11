package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class LoadURL extends SLMessage {
    public Data Data_Field;

    /* loaded from: classes.dex */
    public static class Data {
        public byte[] Message;
        public UUID ObjectID;
        public byte[] ObjectName;
        public UUID OwnerID;
        public boolean OwnerIsGroup;
        public byte[] URL;
    }

    public LoadURL() {
        this.zeroCoded = false;
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.Data_Field.ObjectName.length + 1 + 16 + 16 + 1 + 1 + this.Data_Field.Message.length + 1 + this.Data_Field.URL.length + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLoadURL(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -62);
        packVariable(byteBuffer, this.Data_Field.ObjectName, 1);
        packUUID(byteBuffer, this.Data_Field.ObjectID);
        packUUID(byteBuffer, this.Data_Field.OwnerID);
        packBoolean(byteBuffer, this.Data_Field.OwnerIsGroup);
        packVariable(byteBuffer, this.Data_Field.Message, 1);
        packVariable(byteBuffer, this.Data_Field.URL, 1);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectName = unpackVariable(byteBuffer, 1);
        this.Data_Field.ObjectID = unpackUUID(byteBuffer);
        this.Data_Field.OwnerID = unpackUUID(byteBuffer);
        this.Data_Field.OwnerIsGroup = unpackBoolean(byteBuffer);
        this.Data_Field.Message = unpackVariable(byteBuffer, 1);
        this.Data_Field.URL = unpackVariable(byteBuffer, 1);
    }
}
