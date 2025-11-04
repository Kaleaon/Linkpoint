// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class InventoryAssetResponse extends SLMessage
{
    public QueryData QueryData_Field;
    
    public InventoryAssetResponse() {
        this.zeroCoded = false;
        this.QueryData_Field = new QueryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 37;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleInventoryAssetResponse(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)27);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        this.packUUID(byteBuffer, this.QueryData_Field.AssetID);
        this.packBoolean(byteBuffer, this.QueryData_Field.IsReadable);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.AssetID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.IsReadable = this.unpackBoolean(byteBuffer);
    }
    
    public static class QueryData
    {
        public UUID AssetID;
        public boolean IsReadable;
        public UUID QueryID;
    }
}
