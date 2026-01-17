package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class ImageNotInDatabase extends SLMessage {
    public ImageID ImageID_Field;

    /* loaded from: classes.dex */
    public static class ImageID {
        public UUID ID;
    }

    public ImageNotInDatabase() {
        this.zeroCoded = false;
        this.ImageID_Field = new ImageID();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleImageNotInDatabase(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 86);
        packUUID(byteBuffer, this.ImageID_Field.ID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ImageID_Field.ID = unpackUUID(byteBuffer);
    }
}
