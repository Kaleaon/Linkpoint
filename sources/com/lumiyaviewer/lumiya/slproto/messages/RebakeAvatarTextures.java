package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class RebakeAvatarTextures extends SLMessage {
    public TextureData TextureData_Field = new TextureData();

    public static class TextureData {
        public UUID TextureID;
    }

    public RebakeAvatarTextures() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 20;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRebakeAvatarTextures(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 87);
        packUUID(byteBuffer, this.TextureData_Field.TextureID);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TextureData_Field.TextureID = unpackUUID(byteBuffer);
    }
}
