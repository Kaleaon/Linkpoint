package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class AssetUploadComplete extends SLMessage {
    public AssetBlock AssetBlock_Field = new AssetBlock();

    public static class AssetBlock {
        public boolean Success;
        public int Type;
        public UUID UUID;
    }

    public AssetUploadComplete() {
        this.zeroCoded = false;
    }

    public int CalcPayloadSize() {
        return 22;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAssetUploadComplete(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 78);
        packUUID(byteBuffer, this.AssetBlock_Field.UUID);
        packByte(byteBuffer, (byte) this.AssetBlock_Field.Type);
        packBoolean(byteBuffer, this.AssetBlock_Field.Success);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AssetBlock_Field.UUID = unpackUUID(byteBuffer);
        this.AssetBlock_Field.Type = unpackByte(byteBuffer);
        this.AssetBlock_Field.Success = unpackBoolean(byteBuffer);
    }
}
