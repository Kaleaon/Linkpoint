package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class InventoryAssetResponse extends SLMessage {
    public QueryData QueryData_Field;

    /* loaded from: classes.dex */
    public static class QueryData {
        public UUID AssetID;
        public boolean IsReadable;
        public UUID QueryID;
    }

    public InventoryAssetResponse() {
        this.zeroCoded = false;
        this.QueryData_Field = new QueryData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 37;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleInventoryAssetResponse(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put(Ascii.ESC);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        packUUID(byteBuffer, this.QueryData_Field.AssetID);
        packBoolean(byteBuffer, this.QueryData_Field.IsReadable);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        this.QueryData_Field.AssetID = unpackUUID(byteBuffer);
        this.QueryData_Field.IsReadable = unpackBoolean(byteBuffer);
    }
}
