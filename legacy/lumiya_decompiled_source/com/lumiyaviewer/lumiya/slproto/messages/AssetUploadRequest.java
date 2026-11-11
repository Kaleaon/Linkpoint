package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class AssetUploadRequest extends SLMessage {
    public AssetBlock AssetBlock_Field;

    /* loaded from: classes.dex */
    public static class AssetBlock {
        public byte[] AssetData;
        public boolean StoreLocal;
        public boolean Tempfile;
        public UUID TransactionID;
        public int Type;
    }

    public AssetUploadRequest() {
        this.zeroCoded = false;
        this.AssetBlock_Field = new AssetBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return this.AssetBlock_Field.AssetData.length + 21 + 4;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAssetUploadRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 77);
        packUUID(byteBuffer, this.AssetBlock_Field.TransactionID);
        packByte(byteBuffer, (byte) this.AssetBlock_Field.Type);
        packBoolean(byteBuffer, this.AssetBlock_Field.Tempfile);
        packBoolean(byteBuffer, this.AssetBlock_Field.StoreLocal);
        packVariable(byteBuffer, this.AssetBlock_Field.AssetData, 2);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AssetBlock_Field.TransactionID = unpackUUID(byteBuffer);
        this.AssetBlock_Field.Type = unpackByte(byteBuffer);
        this.AssetBlock_Field.Tempfile = unpackBoolean(byteBuffer);
        this.AssetBlock_Field.StoreLocal = unpackBoolean(byteBuffer);
        this.AssetBlock_Field.AssetData = unpackVariable(byteBuffer, 2);
    }
}
