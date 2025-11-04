// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class RequestInventoryAsset extends SLMessage
{
    public QueryData QueryData_Field;
    
    public RequestInventoryAsset() {
        this.zeroCoded = false;
        this.QueryData_Field = new QueryData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 68;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleRequestInventoryAsset(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)1);
        byteBuffer.put((byte)26);
        this.packUUID(byteBuffer, this.QueryData_Field.QueryID);
        this.packUUID(byteBuffer, this.QueryData_Field.AgentID);
        this.packUUID(byteBuffer, this.QueryData_Field.OwnerID);
        this.packUUID(byteBuffer, this.QueryData_Field.ItemID);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.QueryData_Field.ItemID = this.unpackUUID(byteBuffer);
    }
    
    public static class QueryData
    {
        public UUID AgentID;
        public UUID ItemID;
        public UUID OwnerID;
        public UUID QueryID;
    }
}
