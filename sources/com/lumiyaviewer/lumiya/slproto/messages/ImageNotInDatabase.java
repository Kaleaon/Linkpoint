package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ImageNotInDatabase extends SLMessage {
    public ImageID ImageID_Field = new ImageID();

    public static class ImageID {
        public UUID ID;
    }

    public ImageNotInDatabase() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImageNotInDatabase(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 86);
        packUUID(byteBuffer, this.ImageID_Field.ID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer);
    }
}
