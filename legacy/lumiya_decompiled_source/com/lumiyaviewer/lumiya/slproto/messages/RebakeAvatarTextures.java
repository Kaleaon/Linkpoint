package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class RebakeAvatarTextures extends SLMessage {
    public TextureData TextureData_Field;

    /* loaded from: classes.dex */
    public static class TextureData {
        public UUID TextureID;
    }

    public RebakeAvatarTextures() {
        this.zeroCoded = false;
        this.TextureData_Field = new TextureData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRebakeAvatarTextures(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) 87);
        packUUID(byteBuffer, this.TextureData_Field.TextureID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.TextureData_Field.TextureID = unpackUUID(byteBuffer);
    }
}
