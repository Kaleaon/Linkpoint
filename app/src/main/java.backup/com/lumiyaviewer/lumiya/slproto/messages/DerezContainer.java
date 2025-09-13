package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class DerezContainer extends SLMessage {
    public Data Data_Field = new Data();

    public static class Data {
        public boolean Delete;
        public UUID ObjectID;
    }

    public DerezContainer() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 21;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleDerezContainer(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 104);
        packUUID(byteBuffer, this.Data_Field.ObjectID);
        packBoolean(byteBuffer, this.Data_Field.Delete);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.Data_Field.ObjectID = unpackUUID(byteBuffer);
        this.Data_Field.Delete = unpackBoolean(byteBuffer);
    }
}
