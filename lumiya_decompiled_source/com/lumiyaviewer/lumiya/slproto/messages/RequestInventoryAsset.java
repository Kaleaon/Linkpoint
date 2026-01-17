package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
/* loaded from: classes.dex */
public class RequestInventoryAsset extends SLMessage {
    public QueryData QueryData_Field;

    /* loaded from: classes.dex */
    public static class QueryData {
        public UUID AgentID;
        public UUID ItemID;
        public UUID OwnerID;
        public UUID QueryID;
    }

    public RequestInventoryAsset() {
        this.zeroCoded = false;
        this.QueryData_Field = new QueryData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 68;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRequestInventoryAsset(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put(Ascii.SUB);
        packUUID(byteBuffer, this.QueryData_Field.QueryID);
        packUUID(byteBuffer, this.QueryData_Field.AgentID);
        packUUID(byteBuffer, this.QueryData_Field.OwnerID);
        packUUID(byteBuffer, this.QueryData_Field.ItemID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.QueryData_Field.QueryID = unpackUUID(byteBuffer);
        this.QueryData_Field.AgentID = unpackUUID(byteBuffer);
        this.QueryData_Field.OwnerID = unpackUUID(byteBuffer);
        this.QueryData_Field.ItemID = unpackUUID(byteBuffer);
    }
}
