package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class DerezContainer extends SLMessage {
    public Data Data_Field;

    /* loaded from: classes.dex */
    public static class Data {
        public boolean Delete;
        public UUID ObjectID;
    }

    public DerezContainer() {
        this.zeroCoded = true;
        this.Data_Field = new Data();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 21;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDerezContainer(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 104);
        packUUID(byteBuffer, this.Data_Field.ObjectID);
        packBoolean(byteBuffer, this.Data_Field.Delete);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer);
        this.Data_Field.Delete = unpackBoolean(byteBuffer);
    }
}
