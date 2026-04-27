// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AssetUploadRequest extends SLMessage
{
    public AssetBlock AssetBlock_Field;
    
    public AssetUploadRequest() {
        this.zeroCoded = false;
        this.AssetBlock_Field = new AssetBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.AssetBlock_Field.AssetData.length + 21 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAssetUploadRequest(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)77);
        this.packUUID(byteBuffer, this.AssetBlock_Field.TransactionID);
        this.packByte(byteBuffer, (byte)this.AssetBlock_Field.Type);
        this.packBoolean(byteBuffer, this.AssetBlock_Field.Tempfile);
        this.packBoolean(byteBuffer, this.AssetBlock_Field.StoreLocal);
        this.packVariable(byteBuffer, this.AssetBlock_Field.AssetData, 2);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AssetBlock_Field.TransactionID = this.unpackUUID(byteBuffer);
        this.AssetBlock_Field.Type = this.unpackByte(byteBuffer);
        this.AssetBlock_Field.Tempfile = this.unpackBoolean(byteBuffer);
        this.AssetBlock_Field.StoreLocal = this.unpackBoolean(byteBuffer);
        this.AssetBlock_Field.AssetData = this.unpackVariable(byteBuffer, 2);
    }
    
    public static class AssetBlock
    {
        public byte[] AssetData;
        public boolean StoreLocal;
        public boolean Tempfile;
        public UUID TransactionID;
        public int Type;
    }
}
