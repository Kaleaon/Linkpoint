// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class AssetUploadComplete extends SLMessage
{
    public AssetBlock AssetBlock_Field;
    
    public AssetUploadComplete() {
        this.zeroCoded = false;
        this.AssetBlock_Field = new AssetBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 22;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleAssetUploadComplete(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)78);
        this.packUUID(byteBuffer, this.AssetBlock_Field.UUID);
        this.packByte(byteBuffer, (byte)this.AssetBlock_Field.Type);
        this.packBoolean(byteBuffer, this.AssetBlock_Field.Success);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AssetBlock_Field.UUID = this.unpackUUID(byteBuffer);
        this.AssetBlock_Field.Type = this.unpackByte(byteBuffer);
        this.AssetBlock_Field.Success = this.unpackBoolean(byteBuffer);
    }
    
    public static class AssetBlock
    {
        public boolean Success;
        public int Type;
        public UUID UUID;
    }
}
