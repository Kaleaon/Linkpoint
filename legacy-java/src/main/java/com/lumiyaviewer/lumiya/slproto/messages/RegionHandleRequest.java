package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

/* loaded from: classes.dex */
public class RegionHandleRequest extends SLMessage {
    public RequestBlock RequestBlock_Field;

    /* loaded from: classes.dex */
    public static class RequestBlock {
        public UUID RegionID;
    }

    public RegionHandleRequest() {
        this.zeroCoded = false;
        this.RequestBlock_Field = new RequestBlock();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        return 20;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleRegionHandleRequest(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 53);
        packUUID(byteBuffer, this.RequestBlock_Field.RegionID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.RequestBlock_Field.RegionID = unpackUUID(byteBuffer);
    }
}
