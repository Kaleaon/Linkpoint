package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ClearFollowCamProperties extends SLMessage {
    public ObjectData ObjectData_Field = new ObjectData();

    public static class ObjectData {
        public UUID ObjectID;
    }

    public ClearFollowCamProperties() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleClearFollowCamProperties(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -96);
        packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ObjectData_Field.ObjectID = unpackUUID(byteBuffer);
    }
}
